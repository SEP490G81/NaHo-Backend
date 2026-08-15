package org.naho.config.application;

import org.naho.book.adapter.LessonRepositoryAdapter;
import org.naho.book.adapter.ObjectiveRepositoryAdapter;
import org.naho.book.port.in.GetLessonDetailInputPort;
import org.naho.book.port.in.UpdateLessonInputPort;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.usecase.GetLessonDetailUseCase;
import org.naho.book.usecase.UpdateLessonUseCase;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LessonConfig {

    @Bean
    public GetLessonDetailInputPort getLessonDetailInputPort(
            LessonRepositoryAdapter lessonRepositoryAdapter,
            ObjectiveRepositoryAdapter objectiveRepositoryAdapter
    ) {
        return new GetLessonDetailUseCase(lessonRepositoryAdapter, objectiveRepositoryAdapter);
    }

    @Bean
    public UpdateLessonInputPort updateLessonInputPort(
            LessonRepositoryPort lessonRepositoryPort,
            TransactionPort transactionPort,
            org.naho.furigana.port.out.FuriganaGenerationPort furiganaGenerationPort
    ) {
        return new UpdateLessonUseCase(
                lessonRepositoryPort,
                transactionPort,
                furiganaGenerationPort
        );
    }
}
