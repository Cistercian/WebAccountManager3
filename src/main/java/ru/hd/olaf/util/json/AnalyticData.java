package ru.hd.olaf.util.json;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by d.v.hozyashev on 08.06.2017.
 */
public class AnalyticData {
    private String type;
    private Integer id;
    private BigDecimal avgSum;
    private String name;
    private Date maxDate;
    private Date minDate;
    private BigDecimal currentSum;


    public AnalyticData(String type, Integer id, BigDecimal avgSum, String name, Date maxDate, Date minDate, BigDecimal currentSum) {
        this.type = type;
        this.id = id;
        this.avgSum = avgSum;
        this.name = name;
        this.maxDate = maxDate;
        this.minDate = minDate;
        this.currentSum = currentSum;
    }
}
