package org.naho.shared.handler;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;
import org.jspecify.annotations.Nullable;
import org.naho.i18n.MessageService;
import org.naho.logging.ContextLoggingKey;
import org.naho.pagination.PageData;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.response.ApiMeta;
import org.naho.shared.response.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiResponseHandler implements ResponseBodyAdvice<Object> {
    private final MessageService messageService;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // nếu controller đã trả ra ApiResponse rồi thì không bọc nữa
        if (ApiResponse.class.isAssignableFrom(returnType.getParameterType())) {
            return false;
        }
        if (ProblemDetail.class.isAssignableFrom(returnType.getParameterType())) {
            return false;
        }
        if (ByteArrayHttpMessageConverter.class.isAssignableFrom(converterType)) {
            return false;
        }
        if (String.class.isAssignableFrom(returnType.getParameterType())) {
            return false;
        }
        return true;
    }

    @Override
    public @Nullable Object beforeBodyWrite(
            @Nullable Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response
    ) {
        if (body == null ||
                body instanceof ApiResponse<?> ||
                body instanceof ProblemDetail ||
                body instanceof ByteArrayHttpMessageConverter ||
                body instanceof String) {
            return body;
        }

        ApiResponseMessage apiResponseMessage = returnType.getMethodAnnotation(ApiResponseMessage.class);
        String message = messageService.getMessage(
                apiResponseMessage == null ? org.naho.i18n.message.common.CommonDetailMessageKey.COMMON_NO_MESSAGE
                        : apiResponseMessage.message()
        );

        String traceId = ThreadContext.get(ContextLoggingKey.TRACE_ID);

        if (body instanceof PageData<?>) {
            return ApiResponse.builder()
                    .meta(ApiMeta.createWithPagination(
                            traceId,
                            ((PageData<?>) body).getPageMeta()
                    ))
                    .message(message)
                    .data(((PageData<?>) body).getData())
                    .build();
        }

        return ApiResponse.builder()
                .meta(ApiMeta.create(traceId))
                .message(message)
                .data(body)
                .build();
    }
}
