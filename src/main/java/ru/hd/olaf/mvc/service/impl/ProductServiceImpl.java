package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.controller.LoginController;
import ru.hd.olaf.mvc.repository.ProductRepository;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.time.LocalDate;
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
    @Autowired
    private AmountService amountService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Функция возвращает список всех Product текущего пользователя
     * @return
     */
    public List<Product> getAll() {
        logger.debug(LogUtil.getMethodName());
        return Lists.newArrayList(productRepository.findByUserId(securityService.findLoggedUser()));
    }

    public JsonResponse getById(Integer id) {
        logger.debug(LogUtil.getMethodName());

        Product product = null;

        JsonResponse jsonResponse = new JsonResponse();
        try {
            product = getOne(id);

            String message = String.format("Запись с id = %d найдена: %s", id, product);
            jsonResponse.setType(ResponseType.SUCCESS);
            jsonResponse.setMessage(message);
            jsonResponse.setEntity(product);
        } catch (AuthException e) {
            logger.debug(e.getMessage());
            jsonResponse.setType(ResponseType.ERROR);
            jsonResponse.setMessage(e.getMessage());
        } catch (IllegalArgumentException e) {
            String message = "Переданный параметр id равен null.";
            logger.debug(message);

            jsonResponse.setType(ResponseType.INFO);
            jsonResponse.setMessage(message);
        }

        return jsonResponse;
    }

    /**
     * Функция возвращает product по id с проверкой на пользователя
     * @param id
     * @return
     */
    private Product getOne(Integer id) throws AuthException, IllegalArgumentException {
        logger.debug(LogUtil.getMethodName());
        if (id == null) throw new IllegalArgumentException();

        Product product = productRepository.findOne(id);
        if (product == null) return null;

        if (!product.getUserId().equals(securityService.findLoggedUser()))
            throw new AuthException(String.format("Запрошенный объект с id %d Вам не принадлежит.", id));

        return product;
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
    public Product save(Product product) throws CrudException{
        logger.debug(LogUtil.getMethodName());

        Product entity;

        try {
            entity = productRepository.save(product);
        } catch (Exception e) {
            throw new CrudException(e.getMessage());
        }

        return entity;
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

            try {
                save(product);
            } catch (CrudException e1) {
                //TODO: throws?
            }
        }

        return product;
    }

    /**
     * Функция удаления записи из БД
     * @param product
     * @return JsonResponse
     */
    public JsonResponse delete(Product product) throws CrudException {
        try {
            if (amountService.getByProduct(product, LocalDate.MIN, LocalDate.MAX).size() > 0)
                return new JsonResponse(ResponseType.ERROR, "Откат удаления: обнаружены существующие записи amount " +
                        "с данной товарной группой");

            productRepository.delete(product.getId());
            return new JsonResponse(ResponseType.SUCCESS, "Удаление успешно завершено.");
        } catch (Exception e) {
            throw new CrudException(e.getMessage());
        }
    }
}
