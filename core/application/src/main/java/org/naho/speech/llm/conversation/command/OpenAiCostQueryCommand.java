package org.naho.speech.llm.conversation.command;

import java.time.ZonedDateTime;

public class OpenAiCostQueryCommand {
    private final String timeframe;
    private final String granularity;
    private final ZonedDateTime fromDate;
    private final ZonedDateTime toDate;

    public OpenAiCostQueryCommand(String timeframe, String granularity, ZonedDateTime fromDate, ZonedDateTime toDate) {
        this.timeframe = timeframe;
        this.granularity = granularity;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public String getGranularity() {
        return granularity;
    }

    public ZonedDateTime getFromDate() {
        return fromDate;
    }

    public ZonedDateTime getToDate() {
        return toDate;
    }
}
