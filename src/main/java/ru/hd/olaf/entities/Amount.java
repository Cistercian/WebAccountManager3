package ru.hd.olaf.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import ru.hd.olaf.util.DateUtil;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


/**
 * Created by Olaf on 11.04.2017.
 */
@Entity
@Table(name = "amounts", schema = "web_account_db")
public class Amount {
    private Integer id;
    private Category categoryId;
    private String name;
    private BigDecimal price;
    private Date date;
    private String details;
    private User userId;
    private Product productId;
    private Limit limit;
    /**
     * type - тип оборота
     * 0 - default;
     * 1 - единоразовый (непрогнозируемый на конец месяца - берется просто сумма на текущее число);
     * 2 - единоразовый (непрогнозируемый на конец месяца) и исключенный из подсчета среднего за предыдущие месяцы;
     * 3 - обязательный (будущий, запланированный) оборот (берется при расчете прогнозируемой суммы за месяц).
     */
    private Byte type;
    private Amount regularId;

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
    @JsonBackReference
    public Category getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Category categoryId) {
        this.categoryId = categoryId;
    }

    @ManyToOne()
    @JoinColumn(name = "user_id")
    @JsonBackReference
    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    @ManyToOne()
    @JoinColumn(name = "product_id")
    @JsonBackReference
    public Product getProductId() {
        return productId;
    }

    public void setProductId(Product productId) {
        this.productId = productId;
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
    public Date getDate() {
        return date;
    }

    public void setDate(Date amountsDate) {
        this.date = amountsDate;
    }

    @Basic
    @Column(name = "details", nullable = true, length = 256)
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Basic
    @Column(name = "type", nullable = false)
    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    @ManyToOne()
    @JoinColumn(name = "regular_id")
    @JsonBackReference
    public Amount getRegularId() {
        return regularId;
    }

    public void setRegularId(Amount regularId) {
        this.regularId = regularId;
    }

    @Transient
    public LocalDate getLocalDate(){
        //convert amounts.date to LocalDate
        Date date = new Date(this.date.getTime());
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @ManyToOne()
    @JsonBackReference
    @Transient
    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public Amount cloneToRegular(){
        Amount regular = new Amount();

        regular.setType((byte) 2);
        regular.setName(name);
        regular.setProductId(productId);
        regular.setCategoryId(categoryId);
        regular.setPrice(price);
        regular.setDetails("Обязательный платеж для анализа прогнозируемого результата. " + details);
        regular.setUserId(userId);
        regular.setDate(DateUtil.getDateOfStartOfEra());

        return regular;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Amount amount = (Amount) o;

        if (id != null ? !id.equals(amount.id) : amount.id != null) return false;
        if (categoryId != null ? !categoryId.equals(amount.categoryId) : amount.categoryId != null) return false;
        if (name != null ? !name.equals(amount.name) : amount.name != null) return false;
        if (price != null ? !price.equals(amount.price) : amount.price != null) return false;
        if (date != null ? !date.equals(amount.date) : amount.date != null) return false;
        return details != null ? details.equals(amount.details) : amount.details == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Amount{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", amountsDate=" + date +
                ", details='" + details + '\'' +
                '}';
    }
}
