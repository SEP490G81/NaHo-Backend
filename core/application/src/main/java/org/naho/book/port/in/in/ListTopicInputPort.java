package org.naho.book.port.in.in;

import org.naho.book.result.TopicResult;

import java.util.List;

public interface ListTopicInputPort {
    List<TopicResult> findAllByBookId(Long bookId);
}
