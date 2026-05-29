package org.naho.config.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class LocaleResolverConfig {

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();

        Locale vietnamese = Locale.forLanguageTag("vi");

        resolver.setDefaultLocale(vietnamese);
        resolver.setSupportedLocales(List.of(
                vietnamese,
                Locale.ENGLISH
        ));

        return resolver;
    }
}
