package org.naho.file.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.naho.shared.persistence.BaseEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file_operations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileOperationEntity extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "file_id", nullable = false, unique = true)
    FileEntity file;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    OperationType operationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_status", nullable = false)
    OperationStatus operationStatus;

    @Column(name = "retry_count", nullable = false)
    Integer retryCount;
}
