package org.naho.speech.llm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.naho.shared.constant.SortDirection;
import org.naho.speech.llm.constant.SpeakingHistorySortColumn;

@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingSessionQueryRequest {
    Integer page = 0;
    Integer size = 10;
    SpeakingHistorySortColumn sortColumn = SpeakingHistorySortColumn.CREATED_TIME;
    SortDirection sortDirection = SortDirection.DESC;
    Long personaId;
    String sessionType;
    String search;
}
