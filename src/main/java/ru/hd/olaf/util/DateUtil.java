package ru.hd.olaf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Olaf on 30.04.2017.
 */
public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static LocalDate getParsedDate(String string){
        logger.debug(LogUtil.getMethodName());

        String delimiter = string.contains(".") ? "." : "-";
        DateTimeFormatter formatter;
        if (string.indexOf(delimiter) < 3)
            formatter = DateTimeFormatter.ofPattern("dd" + delimiter + "MM" + delimiter + "yyyy");
        else
            formatter = DateTimeFormatter.ofPattern("yyyy" + delimiter + "MM" + delimiter + "dd");

        LocalDate date = LocalDate.parse(string, formatter);

        logger.debug(String.format("Исходная строка до преобразования: %s, после: %s", string, date.toString()));
        return date;
    }

    public static String getFormattedDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        return date.format(formatter);
    }

    public static String getFormattedForFullCalendar(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return date.format(formatter);
    }

    public static Date getDate(LocalDate date){
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Функция возвращает дату начала текущей недели
     * @return LocalDate
     */
    public static LocalDate getStartOfWeek(){
        return LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().ordinal());
    }

    /**
     * Функция возвращает дату начала текущего месяца
     * @return LocalDate
     */
    public static LocalDate getStartOfMonth(){
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getStartOfEra(){
        return LocalDate.of(1900, 1, 1);
    }

    public static int getCountDaysInMonth(LocalDate date){
        logger.debug(LogUtil.getMethodName());

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();

        calendar.set(Calendar.YEAR, date.getYear());
        calendar.set(Calendar.MONTH, date.getMonthValue() - 1);

        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Date getDateOfStartOfEra(){
        return getDate(getStartOfEra());
    }
}
