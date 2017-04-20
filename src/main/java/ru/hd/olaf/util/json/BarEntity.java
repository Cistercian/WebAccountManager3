package ru.hd.olaf.util.json;

import java.math.BigDecimal;

/**
 * class for export raw data from db
 *
 * Created by d.v.hozyashev on 20.04.2017.
 */
public class BarEntity {
    private String className;
    private int id;
    private BigDecimal sum;
    private String name;

    public BarEntity(String className, int id, BigDecimal sum, String name) {
        this.className = className;
        this.id = id;
        this.sum = sum;
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    @Override
    public String toString() {
        return "BarEntity{" +
                "className='" + className + '\'' +
                ", id=" + id +
                ", sum=" + sum +
                ", name='" + name + '\'' +
                '}';
    }
}
