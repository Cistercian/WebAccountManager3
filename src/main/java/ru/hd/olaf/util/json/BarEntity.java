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
        this.sum = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.name = name;
        this.limit = new BigDecimal("0");
    }

    public BarEntity(String type, int id, Double sum, String name) {
        this.type = type;
        this.id = id;
        this.sum = new BigDecimal(sum).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.name = name;
        this.limit = new BigDecimal("0");
    }

    public BarEntity(String type, int id, BigDecimal sum, String name, BigDecimal limit) {
        this.type = type;
        this.id = id;
        this.sum = sum == null ? new BigDecimal("0") : sum;
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
                '}';
    }


}
