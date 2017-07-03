package ru.hd.olaf.util.json;

import ru.hd.olaf.util.FormatUtil;

import java.math.BigDecimal;

/**
 * class for export raw data from db
 * <p>
 * Created by d.v.hozyashev on 20.04.2017.
 */
public class BarEntity implements DBData {
    private String type;
    private int id;
    private BigDecimal sum;
    private String name;
    private BigDecimal limit;

    //added 06/06/17
    //поля для аналитики прогнозирования
    //TODO: redudant
    private BigDecimal oneTimeSum;
    private BigDecimal regularSum;

    public BarEntity(String type, int id, BigDecimal sum, String name) {
        this.type = type;
        this.id = id;
        this.sum = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.name = name;
        this.limit = new BigDecimal("0");
        this.oneTimeSum = new BigDecimal("0");
        this.regularSum = new BigDecimal("0");
    }

    public BarEntity(String type, int id, Double sum, String name) {
        this.type = type;
        this.id = id;
        this.sum = new BigDecimal(sum).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.name = name;
        this.limit = new BigDecimal("0");
        this.oneTimeSum = new BigDecimal("0");
        this.regularSum = new BigDecimal("0");
    }

    public BarEntity(String type, int id, BigDecimal sum, String name, BigDecimal limit) {
        this.type = type;
        this.id = id;
        this.sum = sum == null ? new BigDecimal("0") : sum;
        this.name = name;
        this.limit = limit == null ? new BigDecimal("0") : limit;
        ;
        this.oneTimeSum = new BigDecimal("0");
        this.regularSum = new BigDecimal("0");
    }

    public BarEntity(String type, int id, BigDecimal sum, String name, BigDecimal limit, BigDecimal oneTimeSum, BigDecimal regularSum) {
        this.type = type;
        this.id = id;
        this.sum = sum == null ? new BigDecimal("0") : sum;
        this.name = name;
        this.limit = limit == null ? new BigDecimal("0") : limit;
        ;
        this.oneTimeSum = oneTimeSum == null ? new BigDecimal("0") : oneTimeSum;
        this.regularSum = regularSum == null ? new BigDecimal("0") : regularSum;
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

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public String getFormattedSum() {
        return FormatUtil.numberToString(limit);
    }

    public String getFormattedLimit() {
        return FormatUtil.numberToString(limit);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BarEntity entity = (BarEntity) o;

        if (id != entity.id) return false;
        if (type != null ? !type.equals(entity.type) : entity.type != null) return false;
        return name != null ? name.equals(entity.name) : entity.name == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BarEntity{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", sum=" + sum +
                ", name='" + name + '\'' +
                ", limit='" + limit + '\'' +
                ", oneTimeSum='" + (oneTimeSum == null ? "0" : oneTimeSum) + '\'' +
                ", regularSum='" + (regularSum == null ? "0" : regularSum) + '\'' +
                '}';
    }


}
