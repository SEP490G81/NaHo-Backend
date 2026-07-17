package org.naho.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.book.exception.BookErrorCode;
import org.naho.book.port.in.ImportBookInputPort;
import org.naho.book.repository.TopicJpaRepository;
import org.naho.shared.exception.BootstrapException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookDataInitializer implements ApplicationRunner {

    private final ResourceLoader resourceLoader;
    private final ImportBookInputPort importBookInputPort;
    private final TopicJpaRepository topicJpaRepository;

    @Value("${app.static-resources.base-location}")
    private String staticResourcesBaseLocation;

    @Override
    public void run(ApplicationArguments args) {
        if (topicJpaRepository.existsBy()) {
            log.info("Book Data existed!");
        } else {
            try (InputStream inputStream = resourceLoader
                    .getResource(staticResourcesBaseLocation + "books/data.xlsx")
                    .getInputStream()) {

                log.info("Initializing Book Data...");

                importBookInputPort.importBookDataFromExcel(inputStream);

                log.info("Book Data initialized");
            } catch (IOException e) {
                throw new BootstrapException(
                        BookErrorCode.BOOK_IMPORT_FAILED,
                        e.getMessage()
                );
            }
        }
    }
}
