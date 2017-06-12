package ru.hd.olaf.util.json;

import ru.hd.olaf.util.FormatUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by d.v.hozyashev on 08.06.2017.
 */
public class AnalyticEntity implements DBData {
    private String type;
    private int id;
    private BigDecimal avgSum;
    private String name;
    private Date maxDate;
    private Date minDate;
    private BigDecimal currentSum;

    //added 06/06/17
    //поля для аналитики прогнозирования
    private BigDecimal oneTimeSum;
    private BigDecimal regularSum;

    public AnalyticEntity(String type, int id, BigDecimal avgSum, String name, Date maxDate, Date minDate) {
        this.type = type;
        this.id = id;
        this.avgSum = avgSum;
        this.name = name;
        this.maxDate = maxDate;
        this.minDate = minDate;
        currentSum = new BigDecimal("0");
        this.oneTimeSum = new BigDecimal("0");
        this.regularSum = new BigDecimal("0");
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

    public BigDecimal getOneTimeSum() {
        return oneTimeSum;
    }

    public void setOneTimeSum(BigDecimal oneTimeSum) {
        this.oneTimeSum = oneTimeSum;
    }

    public BigDecimal getRegularSum() {
        return regularSum;
    }

    public void setRegularSum(BigDecimal regularSum) {
        this.regularSum = regularSum;
    }

    public BigDecimal getSum() {
        return getCurrentSum();
    }

    public BigDecimal getLimit() {
        return getAvgSum();
    }

    public String getFormattedSum() {
        return FormatUtil.formatToString(getCurrentSum());
    }

    public String getFormattedLimit() {
        return FormatUtil.formatToString(getAvgSum());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalyticEntity that = (AnalyticEntity) o;

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
        return "AnalyticEntity{" +
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
