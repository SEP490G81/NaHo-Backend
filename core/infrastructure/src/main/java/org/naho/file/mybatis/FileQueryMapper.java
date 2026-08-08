package org.naho.file.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.file.entity.FileEntity;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Mapper
public interface FileQueryMapper {
    List<FileEntity> findAllForSchedulerRetryUpload(
            @Param("now") Instant now,
            @Param("operationType") OperationType operationType,
            @Param("operationStatus") OperationStatus operationStatus,
            @Param("maxRetryCount") int maxRetryCount
    );

    Optional<FileEntity> findAvatarFileByUserId(@Param("userId") Long userId);
}
