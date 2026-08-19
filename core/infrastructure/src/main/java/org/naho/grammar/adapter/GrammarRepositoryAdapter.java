package org.naho.grammar.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.grammar.entity.GrammarEntity;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.pagination.PageData;
import org.naho.pagination.PageMeta;
import org.naho.question.model.Grammar;
import org.naho.question.repository.GrammarJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GrammarRepositoryAdapter implements GrammarRepositoryPort {

    private final GrammarJpaRepository grammarJpaRepository;

    @Override
    public Grammar save(Grammar grammar) {
        GrammarEntity entity = new GrammarEntity();
        if (grammar.getId() != null) {
            entity.setId(grammar.getId());
        }
        entity.setJapanese(grammar.getJapanese());
        entity.setReading(grammar.getReading());
        entity.setVietnameseMeaningText(grammar.getVietnameseMeaningText());
        entity.setEnglishMeaningText(grammar.getEnglishMeaningText());

        GrammarEntity saved = grammarJpaRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Grammar> findById(Long id) {
        return grammarJpaRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public void deleteById(Long id) {
        grammarJpaRepository.deleteById(id);
    }

    @Override
    public PageData<Grammar> searchByKeyword(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GrammarEntity> entityPage = grammarJpaRepository.searchByKeyword(keyword, pageable);

        List<Grammar> grammars = entityPage.getContent().stream()
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

        return new PageData<>(grammars, pageMeta);
    }

    private Grammar mapToDomain(GrammarEntity entity) {
        return Grammar.builder()
                .id(entity.getId())
                .reading(entity.getReading())
                .japanese(entity.getJapanese())
                .vietnameseMeaningText(entity.getVietnameseMeaningText())
                .englishMeaningText(entity.getEnglishMeaningText())
                .build();
    }
}
