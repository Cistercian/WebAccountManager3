package ru.hd.olaf.util.json;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by d.v.hozyashev on 08.06.2017.
 */
public class AnalyticData implements DbData{
    private String type;
    private int id;
    private BigDecimal avgSum;
    private String name;
    private Date maxDate;
    private Date minDate;
    private BigDecimal currentSum;


    public AnalyticData(String type, int id, BigDecimal avgSum, String name, Date maxDate, Date minDate) {
        this.type = type;
        this.id = id;
        this.avgSum = avgSum;
        this.name = name;
        this.maxDate = maxDate;
        this.minDate = minDate;
        currentSum = new BigDecimal("0");
    }

    public BigDecimal getCurrentSum() {
        return currentSum;
    }

    public void setCurrentSum(BigDecimal currentSum) {
        this.currentSum = currentSum != null ? currentSum : new BigDecimal("0");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getAvgSum() {
        return avgSum;
    }

    public void setAvgSum(BigDecimal avgSum) {
        this.avgSum = avgSum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalyticData that = (AnalyticData) o;

        if (id != that.id) return false;
        return type != null ? type.equals(that.type) : that.type == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "AnalyticData{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", avgSum=" + avgSum +
                ", name='" + name + '\'' +
                ", maxDate=" + maxDate +
                ", minDate=" + minDate +
                ", currentSum=" + currentSum +
                '}';
    }
}
