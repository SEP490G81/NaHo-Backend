package org.naho.topic.adapter;

import org.naho.file.model.FileEntity;
import org.naho.file.repository.FileJpaRepository;
import org.naho.topic.entity.TopicEntity;
import org.naho.topic.model.Topic;
import org.naho.topic.port.out.TopicRepositoryPort;
import org.naho.topic.repository.TopicJpaRepository;
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

        entity.setJapaneseName(topic.getJapaneseName());
        entity.setJapaneseDescription(topic.getJapaneseDescription());
        entity.setJapaneseNameMarkup(topic.getJapaneseNameMarkup());
        entity.setJapaneseDescriptionMarkup(topic.getJapaneseDescriptionMarkup());
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
                .japaneseName(savedEntity.getJapaneseName())
                .japaneseDescription(savedEntity.getJapaneseDescription())
                .japaneseNameMarkup(savedEntity.getJapaneseNameMarkup())
                .japaneseDescriptionMarkup(savedEntity.getJapaneseDescriptionMarkup())
                .status(savedEntity.getStatus())
                .jlptLevel(savedEntity.getJlptLevel())
                .orderIndex(savedEntity.getOrderIndex())
                .coverImageFileId(savedEntity.getCoverImageFile() != null ? savedEntity.getCoverImageFile().getId() : null)
                .build();
    }

    @Override
    public boolean existsByJapaneseNameAndJlptLevel(String japaneseName, JLPTLevel jlptLevel) {
        return topicJpaRepository.existsByJapaneseNameAndJlptLevel(japaneseName, jlptLevel);
    }

    @Override
    public boolean existsByJapaneseNameAndJlptLevelExcludeId(String japneseName, JLPTLevel jlptLevel, Long id) {
        return topicJpaRepository.existsByJapaneseNameAndJlptLevelAndIdNot(japneseName, jlptLevel, id);
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
                .japaneseName(savedEntity.getJapaneseName())
                .japaneseDescription(savedEntity.getJapaneseDescription())
                .japaneseNameMarkup(savedEntity.getJapaneseNameMarkup())
                .japaneseDescriptionMarkup(savedEntity.getJapaneseDescriptionMarkup())
                .status(savedEntity.getStatus())
                .jlptLevel(savedEntity.getJlptLevel())
                .orderIndex(savedEntity.getOrderIndex())
                .coverImageFileId(savedEntity.getCoverImageFile() != null ? savedEntity.getCoverImageFile().getId() : null)
                .build());
    }

    @Override
    public Optional<Topic> findByObjectiveId(Long objectiveId) {
        return topicJpaRepository.findByObjectiveId(objectiveId).map(savedEntity -> Topic.builder()
                .id(savedEntity.getId())
                .userId(savedEntity.getUser() != null ? savedEntity.getUser().getId() : null)
                .japaneseName(savedEntity.getJapaneseName())
                .japaneseDescription(savedEntity.getJapaneseDescription())
                .japaneseNameMarkup(savedEntity.getJapaneseNameMarkup())
                .japaneseDescriptionMarkup(savedEntity.getJapaneseDescriptionMarkup())
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
