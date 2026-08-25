package org.naho.speech.llm.conversation.usecase;

import org.naho.file.constant.FileAccessStatus;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.File;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.conversation.command.SendAudioMessageCommand;
import org.naho.speech.llm.conversation.command.SendTextMessageCommand;
import org.naho.speech.llm.conversation.command.SpeakingSessionMessageCommand;
import org.naho.speech.llm.conversation.command.StartSpeakingConversationCommand;
import org.naho.speech.llm.conversation.constant.AiMessageField;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.internal.ParsedAiReply;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionMessageResultMapper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.conversation.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.conversation.port.out.AiChatPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionMessageRepositoryPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.port.out.SpeechToTextPort;
import org.naho.speech.llm.conversation.result.ChatResult;
import org.naho.speech.llm.conversation.result.SpeechToTextResult;
import org.naho.speech.llm.conversation.validator.SpeakingSessionValidator;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;
import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SenderType;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpeakingSessionUseCase implements SpeakingSessionInputPort {

    private final AiChatPort aiChatPort;
    private final SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort;
    private final SpeechToTextPort speechToTextPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final UploadFileInputPort uploadFileInputPort;
    private final SpeakingSessionValidator speakingSessionValidator;
    private final SpeakingSessionHelper speakingSessionHelper;
    private final SpeakingSessionResultMapper speakingSessionResultMapper;
    private final TransactionPort transactionPort;
    private final SpeakingSessionMessageResultMapper speakingSessionMessageResultMapper;

    public SpeakingSessionUseCase(
            AiChatPort aiChatPort,
            SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort,
            SpeechToTextPort speechToTextPort,
            PersonaRepositoryPort personaRepositoryPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            SpeakingSessionValidator speakingSessionValidator,
            SpeakingSessionHelper speakingSessionHelper,
            SpeakingSessionResultMapper speakingSessionResultMapper,
            TransactionPort transactionPort,
            SpeakingSessionMessageResultMapper speakingSessionMessageResultMapper
    ) {
        this.aiChatPort = aiChatPort;
        this.speakingSessionMessageRepositoryPort = speakingSessionMessageRepositoryPort;
        this.speechToTextPort = speechToTextPort;
        this.personaRepositoryPort = personaRepositoryPort;
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.uploadFileInputPort = uploadFileInputPort;
        this.speakingSessionValidator = speakingSessionValidator;
        this.speakingSessionHelper = speakingSessionHelper;
        this.speakingSessionResultMapper = speakingSessionResultMapper;
        this.transactionPort = transactionPort;
        this.speakingSessionMessageResultMapper = speakingSessionMessageResultMapper;
    }

    /**
     * Bắt đầu 1 session mới (lúc này chưa có message nào)
     *
     * @param command chứa user id, persona id, formality level, marugoto level
     * @return session code (String)
     */
    @Override
    public String startConversation(StartSpeakingConversationCommand command) {
        return transactionPort.execute(() -> {
            // Kiểm tra xem người dùng đã tới giới hạn lượt tạo session trong ngày chưa
            speakingSessionValidator.validateSessionStartLimit(command.userId());

            // Kiểm tra xem người dùng đã tới giới hạn số session đang mở chưa
            speakingSessionValidator.validateMaxInProgressSession(command.userId());

            String sessionCode = UUID.randomUUID().toString();

            Persona persona = personaRepositoryPort.findById(command.personaId())
                    .orElseThrow(() -> new ApplicationException(
                            PersonaErrorCode.PERSONA_NOT_FOUND,
                            PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                            command.personaId())
                    );

            // tạo mới và lưu speaking session trong db
            // trạng thái sẽ là IN_PROGRESS
            SpeakingSession savedSpeakingSession = speakingSessionRepositoryPort
                    .initSpeakingSession(
                            sessionCode,
                            command.userId(),
                            command.personaId(),
                            "Conversation with " + persona.getName(),
                            persona.getVoiceName(),
                            command.formalityLevel(),
                            command.marugotoLevel()
                    );

            String systemPromptContent = speakingSessionHelper.buildSystemPromptContent(
                    persona,
                    command.formalityLevel(),
                    command.marugotoLevel()
            );

            List<Map<String, String>> messages = List.of(
                    Map.of(
                            AiMessageField.ROLE, SenderType.USER.name().toLowerCase(),
                            AiMessageField.CONTENT, "こんにちは、話しましょう！"
                    ),
                    Map.of(
                            AiMessageField.ROLE, SenderType.SYSTEM.name().toLowerCase(),
                            AiMessageField.CONTENT, systemPromptContent
                    )
            );

            String rawReply = aiChatPort.chatWithContext(messages);
            ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

            speakingSessionRepositoryPort.saveSpeakingSessionMessage(
                    SpeakingSessionMessageCommand.builder()
                            .sessionId(savedSpeakingSession.getId())
                            .turnIndex(0)
                            .senderType(SenderType.ASSISTANT)
                            .messageType(MessageType.TEXT)
                            .content(parsed.reply())
                            .contentTranslation(parsed.replyTranslation())
                            .grammarNote(parsed.grammarNote())
                            .suggestedReplies(parsed.suggestedReplies())
                            .build()
            );

            return sessionCode;
        });
    }

    @Override
    public ChatResult sendMessage(SendTextMessageCommand command) {
        return transactionPort.execute(() -> {
            String sessionCode = command.sessionCode();

            SpeakingSession speakingSession = speakingSessionRepositoryPort.findBySessionCode(sessionCode);

            // nếu session không phải IN PROGRESS thì ném ra lỗi
            if (!SpeakingSessionStatus.IN_PROGRESS.equals(speakingSession.getStatus())) {
                throw new ApplicationException(
                        LlmApplicationError.LLM_SESSION_STATUS_INVALID,
                        LlmDetailMessageKey.LLM_SESSION_STATUS_INVALID
                );
            }

            // kiểm tra xem đã đạt tới giới hạn lượt chat trong session này chưa
            speakingSessionValidator.validateSessionTurnLimit(sessionCode, speakingSession.getUserId());

            Persona persona = personaRepositoryPort.findById(speakingSession.getPersonaId())
                    .orElseThrow(() -> new ApplicationException(
                            PersonaErrorCode.PERSONA_NOT_FOUND,
                            PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                            speakingSession.getPersonaId())
                    );

            List<SpeakingSessionMessage> previousMessages = speakingSessionMessageRepositoryPort
                    .findAllBySessionId(speakingSession.getId());

            List<Map<String, String>> contextMessages = speakingSessionHelper.getSlidingWindowMessages(
                    speakingSession,
                    persona,
                    previousMessages,
                    Map.of(
                            AiMessageField.ROLE, SenderType.USER.name().toLowerCase(),
                            AiMessageField.CONTENT, command.userMessage()
                    )
            );

            String rawReply = aiChatPort.chatWithContext(contextMessages);
            ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

            int currentTurn = speakingSession.getTotalTurns() + 1;
            speakingSessionRepositoryPort.increaseTotalTurns(speakingSession.getId());

            // lưu chat của người dùng
            SpeakingSessionMessage userMessage = speakingSessionRepositoryPort.saveSpeakingSessionMessage(
                    SpeakingSessionMessageCommand.builder()
                            .sessionId(speakingSession.getId())
                            .turnIndex(currentTurn)
                            .senderType(SenderType.USER)
                            .messageType(MessageType.TEXT)
                            .content(command.userMessage())
                            .correctedText(parsed.correctedUserText())
                            .correctionExplanation(parsed.correctionExplanation())
                            .hintForLearner(parsed.hintForLearner())
                            .build()
            );

            // lưu phản hồi của AI
            SpeakingSessionMessage aiMessage = speakingSessionRepositoryPort.saveSpeakingSessionMessage(
                    SpeakingSessionMessageCommand.builder()
                            .sessionId(speakingSession.getId())
                            .turnIndex(currentTurn)
                            .senderType(SenderType.ASSISTANT)
                            .messageType(MessageType.TEXT)
                            .content(parsed.reply())
                            .contentTranslation(parsed.replyTranslation())
                            .grammarNote(parsed.grammarNote())
                            .suggestedReplies(parsed.suggestedReplies())
                            .build()
            );

            return new ChatResult(
                    speakingSessionMessageResultMapper.domainToResult(userMessage),
                    speakingSessionMessageResultMapper.domainToResult(aiMessage)
            );
        });
    }

    @Override
    public ChatResult sendAudioMessage(SendAudioMessageCommand command) {
        StoredFile storedFile = command.storedFile();

        if (storedFile == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_EMPTY
            );
        }

        // transaction để xử lí record của người dùng gửi cho AI
        ChatResult chatResult = transactionPort.execute(() -> {
                    String sessionCode = command.sessionCode();

                    SpeakingSession speakingSession = speakingSessionRepositoryPort.findBySessionCode(sessionCode);

                    // nếu session không phải IN PROGRESS thì ném ra lỗi
                    if (!SpeakingSessionStatus.IN_PROGRESS.equals(speakingSession.getStatus())) {
                        throw new ApplicationException(
                                LlmApplicationError.LLM_SESSION_STATUS_INVALID,
                                LlmDetailMessageKey.LLM_SESSION_STATUS_INVALID
                        );
                    }

                    // kiểm tra xem đã đạt tới giới hạn lượt chat trong session này chưa
                    speakingSessionValidator.validateSessionTurnLimit(sessionCode, speakingSession.getUserId());

                    Persona persona = personaRepositoryPort.findById(speakingSession.getPersonaId())
                            .orElseThrow(() -> new ApplicationException(
                                    PersonaErrorCode.PERSONA_NOT_FOUND,
                                    PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                                    speakingSession.getPersonaId())
                            );

                    SpeechToTextResult speechToTextResult = speechToTextPort.transcribeAndAssess(
                            command.audioBytes(),
                            command.duration(),
                            null,
                            command.userId()
                    );

                    String transcribedText = speechToTextResult.transcribedText();

                    List<SpeakingSessionMessage> previousMessages = speakingSessionMessageRepositoryPort
                            .findAllBySessionId(speakingSession.getId());

                    List<Map<String, String>> contextMessages = speakingSessionHelper.getSlidingWindowMessages(
                            speakingSession,
                            persona,
                            previousMessages,
                            Map.of(
                                    AiMessageField.ROLE, SenderType.USER.name().toLowerCase(),
                                    AiMessageField.CONTENT, transcribedText
                            )
                    );

                    String rawReply = aiChatPort.chatWithContext(contextMessages);
                    ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

                    int currentTurn = speakingSession.getTotalTurns() + 1;
                    speakingSessionRepositoryPort.increaseTotalTurns(speakingSession.getId());


                    File audioFile = fileRepositoryPort.createNewForUpload(storedFile, FileAccessStatus.PRIVATE);

                    // lưu chat của người dùng
                    SpeakingSessionMessage userMessage = speakingSessionRepositoryPort.saveSpeakingSessionMessage(
                            SpeakingSessionMessageCommand.builder()
                                    .sessionId(speakingSession.getId())
                                    .turnIndex(currentTurn)
                                    .senderType(SenderType.USER)
                                    .messageType(MessageType.AUDIO)
                                    .content(transcribedText)
                                    .correctedText(parsed.correctedUserText())
                                    .correctionExplanation(parsed.correctionExplanation())
                                    .hintForLearner(parsed.hintForLearner())
                                    .pronunciationScore(speechToTextResult.pronunciationScore())
                                    .audioFile(audioFile)
                                    .build()
                    );

                    // lưu phản hồi của AI
                    SpeakingSessionMessage aiMessage = speakingSessionRepositoryPort.saveSpeakingSessionMessage(
                            SpeakingSessionMessageCommand.builder()
                                    .sessionId(speakingSession.getId())
                                    .turnIndex(currentTurn)
                                    .senderType(SenderType.ASSISTANT)
                                    .messageType(MessageType.TEXT)
                                    .content(parsed.reply())
                                    .contentTranslation(parsed.replyTranslation())
                                    .grammarNote(parsed.grammarNote())
                                    .suggestedReplies(parsed.suggestedReplies())
                                    .build()
                    );

                    return new ChatResult(
                            speakingSessionMessageResultMapper.domainToResult(userMessage),
                            speakingSessionMessageResultMapper.domainToResult(aiMessage)
                    );
                }
        );

        // nếu transaction thành công thì mới upload file lên cloud
        uploadFileInputPort.uploadFileToCloud(storedFile);
        
        return chatResult;
    }
}
