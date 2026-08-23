package org.naho.speech.llm.conversation.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.entity.FileEntity;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.repository.FileJpaRepository;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.persona.mapper.PersonaIdMapper;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.conversation.command.SpeakingSessionMessageCommand;
import org.naho.speech.llm.conversation.entity.SpeakingSessionEntity;
import org.naho.speech.llm.conversation.entity.SpeakingSessionMessageEntity;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionEntityMapper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionMessageEntityMapper;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.repository.SpeakingSessionJpaRepository;
import org.naho.speech.llm.conversation.repository.SpeakingSessionMessageJpaRepository;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;
import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.UserIdMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SpeakingSessionRepositoryAdapter implements SpeakingSessionRepositoryPort {

    private final SpeakingSessionJpaRepository speakingSessionJpaRepository;
    private final SpeakingSessionMessageJpaRepository speakingSessionMessageJpaRepository;
    private final FileJpaRepository fileJpaRepository;
    private final SpeakingSessionEntityMapper speakingSessionEntityMapper;
    private final SpeakingSessionMessageEntityMapper speakingSessionMessageEntityMapper;
    private final UserIdMapper userIdMapper;
    private final PersonaIdMapper personaIdMapper;

    @Override
    @Transactional
    public SpeakingSession save(SpeakingSession speakingSession) {
        SpeakingSessionEntity entity = speakingSessionEntityMapper.domainToEntity(speakingSession);
        SpeakingSessionEntity savedEntity = speakingSessionJpaRepository.save(entity);
        return speakingSessionEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public SpeakingSession increaseTotalTurns(Long sessionId) {
        SpeakingSessionEntity entity = speakingSessionJpaRepository.getReferenceById(sessionId);
        entity.setTotalTurns(entity.getTotalTurns() + 1);
        SpeakingSessionEntity savedEntity = speakingSessionJpaRepository.save(entity);
        return speakingSessionEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    @Transactional
    public SpeakingSession initSpeakingSession(
            String sessionCode,
            Long userId,
            Long personaId,
            String topic,
            String voiceName,
            FormalityLevel formalityLevel,
            MarugotoLevel marugotoLevel
    ) {
        if (userId == null) {
            throw new InfrastructureException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (sessionCode == null || sessionCode.isBlank()) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        SpeakingSessionEntity sessionEntity = SpeakingSessionEntity.builder()
                .sessionCode(sessionCode)
                .user(userIdMapper.idToEntity(userId))
                .persona(personaIdMapper.idToEntity(personaId))
                .topic(topic)
                .voiceName(voiceName)
                .marugotoLevel(marugotoLevel)
                .formalityLevel(formalityLevel)
                .totalTurns(0)
                .status(SpeakingSessionStatus.IN_PROGRESS)
                .startedAt(Instant.now())
                .build();

        SpeakingSessionEntity savedEntity = speakingSessionJpaRepository.save(sessionEntity);
        return speakingSessionEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    @Transactional
    public SpeakingSessionMessage saveSpeakingSessionMessage(SpeakingSessionMessageCommand command) {
        SpeakingSessionEntity speakingSessionEntity = speakingSessionJpaRepository.getReferenceById(command.sessionId());

        FileEntity fileEntity = null;

        if (MessageType.AUDIO.equals(command.messageType())) {
            if (command.audioFile() == null || command.audioFile().getId() == null) {
                throw new InfrastructureException(
                        FileErrorCode.FILE_NOT_VALID,
                        FileDetailMessageKey.FILE_ID_NULL
                );
            }

            fileEntity = fileJpaRepository.findById(command.audioFile().getId())
                    .orElseThrow(() -> new InfrastructureException(
                            FileErrorCode.FILE_NOT_FOUND,
                            FileDetailMessageKey.FILE_NOT_FOUND,
                            command.audioFile().getId()
                    ));
        }

        SpeakingSessionMessageEntity speakingSessionMessageEntity =
                SpeakingSessionMessageEntity.builder()
                        .session(speakingSessionEntity)
                        .turnIndex(command.turnIndex())
                        .senderType(command.senderType())
                        .messageType(command.messageType())
                        .content(command.content())
                        .contentTranslation(command.contentTranslation())
                        .correctedText(command.correctedText())
                        .correctionExplanation(command.correctionExplanation())
                        .grammarNote(command.grammarNote())
                        .hintForLearner(command.hintForLearner())
                        .pronunciationScore(command.pronunciationScore())
                        .audioFile(fileEntity)
                        .build();

        SpeakingSessionMessageEntity savedSpeakingSessionMessageEntity =
                speakingSessionMessageJpaRepository.save(speakingSessionMessageEntity);

        return speakingSessionMessageEntityMapper.entityToDomain(savedSpeakingSessionMessageEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSessionCompleted(String sessionCode) {
        if (sessionCode == null || sessionCode.isBlank())
            return false;
        return speakingSessionJpaRepository.findBySessionCode(sessionCode)
                .map(s -> SpeakingSessionStatus.COMPLETED.equals(s.getStatus()))
                .orElse(false);
    }

    @Override
    public int countActiveSessionsByUserId(Long userId) {
        if (userId == null) {
            return 0;
        }
        return speakingSessionJpaRepository.countByUserIdAndStatus(userId, SpeakingSessionStatus.IN_PROGRESS);
    }

    @Override
    @Transactional
    public void deleteSessionBySessionCode(String sessionCode) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        SpeakingSessionEntity entity = speakingSessionJpaRepository
                .findBySessionCode(sessionCode)
                .orElseThrow(() -> new InfrastructureException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionCode
                ));

        speakingSessionJpaRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSessionBelongToUser(String sessionCode, Long userId) {
        if (userId == null) {
            throw new InfrastructureException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (sessionCode == null || sessionCode.isBlank()) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        SpeakingSessionEntity entity = speakingSessionJpaRepository
                .findBySessionCode(sessionCode)
                .orElseThrow(() -> new InfrastructureException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionCode
                ));

        Long ownerId = entity.getUser() != null ? entity.getUser().getId() : null;

        return ownerId != null && ownerId.equals(userId);
    }

    @Override
    public SpeakingSession findBySessionCode(String sessionCode) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        SpeakingSessionEntity entity = speakingSessionJpaRepository
                .findBySessionCode(sessionCode)
                .orElseThrow(() -> new InfrastructureException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionCode
                ));

        return speakingSessionEntityMapper.entityToDomain(entity);
    }

    @Override
    public SpeakingSession findBySessionId(Long sessionId) {
        if (sessionId == null) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_ID_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_ID_INVALID
            );
        }

        SpeakingSessionEntity entity = speakingSessionJpaRepository
                .findById(sessionId)
                .orElseThrow(() -> new InfrastructureException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionId
                ));

        return speakingSessionEntityMapper.entityToDomain(entity);
    }

    @Override
    public List<SpeakingSession> findAllByUserIdAndSpeakingSessionStatus(
            Long userId,
            SpeakingSessionStatus status
    ) {
        return speakingSessionJpaRepository
                .findAllByUserIdAndStatus(userId, status)
                .stream()
                .map(speakingSessionEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public SpeakingSession findByUserIdAndSpeakingSessionCodeAndSpeakingSessionStatus(Long userId, String sessionCode, SpeakingSessionStatus status) {
        if (userId == null) {
            throw new InfrastructureException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (sessionCode == null || sessionCode.isBlank()) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        if (status == null) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_STATUS_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_STATUS_INVALID
            );
        }

        SpeakingSessionEntity entity = speakingSessionJpaRepository
                .findByUser_IdAndSessionCodeAndStatus(userId, sessionCode, status)
                .orElseThrow(() -> new InfrastructureException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionCode
                ));

        return speakingSessionEntityMapper.entityToDomain(entity);
    }
}
