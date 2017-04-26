package ru.hd.olaf.util;

/**
 * Created by d.v.hozyashev on 26.04.2017.
 */
public class LogUtil {

    public static String getMethodName(){
        return String.format("Function %s", Thread.currentThread().getStackTrace()[2].getMethodName());
    }
}
