package ru.hd.olaf.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Olaf on 11.04.2017.
 */
@Entity
@Table(name = "categories", schema = "web_account_db")
public class Category {
    private Integer id;
    private Category parentId;
    private String name;
    private String details;
    private Byte type;
    private User userId;
    private Set<Amount> amounts = new HashSet<Amount>();
    private List<Limit> limits;

    public Category() {
    }

    public Category(Category parentId, String name, String details, Byte type) {
        this.parentId = parentId;
        this.name = name;
        this.details = details;
        this.type = type;
    }

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
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    public Category getParentId() {
        return parentId;
    }

    public void setParentId(Category parentId) {
        this.parentId = parentId;
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
    @Column(name = "details", nullable = true, length = 256)
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Basic
    @Column(name = "type", nullable = true)
    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "categoryId", cascade = CascadeType.REMOVE, orphanRemoval = false)
    @JsonBackReference
    public Set<Amount> getAmounts() {
        return amounts;
    }

    public void setAmounts(Set<Amount> amounts) {
        this.amounts = amounts;
    }

    public void addAmounts(Amount amount) {
        amount.setCategoryId(this);
        this.amounts.add(amount);
    }

    public void removeAmounts(Amount amount) {
        this.amounts.remove(amount);
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "categoryId", cascade = CascadeType.REMOVE, orphanRemoval = false)
    @JsonBackReference
    public List<Limit> getLimits() {
        return limits;
    }

    public void setLimits(List<Limit> limits) {
        this.limits = limits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category that = (Category) o;

        if (id != that.id) return false;
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (details != null ? !details.equals(that.details) : that.details != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", type=" + type +
                '}';
    }
}
