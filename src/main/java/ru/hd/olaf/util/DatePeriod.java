package ru.hd.olaf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hd.olaf.mvc.controller.LoginController;

import java.time.LocalDate;

/**
 * Created by Olaf on 23.04.2017.
 */
public enum DatePeriod {
    DAY,
    WEEK,
    MONTH,
    CUSTOM,
    ALL;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    /**
     * Функция парсинга переданного в запросе периода (see DatePeriod)
     *
     * @param period see DatePeriod
     * @param today LocalDate
     * @return LocalDate value
     */
    public static LocalDate getAfterDate(String period, LocalDate today, Integer countDays) {
        logger.debug(LogUtil.getMethodName());

        DatePeriod datePeriod = null;
        countDays = countDays == null ? 0 : countDays;
        try {
            datePeriod = DatePeriod.valueOf(period.toUpperCase());
            logger.debug(String.format("Period: %s", datePeriod));
        } catch (IllegalArgumentException e) {
            datePeriod = DatePeriod.MONTH;
            logger.debug(String.format("Can't parsed param Period. Request: %s, using default value: %s", period, datePeriod));
        }

        LocalDate after = null;
        switch (datePeriod) {
            case DAY:
                after = today;
                break;
            case WEEK:
                after = today.minusDays(today.getDayOfWeek().ordinal());
                break;
            case ALL:
                after = LocalDate.MIN;
                break;
            case CUSTOM:
                after = today.minusDays(countDays - 1);
                break;
            case MONTH:
            default:
                after = today.minusDays(today.getDayOfMonth());
                break;
        }
        return after;
    }
}
