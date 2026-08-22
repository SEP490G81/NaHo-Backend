package org.naho.book.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.exception.TopicDomainErrorCode;
import org.naho.book.mapper.TopicResultMapper;
import org.naho.book.model.Topic;
import org.naho.book.port.out.TopicListRepositoryPort;
import org.naho.book.result.TopicResult;
import org.naho.shared.exception.ApplicationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListTopicTest {

    @Mock
    private TopicListRepositoryPort topicListRepositoryPort;
    @Mock
    private TopicResultMapper topicResultMapper;

    @InjectMocks
    private ListTopicUseCase listTopicUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách Topic theo Book ID thành công")
    void UTCID01_FindAllByBookId_Success() {
        Topic topic = Topic.builder().id(1L).japaneseName("Topic 1").bookId(10L).orderIndex(1.0).build();
        TopicResult topicResult = TopicResult.builder().id(1L).japaneseName("Topic 1").build();

        when(topicListRepositoryPort.findAllByBookId(10L)).thenReturn(List.of(topic));
        when(topicResultMapper.domainToResult(any())).thenReturn(topicResult);

        List<TopicResult> result = listTopicUseCase.findAllByBookId(10L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách Topic thất bại do bookId rỗng")
    void UTCID02_FindAllByBookId_BookIdNull() {
        ApplicationException ex = assertThrows(ApplicationException.class, () -> listTopicUseCase.findAllByBookId(null));
        assertEquals(TopicDomainErrorCode.BOOK_ID_EMPTY, ex.getErrorCode());
    }
}
