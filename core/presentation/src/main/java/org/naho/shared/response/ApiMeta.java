package org.naho.shared.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.naho.pagination.PageMeta;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiMeta {
    String traceId;
    Instant timestamp;
    PageMeta pageMeta;

    private ApiMeta(String traceId, Instant timestamp, PageMeta pageMeta) {
        this.traceId = traceId;
        this.timestamp = timestamp;
        this.pageMeta = pageMeta;
    }

    public static ApiMeta create(String traceId) {
        return new ApiMeta(traceId, Instant.now(), null);
    }

    public static ApiMeta createWithPagination(String traceId, PageMeta pageMeta) {
        return new ApiMeta(traceId, Instant.now(), pageMeta);
    }
}
