package org.naho.question.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.naho.file.entity.FileEntity;
import org.naho.file.mapper.FileEntityMapper;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.repository.FileJpaRepository;
import org.naho.pagination.PageData;
import org.naho.pagination.PageMeta;
import org.naho.question.command.SpeakingHistoryFilterCommand;
import org.naho.question.entity.AnswerHistoryEntity;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.question.mapper.AnswerHistoryEntityMapper;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.repository.AnswerHistoryJpaRepository;
import org.naho.question.repository.SpeakingQuestionJpaRepository;
import org.naho.question.result.SpeakingHistoryListItemResult;
import org.naho.question.specification.AnswerHistorySpecification;
import org.naho.speech.azure.entity.ContentAssessmentEntity;
import org.naho.speech.azure.entity.SpeechAssessmentEntity;
import org.naho.speech.azure.entity.WordAssessmentEntity;
import org.naho.speech.azure.repository.ContentAssessmentJpaRepository;
import org.naho.speech.azure.repository.SpeechAssessmentJpaRepository;
import org.naho.speech.azure.repository.WordAssessmentJpaRepository;
import org.naho.speech.model.AnswerHistory;
import org.naho.speech.model.ContentAssessment;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.user.entity.UserEntity;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AnswerHistoryRepositoryAdapter implements AnswerHistoryRepositoryPort {

    private final AnswerHistoryJpaRepository answerHistoryJpaRepository;
    private final SpeechAssessmentJpaRepository speechAssessmentJpaRepository;
    private final ContentAssessmentJpaRepository contentAssessmentJpaRepository;
    private final WordAssessmentJpaRepository wordAssessmentJpaRepository;

    private final UserJpaRepository userJpaRepository;
    private final SpeakingQuestionJpaRepository questionJpaRepository;
    private final FileJpaRepository fileJpaRepository;
    private final FileStorageServicePort fileStorageServicePort;
    private final AnswerHistoryEntityMapper answerHistoryEntityMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FileEntityMapper fileEntityMapper;

    @Override
    public AnswerHistory saveAnswerHistory(AnswerHistory domain) {
        UserEntity user = userJpaRepository.getReferenceById(domain.getUserId());
        SpeakingQuestionEntity question = questionJpaRepository.getReferenceById(domain.getSpeakingQuestionId());
        FileEntity file = fileJpaRepository.getReferenceById(domain.getAudioFileId());

        AnswerHistoryEntity entity = answerHistoryEntityMapper.toEntity(domain, user, question, file);
        AnswerHistoryEntity saved = answerHistoryJpaRepository.save(entity);
        return answerHistoryEntityMapper.toDomain(saved);
    }

    @Override
    public AnswerHistory save(AnswerHistory answerHistory) {
        UserEntity user = userJpaRepository.getReferenceById(answerHistory.getUserId());
        SpeakingQuestionEntity question = questionJpaRepository.getReferenceById(answerHistory.getSpeakingQuestionId());

        AnswerHistoryEntity entity = AnswerHistoryEntity.builder()
                .id(answerHistory.getId())
                .user(user)
                .speakingQuestion(question)
                .build();

        if (answerHistory.getAudioFileId() != null) {
            FileEntity file = fileJpaRepository.getReferenceById(answerHistory.getAudioFileId());
            entity.setAudioFile(file);
        }

        AnswerHistoryEntity saved = answerHistoryJpaRepository.save(entity);
        return answerHistoryEntityMapper.toDomain(saved);
    }

    @Override
    public SpeechAssessment saveSpeechAssessment(SpeechAssessment domain) {
        AnswerHistoryEntity answerHistory = answerHistoryJpaRepository.getReferenceById(domain.getAnswerHistoryId());

        SpeechAssessmentEntity entity = answerHistoryEntityMapper.toEntity(domain, answerHistory);
        SpeechAssessmentEntity saved = speechAssessmentJpaRepository.save(entity);
        return answerHistoryEntityMapper.toDomain(saved);
    }

    @Override
    public ContentAssessment saveContentAssessment(ContentAssessment domain) {
        AnswerHistoryEntity answerHistory = answerHistoryJpaRepository.getReferenceById(domain.getAnswerHistoryId());

        ContentAssessmentEntity entity = answerHistoryEntityMapper.toEntity(domain, answerHistory);
        ContentAssessmentEntity saved = contentAssessmentJpaRepository.save(entity);
        return answerHistoryEntityMapper.toDomain(saved);
    }

    @Override
    public List<WordAssessment> saveAllWordAssessment(List<WordAssessment> domains) {
        List<WordAssessmentEntity> entities = domains.stream().map(domain -> {
            SpeechAssessmentEntity sa = speechAssessmentJpaRepository.getReferenceById(domain.getSpeechAssessmentId());
            return answerHistoryEntityMapper.toEntity(domain, sa);
        }).toList();

        List<WordAssessmentEntity> saved = wordAssessmentJpaRepository.saveAll(entities);
        return answerHistoryEntityMapper.toWordAssessmentDomainList(saved);
    }

    @Override
    public Optional<SpeechAssessment> findSpeechAssessmentByAnswerHistoryId(Long answerHistoryId) {
        return speechAssessmentJpaRepository.findByAnswerHistoryId(answerHistoryId)
                .map(answerHistoryEntityMapper::toDomain);
    }

    @Override
    public Optional<ContentAssessment> findContentAssessmentByAnswerHistoryId(Long answerHistoryId) {
        return contentAssessmentJpaRepository.findByAnswerHistoryId(answerHistoryId)
                .map(answerHistoryEntityMapper::toDomain);
    }

    @Override
    public PageData<SpeakingHistoryListItemResult> findUserAnswerHistories(SpeakingHistoryFilterCommand command) {
        Long userId = command != null ? command.userId() : null;
        Long questionId = command != null ? command.speakingQuestionId() : null;
        Long topicId = command != null ? command.topicId() : null;
        String search = command != null ? command.search() : null;

        int pageNumber = command != null && command.page() != null && command.page() >= 0 ? command.page() : 0;
        int pageSize = command != null && command.size() != null && command.size() > 0 ? command.size() : 10;
        Sort.Direction direction = (command != null && command.sortDirection() != null)
                ? Sort.Direction.valueOf(command.sortDirection().name())
                : Sort.Direction.DESC;
        String sortCol = (command != null && command.sortColumn() != null)
                ? command.sortColumn().getColumnName()
                : "createdTime";

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortCol));

        Specification<AnswerHistoryEntity> specification = Specification.allOf(
                AnswerHistorySpecification.hasUserId(userId),
                AnswerHistorySpecification.hasSpeakingQuestionId(questionId),
                AnswerHistorySpecification.hasTopicId(topicId),
                AnswerHistorySpecification.searchByTitle(search)
        );

        Page<AnswerHistoryEntity> entityPage = answerHistoryJpaRepository.findAll(specification, pageable);

        List<SpeakingHistoryListItemResult> items = entityPage
                .getContent()
                .stream()
                .map(entity -> {
                    String audioUrl = null;
                    FileEntity fileEntity = entity.getAudioFile();
                    
                    if (fileEntity != null) {
                        audioUrl = fileStorageServicePort.generatePresignedUrl(
                                fileEntityMapper.entityToDomain(fileEntity)
                        );
                    }

                    Double score = 0.0;
                    Integer durationSec = 0;
                    ContentAssessmentEntity ca = entity.getContentAssessment();
                    if (ca != null && ca.getAiFeedback() != null && !ca.getAiFeedback().isBlank()) {
                        try {
                            JsonNode root = objectMapper.readTree(ca.getAiFeedback());
                            score = root.path("overallScore").asDouble(0.0);
                            durationSec = root.path("durationSec").asInt(0);
                        } catch (Exception ignored) {
                        }
                    }

                    return answerHistoryEntityMapper.toListItemResult(entity, score, durationSec, audioUrl);
                }).toList();

        return PageData.<SpeakingHistoryListItemResult>builder()
                .pageMeta(PageMeta.builder()
                        .currentPage(entityPage.getNumber())
                        .pageSize(entityPage.getSize())
                        .totalPages(entityPage.getTotalPages())
                        .totalElements(entityPage.getTotalElements())
                        .hasNext(entityPage.hasNext())
                        .hasPrevious(entityPage.hasPrevious())
                        .build())
                .data(items)
                .build();
    }

    @Override
    public Optional<AnswerHistory> findById(Long id) {
        return answerHistoryJpaRepository
                .findById(id)
                .map(answerHistoryEntityMapper::toDomain);
    }
}
