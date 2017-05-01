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

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    public static LocalDate getParsedDate(String string){
        logger.debug(LogUtil.getMethodName());

        String delimiter = string.contains(".") ? "." : "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd" + delimiter + "MM" + delimiter + "yyyy");

        LocalDate date = LocalDate.parse(string, formatter);

        return date;
    }
}
