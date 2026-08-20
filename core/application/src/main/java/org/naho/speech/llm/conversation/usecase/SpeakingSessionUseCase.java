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
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.conversation.command.SendAudioMessageCommand;
import org.naho.speech.llm.conversation.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.conversation.command.StartSpeakingConversationCommand;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper.ParsedAiReply;
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
            SpeakingSessionResultMapper speakingSessionResultMapper
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
    }

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

        FormalityLevel effectiveFormality = command.formalityLevelOverride();
        if (effectiveFormality == null && persona.getConversationStyle() != null) {
            effectiveFormality = persona.getConversationStyle().getFormalityLevel();
        }

        MarugotoLevel effectiveMarugoto = command.marugotoLevelOverride();
        if (effectiveMarugoto == null && persona.getConversationStyle() != null) {
            effectiveMarugoto = persona.getConversationStyle().getMarugotoLevel();
        }

        // tạo mới và lưu speaking session trong db
        // trạng thái sẽ là INIT (tức là chưa có đoạn chat nào)
        speakingSessionRepositoryPort.initSpeakingSession(
                sessionCode,
                command.userId(),
                command.personaId(),
                "Conversation with " + persona.getName(),
                "ja-JP-NanamiNeural",
                effectiveMarugoto,
                effectiveFormality
        );

        return sessionCode;
    }

    @Override
    public StartConversationResult initFirstGreeting(String sessionCode, Long userId) {
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
                        speakingSession.getPersonaId())
                );

        String customInstruction = speakingSessionHelper.buildCustomInstruction(
                persona,
                speakingSession.getFormalityLevel(),
                speakingSession.getMarugotoLevel()
        );

        String prompt = SpeakingSessionHelper.SYSTEM_PROMPT_TEMPLATE.formatted(customInstruction);

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", prompt),
                Map.of("role", "user", "content", "こんにちは、話しましょう！")
        );

        String rawReply = aiChatPort.chatWithContext(messages);
        ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

        String initialTranscript = "[Turn]\nUser: こんにちは、話しましょう！\nAssistant: " + parsed.reply() + "\n";

        speakingSessionRepositoryPort.saveSessionMessage(
                sessionCode,
                0,
                "assistant",
                MessageType.TEXT,
                parsed.reply(),
                parsed.replyTranslation(),
                null,
                null,
                parsed.grammarNote(),
                null,
                null
        );

        speakingSessionRepositoryPort.updateSessionTurnAndTranscriptAndStatus(
                sessionCode,
                0,
                initialTranscript,
                SpeakingSessionStatus.IN_PROGRESS
        );

        String audioBase64 = speakingSessionHelper.toAudioBase64(speakingSession.getId(), parsed.reply());

        return new StartConversationResult(
                sessionCode,
                audioBase64,
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarNote()
        );
    }

    @Override
    public ChatResult sendMessage(SendMessageWithSessionCommand command) {
        String sessionCode = command.sessionCode();

        SpeakingSession speakingSession = speakingSessionRepositoryPort.findBySessionCode(sessionCode);

        speakingSessionValidator.validateSessionNotCompleted(sessionCode);
        speakingSessionValidator.validateSessionTurnLimit(sessionCode, speakingSession.getUserId());

        Persona persona = personaRepositoryPort.findById(speakingSession.getPersonaId())
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        speakingSession.getPersonaId())
                );

        List<SpeakingSessionMessage> previousMessages = speakingSessionMessageRepositoryPort
                .findAllBySessionId(speakingSession.getId());

        String userMessage = command.userMessage();

        List<Map<String, String>> contextMessages = speakingSessionHelper.getSlidingWindowMessages(
                speakingSession,
                persona,
                previousMessages
        );
        List<Map<String, String>> messagesToSend = new ArrayList<>(contextMessages);
        messagesToSend.add(Map.of("role", "user", "content", userMessage));

        String rawReply = aiChatPort.chatWithContext(messagesToSend);
        ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

        int currentTurn = speakingSession.getTotalTurns() + 1;
        String appendTurn = "[Turn]\nUser: " + userMessage + "\nAssistant: " + parsed.reply() + "\n";
        String currentTranscript = speakingSession.getFullTranscript() != null ? speakingSession.getFullTranscript() : "";
        String updatedTranscript = currentTranscript + appendTurn;

        // Async persistence to DB via Virtual Threads to optimize turn latency
        final String sCode = sessionCode;
        final int sTurn = currentTurn;
        final String uMsg = userMessage;
        final String aMsg = parsed.reply();
        final String aTrans = parsed.replyTranslation();
        final String cText = parsed.correctedUserText();
        final String cExp = parsed.correctionExplanation();
        final String gNote = parsed.grammarNote();
        final String hLearner = parsed.hintForLearner();
        final String fTranscript = updatedTranscript;

        Thread.ofVirtual().start(() -> {
            try {
                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, "user", MessageType.TEXT, uMsg, null,
                        cText, cExp, null, hLearner, null);
                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, "assistant", MessageType.TEXT, aMsg, aTrans, null,
                        null, gNote, null, null);
                speakingSessionRepositoryPort.updateSessionTurnAndTranscript(sCode, sTurn, fTranscript);
            } catch (Exception e) {
                System.out.println("[SpeakingSession] Error saving session message: " + e.getMessage());
                throw new ApplicationException(
                        LlmApplicationError.LLM_SAVE_SESSION_FAILED,
                        LlmDetailMessageKey.LLM_SAVE_SESSION_FAILED,
                        e.getMessage()
                );
            }
        });

        String aiAudio = speakingSessionHelper.toAudioBase64(speakingSession.getId(), parsed.reply());

        return new ChatResult(
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarNote(),
                parsed.correctedUserText(),
                parsed.correctionExplanation(),
                aiAudio
        );
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
        String appendTurn = "[Turn]\nUser: " + transcribedText + "\nAssistant: " + parsed.reply() + "\n";
        String currentTranscript = speakingSession.getFullTranscript() != null ? speakingSession.getFullTranscript() : "";
        String updatedTranscript = currentTranscript + appendTurn;

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
        final String fTranscript = updatedTranscript;

        final StoredFile storedFile = command.storedFile();

        Thread.ofVirtual().start(() -> {
            try {
                File audioFile = null;
                if (storedFile != null) {
                    audioFile = fileRepositoryPort.createNewForUpload(storedFile, FileAccessStatus.PRIVATE);
                }
                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, "user", MessageType.AUDIO, uMsg, null,
                        cText, cExp, null, hLearner, pronScore, audioFile);
                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, "assistant", MessageType.AUDIO, aMsg, aTrans, null,
                        null, gNote, null, null);
                speakingSessionRepositoryPort.updateSessionTurnAndTranscript(sCode, sTurn, fTranscript);

                if (storedFile != null) {
                    uploadFileInputPort.uploadFileToCloud(storedFile);
                }
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
