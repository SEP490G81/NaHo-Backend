package org.naho.cost.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.cost.llm.dto.request.OpenAiCostChartRequest;
import org.naho.speech.llm.command.OpenAiCostQueryCommand;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface OpenAiCostRequestMapper {

    @Mapping(target = "fromDate", expression = "java(parseDateTime(request.getFromDate()))")
    @Mapping(target = "toDate", expression = "java(parseDateTime(request.getToDate()))")
    OpenAiCostQueryCommand requestToCommand(OpenAiCostChartRequest request);

    default ZonedDateTime parseDateTime(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String cleaned = text.trim();
        try {
            return ZonedDateTime.parse(cleaned);
        } catch (Exception e1) {
            try {
                return LocalDateTime.parse(cleaned, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(ZoneOffset.UTC);
            } catch (Exception e2) {
                try {
                    return LocalDate.parse(cleaned, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(ZoneOffset.UTC);
                } catch (Exception e3) {
                    return null;
                }
            }
        }
    }
}
