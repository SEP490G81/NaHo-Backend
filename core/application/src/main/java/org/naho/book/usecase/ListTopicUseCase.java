package org.naho.book.usecase;

import org.naho.book.exception.TopicDomainErrorCode;
import org.naho.book.mapper.TopicResultMapper;
import org.naho.book.model.Topic;
import org.naho.book.port.in.ListTopicInputPort;
import org.naho.book.port.out.TopicListRepositoryPort;
import org.naho.book.result.TopicResult;
import org.naho.i18n.message.book.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import java.util.List;

public class ListTopicUseCase implements ListTopicInputPort {

    private final TopicListRepositoryPort topicListRepositoryPort;
    private final TopicResultMapper topicResultMapper;

    public ListTopicUseCase(
            TopicListRepositoryPort topicListRepositoryPort,
            TopicResultMapper topicResultMapper
    ) {
        this.topicListRepositoryPort = topicListRepositoryPort;
        this.topicResultMapper = topicResultMapper;
    }

    @Override
    public List<TopicResult> findAllByBookId(Long bookId) {
        if (bookId == null) {
            throw new ApplicationException(
                    TopicDomainErrorCode.BOOK_ID_EMPTY,
                    TopicDetailMessageKey.BOOK_ID_EMPTY
            );
        }

        List<Topic> topics = topicListRepositoryPort.findAllByBookId(bookId);

        return topics
                .stream()
                .map(topicResultMapper::domainToResult)
                .toList();
    }
}