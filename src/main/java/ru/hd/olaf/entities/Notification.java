package ru.hd.olaf.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Olaf on 08.05.2017.
 */
@Entity
@Table(name = "notifications", schema = "web_account_db")
public class Notification {
    private Integer id;
    private String type;
    private Integer entityId;
    private String entityName;
    private BigDecimal sum;
    private Byte period;
    private User userId;

    @Id
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "type", nullable = true, length = 40)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "entity_id", nullable = true)
    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    @Basic
    @Column(name = "entity_name", nullable = true, length = 40)
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Basic
    @Column(name = "sum", nullable = true, precision = 2)
    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    @Basic
    @Column(name = "period", nullable = true)
    public Byte getPeriod() {
        return period;
    }

    public void setPeriod(Byte period) {
        this.period = period;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        if (id != that.id) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (entityId != null ? !entityId.equals(that.entityId) : that.entityId != null) return false;
        if (entityName != null ? !entityName.equals(that.entityName) : that.entityName != null) return false;
        if (sum != null ? !sum.equals(that.sum) : that.sum != null) return false;
        if (period != null ? !period.equals(that.period) : that.period != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (entityId != null ? entityId.hashCode() : 0);
        result = 31 * result + (entityName != null ? entityName.hashCode() : 0);
        result = 31 * result + (sum != null ? sum.hashCode() : 0);
        result = 31 * result + (period != null ? period.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
