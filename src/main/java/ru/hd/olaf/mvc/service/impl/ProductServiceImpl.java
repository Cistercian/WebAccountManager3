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

    /**
     * Функция возвращает список всех Product текущего пользователя
     * @return
     */
    public List<Product> getAll() {
        return Lists.newArrayList(productRepository.findByUserId(securityService.findLoggedUser()));
    }

    /**
     * Функция возвращает product по id с проверкой на пользователя
     * @param id
     * @return
     */
    public Product getById(Integer id) {
        if (id == null) return null;

        Product product = productRepository.getOne(id);
        if (product == null) return null;
        return product.getUserId().equals(securityService.findLoggedUser()) ? product : null;
    }

    /**
     * Функция возвращает список Product текущего пользователя по маске наименования
     * (реализация быстрого поиска для выпадающего меню)
     * @param name
     * @return
     */
    public List<Product> getByContainedName(String name) {
        return productRepository.findByUserIdAndNameIgnoreCaseContaining(securityService.findLoggedUser(), name);
    }

    /**
     * Функция возврата записи product по его имени
     * (предполагаем, что поле product.name уникально для каждого пользователя)
     * @param name
     * @return
     * @throws NullPointerException
     */
    public Product getByName(String name) throws NullPointerException{
        Product product =
                productRepository.findByNameAndUserId(name, securityService.findLoggedUser()).get(0);

        return product;
    }

    /**
     * Сохранение записи
     * @param product
     * @return
     */
    public Product save(Product product) {

        return productRepository.save(product);
    }


}
