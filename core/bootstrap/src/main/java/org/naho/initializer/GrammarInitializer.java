package org.naho.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.book.exception.BookErrorCode;
import org.naho.file.constant.StaticResourceProperties;
import org.naho.grammar.port.in.ImportGrammarPort;
import org.naho.question.repository.GrammarJpaRepository;
import org.naho.shared.exception.BootstrapException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Order(2)
@Slf4j
@Component
@RequiredArgsConstructor
public class GrammarInitializer implements ApplicationRunner {

    private final ResourceLoader resourceLoader;
    private final GrammarJpaRepository grammarJpaRepository;
    private final ImportGrammarPort importGrammarPort;
    private final StaticResourceProperties staticResourceProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (grammarJpaRepository.existsBy()) {
            log.info("Grammar Data existed!");
        } else {
            try (InputStream inputStream = resourceLoader
                    .getResource(staticResourceProperties.getLocalRoot() +
                            staticResourceProperties.getBooks() +
                            "/grammar_data.xlsx")
                    .getInputStream()) {

                log.info("Initializing Grammar Data...");
                importGrammarPort.importGrammar(inputStream);
                log.info("Grammar Data initialized");
            } catch (IOException e) {
                throw new BootstrapException(
                        BookErrorCode.BOOK_IMPORT_FAILED,
                        e.getMessage());
            }
        }
    }

}
