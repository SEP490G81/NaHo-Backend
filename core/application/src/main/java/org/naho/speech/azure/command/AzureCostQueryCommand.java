package org.naho.speech.azure.command;

import java.time.ZonedDateTime;

public class AzureCostQueryCommand {
    private final String timeframe;
    private final String granularity;
    private final ZonedDateTime fromDate;
    private final ZonedDateTime toDate;

    public AzureCostQueryCommand(String timeframe, String granularity, ZonedDateTime fromDate, ZonedDateTime toDate) {
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
