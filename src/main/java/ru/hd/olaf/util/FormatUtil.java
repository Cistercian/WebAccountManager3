package ru.hd.olaf.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by d.v.hozyashev on 09.06.2017.
 */
public class FormatUtil {
    public static String formatToString(BigDecimal number) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.getDefault());
        dfs.setDecimalSeparator('.');
        dfs.setGroupingSeparator(' ');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", dfs);

        return decimalFormat.format(number);
    }
}
