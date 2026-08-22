package org.naho.speech.llm.conversation.usecase;

import org.naho.file.constant.FileAccessStatus;
import org.naho.file.model.File;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.conversation.command.SendAudioMessageCommand;
import org.naho.speech.llm.conversation.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.conversation.command.SpeakingSessionMessageCommand;
import org.naho.speech.llm.conversation.command.StartSpeakingConversationCommand;
import org.naho.speech.llm.conversation.constant.AiMessageField;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.internal.ParsedAiReply;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.conversation.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.conversation.port.out.AiChatPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionMessageRepositoryPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.port.out.SpeechToTextPort;
import org.naho.speech.llm.conversation.result.*;
import org.naho.speech.llm.conversation.validator.SpeakingSessionValidator;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;
import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SenderType;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.naho.user.exception.UserErrorCode;

import java.util.ArrayList;
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
            TransactionPort transactionPort
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
    }

    /**
     * Bắt đầu 1 session mới (lúc này chưa có message nào)
     *
     * @param command chứa user id, persona id, formality level, marugoto level
     * @return session code (String)
     */
    @Override
    public String startConversation(StartSpeakingConversationCommand command) {
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
        // trạng thái sẽ là INIT (tức là chưa có đoạn chat nào)
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

        return savedSpeakingSession.getSessionCode();
    }

    /**
     * Lấy ra lời chào đầu tiêu của AI sau khi bắt đầu đoạn chat
     *
     * @param sessionCode mã của session
     * @param userId      user id
     * @return StartConversationResult
     */
    @Override
    public StartConversationResult initFirstGreeting(String sessionCode, Long userId) {
        return transactionPort.execute(() -> {
            // kiểm tra xem session có thuộc về user không
            speakingSessionValidator.validateSessionIsBelongToUser(sessionCode, userId);

            SpeakingSession speakingSession = speakingSessionRepositoryPort.findBySessionCode(sessionCode);

            // kiểm tra xem session đã start chưa, nếu đã start thì ném ra lỗi
            if (!SpeakingSessionStatus.INIT.equals(speakingSession.getStatus())) {
                throw new ApplicationException(
                        LlmApplicationError.LLM_SESSION_ALREADY_STARTED,
                        LlmDetailMessageKey.LLM_SESSION_ALREADY_STARTED
                );
            }

            Persona persona = personaRepositoryPort.findById(speakingSession.getPersonaId())
                    .orElseThrow(() -> new ApplicationException(
                            PersonaErrorCode.PERSONA_NOT_FOUND,
                            PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                            speakingSession.getPersonaId()
                    ));

            String systemPromptContent = speakingSessionHelper.buildSystemPromptContent(
                    persona,
                    speakingSession.getFormalityLevel(),
                    speakingSession.getMarugotoLevel()
            );

            List<Map<String, String>> messages = List.of(
                    Map.of(
                            AiMessageField.ROLE, SenderType.SYSTEM.name().toLowerCase(),
                            AiMessageField.CONTENT, systemPromptContent
                    ),
                    Map.of(
                            AiMessageField.ROLE, SenderType.USER.name().toLowerCase(),
                            AiMessageField.CONTENT, "こんにちは、話しましょう！"
                    )
            );

            String rawReply = aiChatPort.chatWithContext(messages);
            ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

            speakingSessionRepositoryPort.saveSpeakingSessionMessage(
                    SpeakingSessionMessageCommand.builder()
                            .sessionCode(sessionCode)
                            .turnIndex(0)
                            .senderType(SenderType.ASSISTANT)
                            .messageType(MessageType.TEXT)
                            .content(parsed.reply())
                            .contentTranslation(parsed.replyTranslation())
                            .grammarNote(parsed.grammarNote())
                            .build()
            );

            speakingSessionRepositoryPort.updateSpeakingSessionStatus(sessionCode, SpeakingSessionStatus.IN_PROGRESS);

            String audioBase64 = speakingSessionHelper.toAudioBase64(speakingSession.getId(), parsed.reply());

            return new StartConversationResult(
                    sessionCode,
                    audioBase64,
                    parsed.reply(),
                    parsed.replyTranslation(),
                    parsed.grammarNote()
            );
        });
    }

    @Override
    public ChatResult sendMessage(SendMessageWithSessionCommand command) {
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
                    previousMessages
            );

            contextMessages.add(Map.of(
                    AiMessageField.ROLE, SenderType.USER.name().toLowerCase(),
                    AiMessageField.CONTENT, command.userMessage()
            ));

            String rawReply = aiChatPort.chatWithContext(contextMessages);
            ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

            int currentTurn = speakingSession.getTotalTurns() + 1;

            // lưu chat của người dùng
            speakingSessionRepositoryPort.saveSpeakingSessionMessage(
                    SpeakingSessionMessageCommand.builder()
                            .sessionCode(sessionCode)
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
            speakingSessionRepositoryPort.saveSpeakingSessionMessage(
                    SpeakingSessionMessageCommand.builder()
                            .sessionCode(sessionCode)
                            .turnIndex(currentTurn)
                            .senderType(SenderType.ASSISTANT)
                            .messageType(MessageType.TEXT)
                            .content(parsed.reply())
                            .contentTranslation(parsed.replyTranslation())
                            .grammarNote(parsed.grammarNote())
                            .build()
            );

            String audioBase64 = speakingSessionHelper.toAudioBase64(speakingSession.getId(), parsed.reply());

            return new ChatResult(
                    parsed.reply(),
                    parsed.replyTranslation(),
                    parsed.grammarNote(),
                    parsed.correctedUserText(),
                    parsed.correctionExplanation(),
                    audioBase64
            );
        });
    }

    @Override
    public AudioChatResult sendAudioMessage(SendAudioMessageCommand command) {
        String sessionCode = command.sessionCode();
        speakingSessionValidator.validateSessionNotCompleted(sessionCode);
        speakingSessionValidator.validateSessionTurnLimit(sessionCode, command.userId());

        SpeakingSession speakingSession = speakingSessionRepositoryPort.findBySessionCode(sessionCode);

        Persona persona = personaRepositoryPort.findById(speakingSession.getPersonaId())
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        speakingSession.getPersonaId())
                );

        SpeechToTextResult sttResult = speechToTextPort.transcribeAndAssess(
                command.audioBytes(),
                command.referenceText(),
                command.userId()
        );

        String transcribedText = sttResult.transcribedText();
        System.out.println("[SpeakingSession] STT result: " + transcribedText);

        List<SpeakingSessionMessage> previousMessages = speakingSessionMessageRepositoryPort
                .findAllBySessionId(speakingSession.getId());

        List<Map<String, String>> contextMessages = speakingSessionHelper.getSlidingWindowMessages(
                speakingSession,
                persona,
                previousMessages
        );
        List<Map<String, String>> messagesToSend = new ArrayList<>(contextMessages);
        messagesToSend.add(Map.of("role", "user", "content", transcribedText));

        String rawReply = aiChatPort.chatWithContext(messagesToSend);
        ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

        int currentTurn = speakingSession.getTotalTurns() + 1;
