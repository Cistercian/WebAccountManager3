package ru.hd.olaf.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by d.v.hozyashev on 21.04.2017.
 */
@Entity
@Table(name = "products", schema = "web_account_db")
public class Product {
    private Integer id;
    private String name;
    private User userId;
    private Set<Amount> amounts = new HashSet<Amount>();
    private List<Limit> limits;

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
    @Column(name = "name", nullable = true, length = 40)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "productId", cascade = CascadeType.REMOVE, orphanRemoval = false)
    @JsonBackReference
    public Set<Amount> getAmounts() {
        return amounts;
    }

    public void setAmounts(Set<Amount> amounts) {
        this.amounts = amounts;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "productId", cascade = CascadeType.REMOVE, orphanRemoval = false)
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

        Product product = (Product) o;

        if (id != product.id) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        if (userId != null ? !userId.equals(product.userId) : product.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId.getId() + ":" + userId.getUsername() +
                '}';
    }
}
