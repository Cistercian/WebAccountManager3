package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Product;

import java.util.List;

/**
 * Created by d.v.hozyashev on 21.04.2017.
 */
public interface ProductService {

    List<Product> getAll();
    Product getById(Integer id);

    List<Product> getByContainedName(String name);
    Product getByName(String name);
    Product getExistedOrCreated(String productName);

    Product save(Product product);

}
