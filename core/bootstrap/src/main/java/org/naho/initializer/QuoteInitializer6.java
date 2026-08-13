package org.naho.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.book.exception.BookErrorCode;
import org.naho.file.constant.FileFolderConstant;
import org.naho.file.constant.StaticResourceProperties;
import org.naho.quote.port.in.ImportQuoteInputPort;
import org.naho.quote.repository.QuoteJpaRepository;
import org.naho.shared.exception.BootstrapException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Order(6)
@Slf4j
@Component
@RequiredArgsConstructor
public class QuoteInitializer6 implements ApplicationRunner {

    private final QuoteJpaRepository quoteJpaRepository;
    private final ResourceLoader resourceLoader;
    private final ImportQuoteInputPort importQuoteInputPort;
    private final StaticResourceProperties staticResourceProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (quoteJpaRepository.existsBy()) {
            log.info("Quote Data existed!");
        } else {
            try (InputStream inputStream = resourceLoader
                    .getResource(staticResourceProperties.getLocalRoot() +
                            FileFolderConstant.BOOKS +
                            "/quote_data.xlsx")
                    .getInputStream()) {

                log.info("Initializing Quote Data...");
                importQuoteInputPort.importQuote(inputStream);
                log.info("Quote Data initialized");
            } catch (IOException e) {
                throw new BootstrapException(
                        BookErrorCode.BOOK_IMPORT_FAILED,
                        e.getMessage());
            }
        }
    }
}
