package ru.hd.olaf.util.json;

import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.util.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by Olaf on 05.06.2017.
 */
public class CompareEntity {
    private BigDecimal minSum;
    private Date minDate;
    private BigDecimal maxSum;
    private Date maxDate;
    private BigDecimal avgSum;
    private Date lastDate;
    private BigDecimal lastSum;
    private List<Amount> amounts;

    public CompareEntity(BigDecimal minSum, Date minDate, BigDecimal maxSum, Date maxDate, BigDecimal avgSum, List<Amount> amounts) {
        this.minSum = minSum;
        this.minDate = minDate;
        this.maxSum = maxSum;
        this.maxDate = maxDate;
        this.avgSum = avgSum;
        this.amounts = amounts;
    }

    public List<Amount> getAmounts() {
        return amounts;
    }

    public void setAmounts(List<Amount> amounts) {
        this.amounts = amounts;
    }

    public BigDecimal getMinSum() {
        return minSum;
    }

    public void setMinSum(BigDecimal minSum) {
        this.minSum = minSum;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public BigDecimal getMaxSum() {
        return maxSum;
    }

    public void setMaxSum(BigDecimal maxSum) {
        this.maxSum = maxSum;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public BigDecimal getAvgSum() {
        return avgSum;
    }

    public void setAvgSum(BigDecimal avgSum) {
        this.avgSum = avgSum;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public BigDecimal getLastSum() {
        return lastSum;
    }

    public void setLastSum(BigDecimal lastSum) {
        this.lastSum = lastSum;
    }
}