//        String appendTurn = "[Turn]\nUser: " + transcribedText + "\nAssistant: " + parsed.reply() + "\n";
//        String currentTranscript = speakingSession.getFullTranscript() != null ? speakingSession.getFullTranscript() : "";
//        String updatedTranscript = currentTranscript + appendTurn;

        // Async persistence to DB via Virtual Threads to optimize turn latency
        final String sCode = sessionCode;
        final int sTurn = currentTurn;
        final String uMsg = transcribedText;
        final String aMsg = parsed.reply();
        final String aTrans = parsed.replyTranslation();
        final String cText = parsed.correctedUserText();
        final String cExp = parsed.correctionExplanation();
        final String gNote = parsed.grammarNote();
        final String hLearner = parsed.hintForLearner();
        final Double pronScore = sttResult.pronunciationScore();
//        final String fTranscript = updatedTranscript;

        final StoredFile storedFile = command.storedFile();

        Thread.ofVirtual().start(() -> {
            try {
                File audioFile = null;
                if (storedFile != null) {
                    audioFile = fileRepositoryPort.createNewForUpload(storedFile, FileAccessStatus.PRIVATE);
                }
//                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, SenderType.USER, MessageType.AUDIO, uMsg, null,
//                        cText, cExp, null, hLearner, pronScore, audioFile);
//                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, SenderType.ASSISTANT, MessageType.AUDIO, aMsg, aTrans, null,
//                        null, gNote, null, null);
//                speakingSessionRepositoryPort.updateSessionTurnAndTranscript(sCode, sTurn, fTranscript);
//
//                if (storedFile != null) {
//                    uploadFileInputPort.uploadFileToCloud(storedFile);
//                }
            } catch (Exception e) {
                throw new ApplicationException(
                        LlmApplicationError.LLM_SAVE_SESSION_FAILED,
                        LlmDetailMessageKey.LLM_SAVE_SESSION_FAILED,
                        e.getMessage()
                );
            }
        });

        String aiAudio = speakingSessionHelper.toAudioBase64(speakingSession.getId(), parsed.reply());

        return new AudioChatResult(
                transcribedText,
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarNote(),
                parsed.correctedUserText(),
                parsed.correctionExplanation(),
                aiAudio,
                sttResult.accuracyScore(),
                sttResult.fluencyScore(),
                sttResult.completenessScore(),
                sttResult.pronunciationScore(),
                parsed.suggestedReplies());
    }

    @Override
    public SpeakingSessionResult getInProgressSessionDetails(String sessionCode, Long userId) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (sessionCode == null || sessionCode.isBlank()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        // Kiểm tra xem session có thuộc về user không
        speakingSessionValidator.validateSessionIsBelongToUser(sessionCode, userId);

        SpeakingSession inProgressSession = speakingSessionRepositoryPort
                .findBySessionCodeAndStatus(sessionCode, SpeakingSessionStatus.IN_PROGRESS);

        return speakingSessionResultMapper.domainToResult(inProgressSession);
    }
}
