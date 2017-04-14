package ru.hd.olaf.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Olaf on 11.04.2017.
 */
@Entity
@Table(name = "categories", schema = "web_account_db", catalog = "")
public class Categories {
    private Integer id;
    private Integer parentId;
    private String name;
    private String details;
    private Byte type;

    public Categories() {
    }

    public Categories(Integer id, Integer parentId, String name, String details, Byte type) {
        this.id = id;
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

    @Basic
    @Column(name = "parent_id", nullable = true)
    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
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

    private Set<Amounts> amounts = new HashSet<Amounts>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "categoryId", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonBackReference
    public Set<Amounts> getAmounts() {
        return amounts;
    }

    public void setAmounts(Set<Amounts> amounts) {
        this.amounts = amounts;
    }

    public void addAmounts(Amounts amounts) {
        amounts.setCategoryId(this);
        this.amounts.add(amounts);
    }

    public void removeAmounts(Amounts amounts) {
        this.amounts.remove(amounts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Categories that = (Categories) o;

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
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Categories{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", type=" + type +
                '}';
    }
}
