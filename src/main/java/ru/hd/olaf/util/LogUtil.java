package ru.hd.olaf.util;

import org.slf4j.Logger;

import java.util.List;

/**
 * Created by d.v.hozyashev on 26.04.2017.
 */
public class LogUtil {

    public static String getMethodName(){
        return String.format("Function %s", Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    public static <T> void logList(Logger logger, List<T> list) {
        for (T T : list) {
            logger.debug(String.format("%s", T));
        }
    }
}
