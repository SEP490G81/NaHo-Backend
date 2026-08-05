package org.naho.file.mapper;

import org.mapstruct.Mapper;
import org.naho.file.valueobject.NextRetryAt;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface FileValueObjectMapper {
    default NextRetryAt instantToNextRetryAt(Instant value) {
        return NextRetryAt.of(value);
    }

    default Instant nextRetryAtToInstant(NextRetryAt nextRetryAt) {
        return nextRetryAt != null ? nextRetryAt.getValue() : null;
    }
}
