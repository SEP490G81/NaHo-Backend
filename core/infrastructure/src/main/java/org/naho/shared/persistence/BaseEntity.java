package org.naho.shared.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@SuperBuilder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @CreatedBy
    @Column(nullable = false, updatable = false, name = "created_by")
    Long createdBy;

    @Column(nullable = false, updatable = false, name = "created_time")
    Instant createdTime;

    @LastModifiedBy
    @Column(insertable = false, name = "modified_by")
    Long modifiedBy;

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
