package org.naho.speech.topic.adapter;

import org.naho.file.model.FileEntity;
import org.naho.file.repository.FileJpaRepository;
import org.naho.speech.azure.entity.TopicEntity;
import org.naho.speech.model.Topic;
import org.naho.speech.topic.port.out.TopicRepositoryPort;
import org.naho.speech.topic.repository.TopicJpaRepository;
import org.naho.user.entity.UserEntity;
import org.naho.user.repository.UserJpaRepository;
import org.naho.user.type.JLPTLevel;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TopicRepositoryAdapter implements TopicRepositoryPort {

    private final TopicJpaRepository topicJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final FileJpaRepository fileJpaRepository;

    public TopicRepositoryAdapter(TopicJpaRepository topicJpaRepository,
                                  UserJpaRepository userJpaRepository,
                                  FileJpaRepository fileJpaRepository) {
        this.topicJpaRepository = topicJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.fileJpaRepository = fileJpaRepository;
    }

    @Override
    public Topic save(Topic topic) {
        TopicEntity entity = new TopicEntity();

        // Cập nhật ID nếu là update
        if (topic.getId() != null) {
            entity.setId(topic.getId());
        }

        entity.setName(topic.getName());
        entity.setDescription(topic.getDescription());
        entity.setJapaneseNameTokens(topic.getJapaneseNameTokens());
        entity.setJapaneseDescriptionTokens(topic.getJapaneseDescriptionTokens());
        entity.setStatus(topic.getStatus());
        entity.setJlptLevel(topic.getJlptLevel());
        entity.setOrderIndex(topic.getOrderIndex());

        // Set User
        if (topic.getUserId() != null) {
            UserEntity userEntity = userJpaRepository.getReferenceById(topic.getUserId());
            entity.setUser(userEntity);
        }

        // Set Cover Image
        if (topic.getCoverImageFileId() != null) {
            FileEntity fileEntity = fileJpaRepository.getReferenceById(topic.getCoverImageFileId());
            entity.setCoverImageFile(fileEntity);
        }

        TopicEntity savedEntity = topicJpaRepository.save(entity);

        return Topic.builder()
                .id(savedEntity.getId())
                .userId(savedEntity.getUser() != null ? savedEntity.getUser().getId() : null)
                .name(savedEntity.getName())
                .description(savedEntity.getDescription())
                .japaneseNameTokens(savedEntity.getJapaneseNameTokens())
                .japaneseDescriptionTokens(savedEntity.getJapaneseDescriptionTokens())
                .status(savedEntity.getStatus())
                .jlptLevel(savedEntity.getJlptLevel())
                .orderIndex(savedEntity.getOrderIndex())
                .coverImageFileId(savedEntity.getCoverImageFile() != null ? savedEntity.getCoverImageFile().getId() : null)
                .build();
    }

    @Override
    public boolean existsByNameAndJlptLevel(String name, JLPTLevel jlptLevel) {
        return topicJpaRepository.existsByNameAndJlptLevel(name, jlptLevel);
    }

    @Override
    public boolean existsByNameAndJlptLevelExcludeId(String name, JLPTLevel jlptLevel, Long id) {
        return topicJpaRepository.existsByNameAndJlptLevelAndIdNot(name, jlptLevel, id);
    }

    @Override
    public Double getMaxOrderIndex() {
        return topicJpaRepository.getMaxOrderIndex();
    }


    @Override
    public Optional<Topic> findById(Long id) {
        return topicJpaRepository.findById(id).map(savedEntity -> Topic.builder()
                .id(savedEntity.getId())
                .userId(savedEntity.getUser() != null ? savedEntity.getUser().getId() : null)
                .name(savedEntity.getName())
                .description(savedEntity.getDescription())
                .japaneseNameTokens(savedEntity.getJapaneseNameTokens())
                .japaneseDescriptionTokens(savedEntity.getJapaneseDescriptionTokens())
                .status(savedEntity.getStatus())
                .jlptLevel(savedEntity.getJlptLevel())
                .orderIndex(savedEntity.getOrderIndex())
                .coverImageFileId(savedEntity.getCoverImageFile() != null ? savedEntity.getCoverImageFile().getId() : null)
                .build());
    }

    @Override
    public void deleteById(Long id) {
        topicJpaRepository.deleteById(id);
    }
}
