package org.naho.config.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {
    private static final String DEFAULT_ENCODING = "UTF-8";

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();

        messageSource.setBasenames(
                "classpath:i18n/common/common",
                "classpath:i18n/user/user",
                "classpath:i18n/speech/speech",
                "classpath:i18n/validation/validation",
                "classpath:i18n/exception/exception"
        );

        messageSource.setDefaultEncoding(DEFAULT_ENCODING);
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(true);

        return messageSource;
    }
}
