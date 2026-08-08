package org.naho.question.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.naho.question.constant.SpeakingHistorySortColumn;
import org.naho.shared.constant.SortDirection;

@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingHistoryQueryRequest {
    Integer page = 0;
    Integer size = 10;
    SpeakingHistorySortColumn sortColumn = SpeakingHistorySortColumn.CREATED_TIME;
    SortDirection sortDirection = SortDirection.DESC;
    Long speakingQuestionId;
    Long topicId;
    String search;
}
