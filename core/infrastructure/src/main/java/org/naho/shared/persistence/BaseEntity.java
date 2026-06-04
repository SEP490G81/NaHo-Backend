package org.naho.shared.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@SuperBuilder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
@NoArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, updatable = false, name = "created_time")
    Instant createdTime;

    @Column(insertable = false, name = "modified_time")
    Instant modifiedTime;

    @PrePersist
    private void handlePrePersist() {
        createdTime = Instant.now();
    }

    @PreUpdate
    private void handlePreUpdate() {
        modifiedTime = Instant.now();
    }
}
