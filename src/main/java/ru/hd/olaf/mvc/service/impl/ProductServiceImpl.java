package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.controller.LoginController;
import ru.hd.olaf.mvc.repository.ProductRepository;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.LogUtil;

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

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Функция возвращает список всех Product текущего пользователя
     * @return
     */
    public List<Product> getAll() {
        logger.debug(LogUtil.getMethodName());
        return Lists.newArrayList(productRepository.findByUserId(securityService.findLoggedUser()));
    }

    /**
     * Функция возвращает product по id с проверкой на пользователя
     * @param id
     * @return
     */
    public Product getById(Integer id) {
        logger.debug(LogUtil.getMethodName());

        if (id == null) return null;

        Product product = productRepository.findOne(id);
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
        logger.debug(LogUtil.getMethodName());
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
        logger.debug(LogUtil.getMethodName());
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
        logger.debug(LogUtil.getMethodName());
        return productRepository.save(product);
    }

    /**
     * Функция возвращает запись по ее имени (поле name) либо создает новую и возвращает ее.
     * @param productName product.name
     * @return product
     */
    public Product getExistedOrCreated(String productName) {
        logger.debug(LogUtil.getMethodName());

        Product product;
        try {
            product = getByName(productName);
            logger.debug(String.format("Используется найденная запись: %s", product));
        } catch (Exception e) {
            logger.debug(String.format("Запись Product не найдена: %s.", e.getMessage()));

            product = new Product();
            product.setName(productName);
            product.setUserId(securityService.findLoggedUser());

            logger.debug(String.format("Создана новая запись Product: %s", product));

            save(product);
        }

        return product;
    }
}
