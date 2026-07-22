package org.naho.daily.valueobject;

import org.naho.daily.exception.DailyRewardDomainErrorCode;
import org.naho.i18n.message.daily.DailyRewardDetailMessageKey;
import org.naho.shared.exception.DomainException;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class RewardYearMonth {
    private static final Pattern YEAR_MONTH_PATTERN =
            Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])$");

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM");

    private final String value;

    private RewardYearMonth(String value) {
        this.value = value;
    }

    public static RewardYearMonth of(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(
                    DailyRewardDomainErrorCode.DAILY_REWARD_YEAR_MONTH_REQUIRED,
                    DailyRewardDetailMessageKey.DAILY_REWARD_YEAR_MONTH_REQUIRED
            );
        }

        value = value.trim();

        if (!YEAR_MONTH_PATTERN.matcher(value).matches()) {
            throw new DomainException(
                    DailyRewardDomainErrorCode.DAILY_REWARD_YEAR_MONTH_INVALID_FORMAT,
                    DailyRewardDetailMessageKey.DAILY_REWARD_YEAR_MONTH_INVALID_FORMAT,
                    value
            );
        }

        return new RewardYearMonth(value);
    }

    public static RewardYearMonth of(YearMonth value) {
        if (value == null) {
            throw new DomainException(
                    DailyRewardDomainErrorCode.DAILY_REWARD_YEAR_MONTH_REQUIRED,
                    DailyRewardDetailMessageKey.DAILY_REWARD_YEAR_MONTH_REQUIRED
            );
        }

        return new RewardYearMonth(value.format(YEAR_MONTH_FORMATTER));
    }

    public String getValue() {
        return value;
    }
}
