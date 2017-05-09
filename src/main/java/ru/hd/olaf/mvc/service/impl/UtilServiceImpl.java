package ru.hd.olaf.mvc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Notification;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.service.*;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

/**
 * Created by Olaf on 08.05.2017.
 */
@Service
public class UtilServiceImpl implements UtilService{
    @Autowired
    private AmountService amountService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(UtilServiceImpl.class);

    public JsonResponse getById(Class classez, Integer id){

        logger.debug(LogUtil.getMethodName());

        JsonResponse jsonResponse = new JsonResponse();
        String message = null;
        try {
            if (classez.isAssignableFrom(Amount.class)) {
                Amount amount = amountService.getOne(id);

                message = String.format("Запись с id = %d найдена: %s", id, amount);
                jsonResponse.setEntity(amount);
            } else if (classez.isAssignableFrom(Category.class)) {
                Category category = categoryService.getOne(id);

                message = String.format("Запись с id = %d найдена: %s", id, category);
                jsonResponse.setEntity(category);
            } else if (classez.isAssignableFrom(Product.class)) {
                Product product = productService.getOne(id);

                message = String.format("Запись с id = %d найдена: %s", id, product);
                jsonResponse.setEntity(product);
            } else if (classez.isAssignableFrom(Notification.class)) {
                Notification notification = notificationService.getOne(id);

                message = String.format("Запись с id = %d найдена: %s", id, notification);
                jsonResponse.setEntity(notification);
            }

            jsonResponse.setType(ResponseType.SUCCESS);
            jsonResponse.setMessage(message);
        } catch (AuthException e) {
            logger.debug(e.getMessage());
            jsonResponse.setType(ResponseType.ERROR);
            jsonResponse.setMessage(e.getMessage());
        } catch (IllegalArgumentException e) {
            message = "Переданный параметр id равен null.";
            logger.debug(message);

            jsonResponse.setType(ResponseType.INFO);
            jsonResponse.setMessage(message);
        }


        return jsonResponse;
    }

    /**
     * Функция сохранение сущностей БД
     * @param entity
     * @return
     */
    public JsonResponse saveEntity(Object entity) {
        try {
            if (entity.getClass().isAssignableFrom(Category.class)) {
                Category category = (Category) entity;
                categoryService.save(category);
            } else if (entity.getClass().isAssignableFrom(Amount.class)) {
                Amount amount = (Amount) entity;
                amountService.save(amount);
            } else if (entity.getClass().isAssignableFrom(Product.class)) {
                Product product = (Product) entity;
                productService.save(product);
            }
        } catch (CrudException e) {
            String message = String.format("Возникла ошибка при сохранении данных в БД. \n" +
                    "Error message: %s", e.getMessage());
            logger.error(message);
            logger.error(e.toString());

            return new JsonResponse(ResponseType.ERROR, message);
        }

        String message = "Запись успешно сохранена в БД.";
        logger.debug(message);

        return new JsonResponse(ResponseType.SUCCESS, message);
    }
}
