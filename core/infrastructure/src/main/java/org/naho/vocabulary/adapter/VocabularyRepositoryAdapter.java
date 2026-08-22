package org.naho.vocabulary.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.pagination.PageData;
import org.naho.pagination.PageMeta;
import org.naho.question.entity.VocabularyQuestionEntity;
import org.naho.question.mapper.VocabularyEntityMapper;
import org.naho.question.model.Vocabulary;
import org.naho.question.repository.VocabularyQuestionJpaRepository;
import org.naho.vocabulary.entity.VocabularyEntity;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;
import org.naho.vocabulary.repository.VocabularyJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VocabularyRepositoryAdapter implements VocabularyRepositoryPort {

    private final VocabularyQuestionJpaRepository vocabularyQuestionJpaRepository;
    private final VocabularyJpaRepository vocabularyJpaRepository;
    private final VocabularyEntityMapper vocabularyEntityMapper;

    @Override
    public List<Vocabulary> findVocabularyList(Long vocabularyQuestionId) {
        return vocabularyQuestionJpaRepository.findById(vocabularyQuestionId)
                .map(VocabularyQuestionEntity::getVocabularies)
                .map(entities -> entities.stream()
                        .map(entity -> Vocabulary.builder()
                                .id(entity.getId())
                                .reading(entity.getReading())
                                .japanese(entity.getJapanese())
                                .vietnameseMeaningText(entity.getVietnameseMeaningText())
                                .englishMeaningText(entity.getEnglishMeaningText())
                                .build())
                        .toList())
                .orElse(Collections.emptyList());
    }

    @Override
    public Vocabulary save(Vocabulary vocabulary) {
        VocabularyEntity entity = new VocabularyEntity();
        if (vocabulary.getId() != null) {
            entity.setId(vocabulary.getId());
        }
        entity.setJapanese(vocabulary.getJapanese());
        entity.setReading(vocabulary.getReading());
        entity.setVietnameseMeaningText(vocabulary.getVietnameseMeaningText());
        entity.setEnglishMeaningText(vocabulary.getEnglishMeaningText());

        VocabularyEntity saved = vocabularyJpaRepository.save(entity);
        return vocabularyEntityMapper.entityToDomain(saved);
    }

    @Override
    public Optional<Vocabulary> findById(Long id) {
        return vocabularyJpaRepository
                .findById(id)
                .map(vocabularyEntityMapper::entityToDomain);
    }

    @Override
    public void deleteById(Long id) {
        try {
            vocabularyJpaRepository.deleteById(id);
            vocabularyJpaRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new org.naho.shared.exception.ApplicationException(
                    org.naho.vocabulary.exception.VocabularyErrorCode.VOCABULARY_IN_USE,
                    "Cannot delete vocabulary because it is being used by one or more questions"
            );
        }
    }

    @Override
    public PageData<Vocabulary> searchByKeyword(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VocabularyEntity> entityPage = vocabularyJpaRepository.searchByKeyword(keyword, pageable);

        List<Vocabulary> vocabularies = entityPage.getContent().stream()
                .map(vocabularyEntityMapper::entityToDomain)
                .toList();

        PageMeta pageMeta = PageMeta.builder()
                .currentPage(entityPage.getNumber())
                .pageSize(entityPage.getSize())
                .totalPages(entityPage.getTotalPages())
                .totalElements(entityPage.getTotalElements())
                .hasNext(entityPage.hasNext())
                .hasPrevious(entityPage.hasPrevious())
                .build();

        return new PageData<>(vocabularies, pageMeta);
    }
}
