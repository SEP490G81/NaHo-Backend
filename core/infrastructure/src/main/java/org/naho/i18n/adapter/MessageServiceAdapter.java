package org.naho.i18n.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.MessageService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceAdapter implements MessageService {
    private final MessageSource messageSource;

    @Override
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(
                key,
                args,
                key,
                LocaleContextHolder.getLocale()
        );
    }
}