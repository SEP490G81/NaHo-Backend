package org.naho.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.book.exception.BookErrorCode;
import org.naho.file.constant.StaticResourceProperties;
import org.naho.shared.exception.BootstrapException;
import org.naho.vocabulary.port.in.ImportVocabularyPort;
import org.naho.vocabulary.repository.VocabularyJpaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Order(1)
@Slf4j
@Component
@RequiredArgsConstructor
public class VocabularyInitializer implements ApplicationRunner {

    private final VocabularyJpaRepository vocabularyJpaRepository;
    private final ResourceLoader resourceLoader;
    private final ImportVocabularyPort importVocabularyPort;
    private final StaticResourceProperties staticResourceProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (vocabularyJpaRepository.existsBy()) {
            log.info("Vocabulary Data existed!");
        } else {
            try (InputStream inputStream = resourceLoader
                    .getResource(staticResourceProperties.getBaseLocation() +
                            staticResourceProperties.getBooks() +
                            "/vocabulary_data.xlsx")
                    .getInputStream()) {

                log.info("Initializing Vocabulary Data...");
                importVocabularyPort.importVocabulary(inputStream);
                log.info("Vocabulary Data initialized");
            } catch (IOException e) {
                throw new BootstrapException(
                        BookErrorCode.BOOK_IMPORT_FAILED,
                        e.getMessage());
            }
        }
    }
}
