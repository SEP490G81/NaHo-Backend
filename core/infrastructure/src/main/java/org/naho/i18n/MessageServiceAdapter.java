package org.naho.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceAdapter implements MessageService {

    private static final String NOT_FOUND_TEMPLATE = "key: {%s} not found!";

    private final MessageSource messageSource;

    @Override
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(
                key,
                args,
                NOT_FOUND_TEMPLATE.formatted(key),
                LocaleContextHolder.getLocale()
        );
    }
}