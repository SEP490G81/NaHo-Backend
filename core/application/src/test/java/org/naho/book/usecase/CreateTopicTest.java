package org.naho.book.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.command.CreateTopicCommand;
import org.naho.book.exception.TopicErrorCode;
import org.naho.book.model.Topic;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.result.CreateTopicResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTopicTest {

    @Mock
    private TopicRepositoryPort topicRepositoryPort;
    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private CreateTopicUseCase createTopicUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Tạo mới Topic thành công khi truyền thông tin hợp lệ")
    void UTCID01_CreateTopic_Success() {
        CreateTopicCommand command = new CreateTopicCommand(1L, "Mô tả tiếng Việt", "English desc", "Gia đình", "Mô tả", 10L, 1.0, 5L);
        Topic topic = Topic.builder().id(100L).japaneseName("Gia đình").bookId(10L).orderIndex(1.0).build();

        when(topicRepositoryPort.existsByJapaneseNameAndBookId("Gia đình", 10L)).thenReturn(false);
        when(topicRepositoryPort.save(any(Topic.class))).thenReturn(topic);

        CreateTopicResult result = createTopicUseCase.createTopic(command);

        assertNotNull(result);
        assertEquals(100L, result.id());
        verify(topicRepositoryPort, times(1)).save(any(Topic.class));
    }

    @Test
    @DisplayName("UTCID02 - Thất bại do Topic đã tồn tại trong Book")
    void UTCID02_CreateTopic_AlreadyExists() {
        CreateTopicCommand command = new CreateTopicCommand(1L, "Mô tả", "Desc", "Gia đình", "DescMarkup", 10L, 1.0, null);

        when(topicRepositoryPort.existsByJapaneseNameAndBookId("Gia đình", 10L)).thenReturn(true);

        ApplicationException ex = assertThrows(ApplicationException.class, () -> createTopicUseCase.createTopic(command));
        assertEquals(TopicErrorCode.TOPIC_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Thất bại do orderIndex không hợp lệ (< 0)")
    void UTCID03_CreateTopic_OrderIndexInvalid() {
        CreateTopicCommand command = new CreateTopicCommand(1L, "Mô tả", "Desc", "Gia đình", "DescMarkup", 10L, -1.0, null);

        when(topicRepositoryPort.existsByJapaneseNameAndBookId("Gia đình", 10L)).thenReturn(false);

        ApplicationException ex = assertThrows(ApplicationException.class, () -> createTopicUseCase.createTopic(command));
        assertEquals(TopicErrorCode.TOPIC_ORDER_INDEX_INVALID, ex.getErrorCode());
    }
}
