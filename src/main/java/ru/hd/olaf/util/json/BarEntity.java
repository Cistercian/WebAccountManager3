package ru.hd.olaf.util.json;

import java.math.BigDecimal;

/**
 * class for export raw data from db
 *
 * Created by d.v.hozyashev on 20.04.2017.
 */
public class BarEntity {
    private String type;
    private int id;
    private BigDecimal sum;
    private String name;
    private BigDecimal limit;

    public BarEntity(String type, int id, BigDecimal sum, String name) {
        this.type = type;
        this.id = id;
        this.sum = sum;
        this.name = name;
        this.limit = new BigDecimal("0");
    }

    public BarEntity(String type, int id, BigDecimal sum, String name, BigDecimal limit) {
        this.type = type;
        this.id = id;
        this.sum = sum;
        this.name = name;
        this.limit = limit;
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

    @Override
    public String toString() {
        return "BarEntity{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", sum=" + sum +
                ", name='" + name + '\'' +
                ", limit='" + limit + '\'' +
                '}';
    }
}
