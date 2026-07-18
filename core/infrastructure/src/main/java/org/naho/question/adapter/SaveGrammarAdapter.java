package org.naho.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.question.entity.GrammarEntity;
import org.naho.question.model.Grammar;
import org.naho.question.port.out.SaveGrammarPort;
import org.naho.question.repository.GrammarJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SaveGrammarAdapter implements SaveGrammarPort {

    private final GrammarJpaRepository grammarJpaRepository;

    @Override
    public void saveAll(List<Grammar> grammars) {
        for (Grammar g : grammars) {
            GrammarEntity entity = new GrammarEntity();
            entity.setReading(g.getReading());
            entity.setJapanese(g.getJapanese());
            entity.setVietnameseMeaningText(g.getVietnameseMeaningText());
            entity.setEnglishMeaningText(g.getEnglishMeaningText());

            grammarJpaRepository.save(entity);
        }
    }
}
