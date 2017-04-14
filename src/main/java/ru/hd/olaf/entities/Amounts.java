package ru.hd.olaf.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * Created by Olaf on 11.04.2017.
 */
@Entity
@Table(name = "amounts", schema = "web_account_db")
public class Amounts {
    private Integer id;
    private Categories categoryId;
    private String name;
    private BigDecimal price;
    private Date amountsDate;
    private String details;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne()
    @JoinColumn(name = "category_id")
    public Categories getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Categories categoryId) {
        this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 40)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "price", nullable = false, precision = 2)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Basic
    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    public Date getAmountsDate() {
        return amountsDate;
    }

    public void setAmountsDate(Date amountsDate) {
        this.amountsDate = amountsDate;
    }

    @Basic
    @Column(name = "details", nullable = true, length = 256)
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Amounts amounts = (Amounts) o;

        if (id != null ? !id.equals(amounts.id) : amounts.id != null) return false;
        if (categoryId != null ? !categoryId.equals(amounts.categoryId) : amounts.categoryId != null) return false;
        if (name != null ? !name.equals(amounts.name) : amounts.name != null) return false;
        if (price != null ? !price.equals(amounts.price) : amounts.price != null) return false;
        if (amountsDate != null ? !amountsDate.equals(amounts.amountsDate) : amounts.amountsDate != null) return false;
        return details != null ? details.equals(amounts.details) : amounts.details == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (amountsDate != null ? amountsDate.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Amounts{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", date=" + amountsDate +
                ", details='" + details + '\'' +
                '}';
    }
}
