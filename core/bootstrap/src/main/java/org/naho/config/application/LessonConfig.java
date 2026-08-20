package org.naho.config.application;

import org.naho.book.port.in.GetLessonDetailInputPort;
import org.naho.book.port.in.UpdateLessonInputPort;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.usecase.GetLessonDetailUseCase;
import org.naho.book.usecase.UpdateLessonUseCase;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LessonConfig {

    @Bean
    public GetLessonDetailInputPort getLessonDetailInputPort(
            LessonRepositoryPort lessonRepositoryPort,
            ObjectiveRepositoryPort objectiveRepositoryPort
    ) {
        return new GetLessonDetailUseCase(lessonRepositoryPort, objectiveRepositoryPort);
    }

    @Bean
    public UpdateLessonInputPort updateLessonInputPort(
            LessonRepositoryPort lessonRepositoryPort,
            TransactionPort transactionPort,
            FuriganaGenerationPort furiganaGenerationPort
    ) {
        return new UpdateLessonUseCase(
                lessonRepositoryPort,
                transactionPort,
                furiganaGenerationPort
        );
    }
}
