package ru.hd.olaf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hd.olaf.mvc.controller.IndexController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Olaf on 30.04.2017.
 */
public class ParseUtil {

    private static final Logger logger = LoggerFactory.getLogger(ParseUtil.class);

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
}
