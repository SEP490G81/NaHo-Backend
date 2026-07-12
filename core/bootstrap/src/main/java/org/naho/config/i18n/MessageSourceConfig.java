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
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasenames(
                "classpath:i18n/common/common",
                "classpath:i18n/user/user",
                "classpath:i18n/social/social",
                "classpath:i18n/speech/speech",
                "classpath:i18n/file/file",
                "classpath:i18n/llm/llm",
                "classpath:i18n/furigana/furigana",
                "classpath:i18n/point/point",
                "classpath:i18n/book/book",
                "classpath:i18n/topic/topic",
                "classpath:i18n/lesson/lesson",
                "classpath:i18n/objective/objective"

        );

        messageSource.setDefaultEncoding(DEFAULT_ENCODING);
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(true);

        return messageSource;
    }
}
