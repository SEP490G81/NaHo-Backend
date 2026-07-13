package org.naho.book.port.out;

import org.naho.book.model.Topic;

import java.util.List;

public interface TopicListRepositoryPort {
    List<Topic> findAllByBookId(Long bookId);
}
