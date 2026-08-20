package org.naho.question.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.naho.file.entity.FileEntity;
import org.naho.file.mapper.FileEntityMapper;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.repository.FileJpaRepository;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.pagination.PageData;
import org.naho.question.command.SpeakingHistoryFilterCommand;
import org.naho.question.entity.AnswerHistoryEntity;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.question.mapper.AnswerHistoryEntityMapper;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.repository.AnswerHistoryJpaRepository;
import org.naho.question.repository.SpeakingQuestionJpaRepository;
import org.naho.question.result.SpeakingHistoryListItemResult;
import org.naho.shared.exception.CommonErrorCode;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.azure.model.AnswerHistory;
import org.naho.speech.azure.model.ContentAssessment;
import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.model.WordAssessment;
import org.naho.speech.azure.repository.SpeechAssessmentJpaRepository;
import org.naho.speech.azure.repository.WordAssessmentJpaRepository;
import org.naho.speech.llm.question.repository.AiFeedbackJpaRepository;
import org.naho.user.entity.UserEntity;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AnswerHistoryRepositoryAdapter implements AnswerHistoryRepositoryPort {

    private final AnswerHistoryJpaRepository answerHistoryJpaRepository;
    private final SpeechAssessmentJpaRepository speechAssessmentJpaRepository;
    private final WordAssessmentJpaRepository wordAssessmentJpaRepository;

    private final UserJpaRepository userJpaRepository;
    private final SpeakingQuestionJpaRepository questionJpaRepository;
    private final FileJpaRepository fileJpaRepository;
    private final FileStorageServicePort fileStorageServicePort;
    private final AnswerHistoryEntityMapper answerHistoryEntityMapper;
    private final AiFeedbackJpaRepository aiFeedbackJpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FileEntityMapper fileEntityMapper;

    @Override
    public AnswerHistory saveAnswerHistory(AnswerHistory domain) {
        return null;
    }

    @Override
    public AnswerHistory save(AnswerHistory answerHistory) {
        if (answerHistory.getUserId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (answerHistory.getSpeakingQuestionId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_ID_NULL
            );
        }

        UserEntity user = userJpaRepository.getReferenceById(answerHistory.getUserId());
        SpeakingQuestionEntity question = questionJpaRepository.getReferenceById(answerHistory.getSpeakingQuestionId());

        AnswerHistoryEntity entity = AnswerHistoryEntity.builder()
                .id(answerHistory.getId())
                .user(user)
                .speakingQuestion(question)
                .duration(answerHistory.getDuration())
                .build();

        if (answerHistory.getAudioFileId() != null) {
            FileEntity file = fileJpaRepository.getReferenceById(answerHistory.getAudioFileId());
            entity.setAudioFile(file);
        }

        AnswerHistoryEntity saved = answerHistoryJpaRepository.save(entity);
        return answerHistoryEntityMapper.entityToDomain(saved);
    }

    @Override
    public AnswerHistory createNew(AnswerHistory answerHistory) {
        if (answerHistory.getUserId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (answerHistory.getSpeakingQuestionId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_ID_NULL
            );
        }

        if (answerHistory.getSpeechAssessmentId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    SpeakingQuestionDetailMessageKey.SPEECH_ASSESSMENT_ID_NULL
            );
        }

        if (answerHistory.getAiFeedbackId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    SpeakingQuestionDetailMessageKey.AI_FEEDBACK_ID_NULL
            );
        }

        if (answerHistory.getAudioFileId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        AnswerHistoryEntity entity = AnswerHistoryEntity.builder()
                .user(userJpaRepository.getReferenceById(answerHistory.getUserId()))
                .speakingQuestion(questionJpaRepository.getReferenceById(answerHistory.getSpeakingQuestionId()))
                .speechAssessment(speechAssessmentJpaRepository.getReferenceById(answerHistory.getSpeechAssessmentId()))
                .aiFeedback(aiFeedbackJpaRepository.getReferenceById(answerHistory.getAiFeedbackId()))
                .audioFile(fileJpaRepository.getReferenceById(answerHistory.getAudioFileId()))
                .duration(answerHistory.getDuration())
                .overallScore(answerHistory.getOverallScore())
                .build();
        AnswerHistoryEntity saved = answerHistoryJpaRepository.save(entity);
        return answerHistoryEntityMapper.entityToDomain(saved);
    }

    @Override
    public SpeechAssessment saveSpeechAssessment(SpeechAssessment domain) {
//        SpeechAssessmentEntity entity = answerHistoryEntityMapper.toEntity(domain, answerHistory);
//        SpeechAssessmentEntity saved = speechAssessmentJpaRepository.save(entity);
//        return answerHistoryEntityMapper.toDomain(saved);
        return null;
    }

    @Override
    public ContentAssessment saveContentAssessment(ContentAssessment domain) {
        AnswerHistoryEntity answerHistory = answerHistoryJpaRepository.getReferenceById(domain.getAnswerHistoryId());
        return null;
    }

    @Override
    public List<WordAssessment> saveAllWordAssessment(List<WordAssessment> domains) {
        return null;

//        List<WordAssessmentEntity> entities = domains.stream().map(domain -> {
//            SpeechAssessmentEntity sa = speechAssessmentJpaRepository.getReferenceById(domain.getSpeechAssessmentId());
//            return answerHistoryEntityMapper.toEntity(domain, sa);
//        }).toList();
//
//        List<WordAssessmentEntity> saved = wordAssessmentJpaRepository.saveAll(entities);
//        return answerHistoryEntityMapper.toWordAssessmentDomainList(saved);
    }

    @Override
    public Optional<SpeechAssessment> findSpeechAssessmentByAnswerHistoryId(Long answerHistoryId) {
        return null;

//        return speechAssessmentJpaRepository.findByAnswerHistoryId(answerHistoryId)
//                .map(answerHistoryEntityMapper::toDomain);
    }

    @Override
    public Optional<ContentAssessment> findContentAssessmentByAnswerHistoryId(Long answerHistoryId) {
        return null;
//        return contentAssessmentJpaRepository.findByAnswerHistoryId(answerHistoryId)
//                .map(answerHistoryEntityMapper::toDomain);
    }

    @Override
    public PageData<SpeakingHistoryListItemResult> findUserAnswerHistories(SpeakingHistoryFilterCommand command) {
        return null;
//        Long userId = command != null ? command.userId() : null;
//        Long questionId = command != null ? command.speakingQuestionId() : null;
//        Long topicId = command != null ? command.topicId() : null;
//        String search = command != null ? command.search() : null;
//
//        int pageNumber = command != null && command.page() != null && command.page() >= 0 ? command.page() : 0;
//        int pageSize = command != null && command.size() != null && command.size() > 0 ? command.size() : 10;
//        Sort.Direction direction = (command != null && command.sortDirection() != null)
//                ? Sort.Direction.valueOf(command.sortDirection().name())
//                : Sort.Direction.DESC;
//        String sortCol = (command != null && command.sortColumn() != null)
//                ? command.sortColumn().getColumnName()
//                : "createdTime";
//
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortCol));
//
//        Specification<AnswerHistoryEntity> specification = Specification.allOf(
//                AnswerHistorySpecification.hasUserId(userId),
//                AnswerHistorySpecification.hasSpeakingQuestionId(questionId),
//                AnswerHistorySpecification.hasTopicId(topicId),
//                AnswerHistorySpecification.searchByTitle(search)
//        );
//
//        Page<AnswerHistoryEntity> entityPage = answerHistoryJpaRepository.findAll(specification, pageable);
//
//        List<SpeakingHistoryListItemResult> items = entityPage
//                .getContent()
//                .stream()
//                .map(entity -> {
//                    String audioUrl = null;
//                    FileEntity fileEntity = entity.getAudioFile();
//
//                    if (fileEntity != null) {
//                        audioUrl = fileStorageServicePort.generatePresignedUrl(
//                                fileEntityMapper.entityToDomain(fileEntity)
//                        );
//                    }
//
//                    Double score = 0.0;
//                    Double duration = entity.getDuration() != null ? entity.getDurationSec() : 0;
//                    ContentAssessmentEntity ca = entity.getContentAssessment();
//                    if (ca != null && ca.getAiFeedback() != null && !ca.getAiFeedback().isBlank()) {
//                        try {
//                            JsonNode root = objectMapper.readTree(ca.getAiFeedback());
//                            score = root.path("overallScore").asDouble(0.0);
//                            if (durationSec == 0 && root.has("durationSec")) {
//                                durationSec = root.path("durationSec").asInt(0);
//                            }
//                        } catch (Exception ignored) {
//                        }
//                    }
//
//                    return answerHistoryEntityMapper.toListItemResult(entity, score, durationSec, audioUrl);
//                }).toList();
//
//
//        return PageData.<SpeakingHistoryListItemResult>builder()
//                .pageMeta(PageMeta.builder()
//                        .currentPage(entityPage.getNumber())
//                        .pageSize(entityPage.getSize())
//                        .totalPages(entityPage.getTotalPages())
//                        .totalElements(entityPage.getTotalElements())
//                        .hasNext(entityPage.hasNext())
//                        .hasPrevious(entityPage.hasPrevious())
//                        .build())
//                .data(items)
//                .build();
    }

    @Override
    public Optional<AnswerHistory> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return answerHistoryJpaRepository
                .findById(id)
                .map(answerHistoryEntityMapper::entityToDomain);
    }
}
