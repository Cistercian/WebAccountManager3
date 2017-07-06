package ru.hd.olaf.util;

import org.slf4j.Logger;

import java.util.List;

/**
 * Created by d.v.hozyashev on 26.04.2017.
 */
public class LogUtil {

    /**
     * Функция для записи в лог имени текущего метода
     *
     * @return имя метода
     */
    public static String getMethodName() {
        return String.format("Function %s", Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    /**
     * Функция выводит в лог переданный список по элементам
     *
     * @param logger текущий логер
     * @param list
     * @param <T>
     */
    public static <T> void logList(Logger logger, List<T> list) {
        logger.debug("Список данных:");
        for (T T : list) {
            logger.debug(String.format("%s", T));
        }
    }
}
