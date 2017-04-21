package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.repository.ProductRepository;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.SecurityService;

import java.util.List;

/**
 * Created by d.v.hozyashev on 21.04.2017.
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SecurityService securityService;

    public List<Product> getAll() {
        return Lists.newArrayList(productRepository.findByUserId(securityService.findLoggedUser()));
    }

    public Product getById(Integer id) {
        return productRepository.findOne(id);
    }

    public List<Product> getByContainedName(String name) {
        return productRepository.findByNameIgnoreCaseContaining(name);
    }

    public Product getByName(String name) throws NullPointerException{
        Product product = null;
        product = productRepository.findByName(name).get(0);

        return product;
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }


}
