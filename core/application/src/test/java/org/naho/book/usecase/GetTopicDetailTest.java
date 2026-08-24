package org.naho.book.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.command.GetTopicDetailCommand;
import org.naho.book.exception.TopicErrorCode;
import org.naho.book.model.Lesson;
import org.naho.book.model.Topic;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.result.TopicDetailResult;
import org.naho.shared.exception.ApplicationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTopicDetailTest {

    @Mock
    private TopicRepositoryPort topicRepositoryPort;
    @Mock
    private LessonRepositoryPort lessonRepositoryPort;

    @InjectMocks
    private GetTopicDetailUseCase getTopicDetailUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy chi tiết Topic thành công")
    void UTCID01_GetTopicDetail_Success() {
        GetTopicDetailCommand command = new GetTopicDetailCommand(1L);
        Topic topic = Topic.builder().id(1L).japaneseName("Topic 1").bookId(10L).orderIndex(1.0).build();
        Lesson lesson = Lesson.builder().id(10L).japaneseName("Lesson 1").orderIndex(1.0).build();

        when(topicRepositoryPort.findById(1L)).thenReturn(Optional.of(topic));
        when(lessonRepositoryPort.findByTopicId(1L)).thenReturn(List.of(lesson));

        TopicDetailResult result = getTopicDetailUseCase.getTopicDetail(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1, result.lessons().size());
    }

    @Test
    @DisplayName("UTCID02 - Lấy chi tiết Topic thất bại khi không tìm thấy Topic")
    void UTCID02_GetTopicDetail_NotFound() {
        GetTopicDetailCommand command = new GetTopicDetailCommand(99L);
        when(topicRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> getTopicDetailUseCase.getTopicDetail(command));
        assertEquals(TopicErrorCode.TOPIC_NOT_FOUND, ex.getErrorCode());
    }
}
