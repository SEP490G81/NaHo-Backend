package org.naho.book.usecase;

import org.naho.book.command.GetTopicDetailCommand;
import org.naho.book.exception.TopicErrorCode;
import org.naho.book.model.Topic;
import org.naho.book.port.in.GetTopicDetailInputPort;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.result.LessonListItemResult;
import org.naho.book.result.TopicDetailResult;
import org.naho.i18n.message.book.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

public class GetTopicDetailUseCase implements GetTopicDetailInputPort {

    private final TopicRepositoryPort topicRepositoryPort;
    private final LessonRepositoryPort lessonRepositoryPort;

    public GetTopicDetailUseCase(TopicRepositoryPort topicRepositoryPort, LessonRepositoryPort lessonRepositoryPort) {
        this.topicRepositoryPort = topicRepositoryPort;
        this.lessonRepositoryPort = lessonRepositoryPort;
    }

    @Override
    public TopicDetailResult getTopicDetail(GetTopicDetailCommand command) {
        Topic topic = topicRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        TopicErrorCode.TOPIC_NOT_FOUND,
                        TopicDetailMessageKey.TOPIC_ID_NOT_FOUND,
                        command.id()));

        var lessons = lessonRepositoryPort.findByTopicId(command.id()).stream()
                .map(lesson -> new LessonListItemResult(
                        lesson.getId(),
                        lesson.getJapaneseName(),
                        lesson.getJapaneseDescription(),
                        lesson.getJapaneseNameMarkup(),
                        lesson.getJapaneseDescriptionMarkup(),
                        lesson.getStatus(),
                        lesson.getOrderIndex(),
                        lesson.getFirstNodeGlobalOrderIndex(),
                        lesson.getLastNodeGlobalOrderIndex()
                ))
                .toList();

        return new TopicDetailResult(
                topic.getId(),
                topic.getUserId(),
                topic.getJapaneseName(),
                topic.getJapaneseDescription(),
                topic.getVietnameseDescription(),
                topic.getEnglishDescription(),
                topic.getJapaneseNameMarkup(),
                topic.getJapaneseDescriptionMarkup(),
                topic.getStatus(),
                topic.getOrderIndex(),
                topic.getFirstNodeGlobalOrderIndex(),
                topic.getLastNodeGlobalOrderIndex(),
                topic.getCoverImageFileId(),
                lessons
        );
    }
}
