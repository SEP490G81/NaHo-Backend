package org.naho.file.adapter;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.FileEntityMapper;
import org.naho.file.model.File;
import org.naho.file.model.FileEntity;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.repository.FileJpaRepository;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.question.entity.QuestionEntity;
import org.naho.shared.exception.InfrastructureException;
import org.naho.social.entity.CommentEntity;
import org.naho.social.report.entity.ReportEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileRepositoryAdapter implements FileRepositoryPort {

    private final FileJpaRepository fileJpaRepository;
    private final FileEntityMapper fileEntityMapper;
    private final EntityManager entityManager;

    @Override
    public String findObjectKeyById(Long id) {
        if (id == null) return null;
        FileEntity file = fileJpaRepository.findById(id)
                .orElseThrow(() -> new InfrastructureException(
                        FileErrorCode.FILE_NOT_FOUND,
                        FileDetailMessageKey.FILE_NOT_FOUND,
                        id
                ));
        return file.getObjectKey();
    }

    @Override
    public File save(File file) {
        if (file == null) return null;
        FileEntity fileEntity = fileEntityMapper.domainToEntity(file);

        if (file.getCommentId() != null) {
            fileEntity.setComment(entityManager.getReference(CommentEntity.class, file.getCommentId()));
        }
        if (file.getQuestionId() != null) {
            fileEntity.setQuestion(entityManager.getReference(QuestionEntity.class, file.getQuestionId()));
        }
        if (file.getReportId() != null) {
            fileEntity.setReport(entityManager.getReference(ReportEntity.class, file.getReportId()));
        }

        FileEntity savedFileEntity = fileJpaRepository.save(fileEntity);
        return fileEntityMapper.entityToDomain(savedFileEntity);
    }

    @Override
    public void deleteById(Long id) {
        fileJpaRepository.deleteById(id);
    }
}
