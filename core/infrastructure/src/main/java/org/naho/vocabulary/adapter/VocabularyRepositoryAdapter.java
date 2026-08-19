package org.naho.vocabulary.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.pagination.PageData;
import org.naho.pagination.PageMeta;
import org.naho.question.entity.VocabularyQuestionEntity;
import org.naho.question.model.Vocabulary;
import org.naho.question.repository.VocabularyQuestionJpaRepository;
import org.naho.vocabulary.entity.VocabularyEntity;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.out.VocabularyPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VocabularyRepositoryAdapter implements VocabularyPort {

    private final VocabularyQuestionJpaRepository vocabularyQuestionJpaRepository;

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
        return mapToDomain(saved);
    }

    @Override
    public Optional<Vocabulary> findById(Long id) {
        return vocabularyJpaRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public void deleteById(Long id) {
        vocabularyJpaRepository.deleteById(id);
    }

    @Override
    public PageData<Vocabulary> searchByKeyword(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VocabularyEntity> entityPage = vocabularyJpaRepository.searchByKeyword(keyword, pageable);

        List<Vocabulary> vocabularies = entityPage.getContent().stream()
                .map(this::mapToDomain)
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
