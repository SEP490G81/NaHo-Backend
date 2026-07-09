package org.naho.point.mapper;

import org.naho.point.command.PointHistoryCommand;
import org.naho.point.model.PointHistory;

import java.time.Instant;

public class PointHistoryCommandMapper {
    public PointHistory commandToDomain(PointHistoryCommand command, Instant transactionTime) {
        if (command == null) {
            return null;
        }
        return PointHistory.builder()
                .id(command.id())
                .userId(command.userId())
                .questionId(command.questionId())
                .objectiveId(command.objectiveId())
                .lessonId(command.lessonId())
                .topicId(command.topicId())
                .point(command.point())
                .transactionType(command.transactionType())
                .transactionTime(transactionTime)
                .build();
    }
}
