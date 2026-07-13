package org.naho.book.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.book.entity.TopicEntity;
import org.naho.book.mapper.TopicEntityMapper;
import org.naho.book.model.Topic;
import org.naho.book.port.out.TopicListRepositoryPort;
import org.naho.book.repository.TopicJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TopicListRepositoryAdapter implements TopicListRepositoryPort {

    private final TopicJpaRepository topicJpaRepository;
    private final TopicEntityMapper topicEntityMapper;

    @Override
    public List<Topic> findAllByBookId(Long bookId) {
        List<TopicEntity> topicEntityList = topicJpaRepository.findAllByBook_Id(bookId);
        return topicEntityList
                .stream()
                .map(topicEntityMapper::entityToDomain)
                .toList();
    }
}
