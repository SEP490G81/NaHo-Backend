package org.naho.book.adapter;

import org.naho.book.entity.BookEntity;
import org.naho.book.entity.TopicEntity;
import org.naho.book.mapper.TopicEntityMapper;
import org.naho.book.model.Topic;
import org.naho.book.mybatis.TopicQueryMapper;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.repository.BookJpaRepository;
import org.naho.book.repository.TopicJpaRepository;
import org.naho.file.entity.FileEntity;
import org.naho.file.repository.FileJpaRepository;
import org.naho.user.entity.UserEntity;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TopicRepositoryAdapter implements TopicRepositoryPort {

    private final TopicJpaRepository topicJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final FileJpaRepository fileJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final TopicEntityMapper topicEntityMapper;
    private final TopicQueryMapper topicQueryMapper;

    public TopicRepositoryAdapter(TopicJpaRepository topicJpaRepository,
                                  UserJpaRepository userJpaRepository,
                                  FileJpaRepository fileJpaRepository,
                                  BookJpaRepository bookJpaRepository, TopicEntityMapper topicEntityMapper, TopicQueryMapper topicQueryMapper) {
        this.topicJpaRepository = topicJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.fileJpaRepository = fileJpaRepository;
        this.bookJpaRepository = bookJpaRepository;
        this.topicEntityMapper = topicEntityMapper;
        this.topicQueryMapper = topicQueryMapper;
    }

    @Override
    public Topic save(Topic topic) {
        TopicEntity entity = new TopicEntity();

        if (topic.getId() != null) {
            entity.setId(topic.getId());
        }

        entity.setJapaneseName(topic.getJapaneseName());
        entity.setJapaneseDescription(topic.getJapaneseDescription());
        entity.setVietnameseDescription(topic.getVietnameseDescription());
        entity.setEnglishDescription(topic.getEnglishDescription());
        entity.setJapaneseNameMarkup(topic.getJapaneseNameMarkup());
        entity.setJapaneseDescriptionMarkup(topic.getJapaneseDescriptionMarkup());
        entity.setStatus(topic.getStatus());
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

        // Set Book
        if (topic.getBookId() != null) {
            BookEntity bookEntity = bookJpaRepository.getReferenceById(topic.getBookId());
            entity.setBook(bookEntity);
        }

        TopicEntity savedEntity = topicJpaRepository.save(entity);

        return topicEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public boolean existsByJapaneseNameAndBookId(String name, Long bookId) {
        return topicJpaRepository.existsByJapaneseNameAndBookId(name, bookId);
    }

    @Override
    public boolean existsByJapaneseNameAndBookIdExcludeId(String name, Long bookId, Long id) {
        return topicJpaRepository.existsByJapaneseNameAndBookIdAndIdNot(name, bookId, id);
    }

    @Override
    public Double getMaxOrderIndex() {
        return topicJpaRepository.getMaxOrderIndex();
    }

    @Override
    public Optional<Topic> findById(Long id) {
        return topicJpaRepository.findById(id).map(topicEntityMapper::entityToDomain);
    }

    @Override
    public Optional<Topic> findByObjectiveId(Long objectiveId) {
        return topicJpaRepository.findByObjectiveId(objectiveId).map(topicEntityMapper::entityToDomain);
    }

    @Override
    public void deleteById(Long id) {
        topicJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Topic> findBySpeakingQuestionId(Long speakingQuestionId) {
        return topicQueryMapper
                .findBySpeakingQuestionId(speakingQuestionId)
                .map(topicEntityMapper::entityToDomain);
    }
}
