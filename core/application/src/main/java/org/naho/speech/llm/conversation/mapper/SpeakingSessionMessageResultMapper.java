package org.naho.speech.llm.conversation.mapper;

import org.naho.file.port.out.FileStorageServicePort;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.result.SpeakingSessionMessageResult;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;
import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SenderType;

public class SpeakingSessionMessageResultMapper {
    private final SpeakingSessionHelper speakingSessionHelper;
    private final FileStorageServicePort fileStorageServicePort;

    public SpeakingSessionMessageResultMapper(
            SpeakingSessionHelper speakingSessionHelper,
            FileStorageServicePort fileStorageServicePort
    ) {
        this.speakingSessionHelper = speakingSessionHelper;
        this.fileStorageServicePort = fileStorageServicePort;
    }

    public SpeakingSessionMessageResult domainToResult(SpeakingSessionMessage domain) {
        if (domain == null) {
            return null;
        }

        String aiReplyAudio = SenderType.ASSISTANT.equals(domain.getSenderType()) ?
                speakingSessionHelper.toAudioBase64(domain.getSessionId(), domain.getContent()) :
                "";

        String userRecordAudio = (domain.getMessageType().equals(MessageType.AUDIO) && domain.getAudioFileId() != null) ?
                fileStorageServicePort.generatePresignedUrl(domain.getAudioFileId()) :
                "";

        return SpeakingSessionMessageResult.builder()
                .id(domain.getId())
                .sessionId(domain.getSessionId())
                .audioFileId(domain.getAudioFileId())
                .turnIndex(domain.getTurnIndex())
                .senderType(domain.getSenderType())
                .messageType(domain.getMessageType())
                .content(domain.getContent())
                .contentTranslation(domain.getContentTranslation())
                .correctedText(domain.getCorrectedText())
                .correctionExplanation(domain.getCorrectionExplanation())
                .grammarNote(domain.getGrammarNote())
                .hintForLearner(domain.getHintForLearner())
                .pronunciationScore(domain.getPronunciationScore())
                .aiReplyAudio(aiReplyAudio)
                .userRecordAudio(userRecordAudio)
                .build();
    }
}
