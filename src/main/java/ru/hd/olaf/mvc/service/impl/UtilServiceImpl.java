package ru.hd.olaf.mvc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.*;
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
    private LimitService limitService;
    @Autowired
    private MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(UtilServiceImpl.class);



    /**
     * Функция поиска объекта с форматированным отвветом в виде объекта JsonResponse
     * @param classez Класс искомого объекта
     * @param id id сущности
     * @return JsonResponse
     */
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
            } else if (classez.isAssignableFrom(Limit.class)) {
                Limit limit = limitService.getOne(id);

                message = String.format("Запись с id = %d найдена: %s", id, limit);
                jsonResponse.setEntity(limit);
            } else if (classez.isAssignableFrom(Mail.class)) {
                Mail mail = mailService.getOne(id);

                message = String.format("Запись с id = %d найдена: %s", id, mail);
                jsonResponse.setEntity(mail);
            }

            jsonResponse.setType(ResponseType.SUCCESS);
            jsonResponse.setMessage(message);
        } catch (AuthException e) {
            logger.debug(e.getMessage());
            jsonResponse.setType(ResponseType.ERROR);
            jsonResponse.setMessage(e.getMessage());
        } catch (IllegalArgumentException e) {
            //ситуацию не считаем ошибкой, т.к. этот случай возможен при view окна создания сущности.
            //Но отмечаем тип JsonResponse
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
        logger.debug(LogUtil.getMethodName());

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
            } else if (entity.getClass().isAssignableFrom(Limit.class)) {
                Limit limit = (Limit) entity;
                limitService.save(limit);
            } else if (entity.getClass().isAssignableFrom(Mail.class)) {
                Mail mail = (Mail) entity;
                mailService.save(mail);
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

    /**
     * функция удаления сущности БД
     * @param className наименование класса сущности (string)
     * @param id id сущности
     * @return JsonResponse
     */
    public JsonResponse deleteEntity(String className, Integer id) {
        logger.debug(LogUtil.getMethodName());
        JsonResponse response = new JsonResponse();

        try {
            if (className.equalsIgnoreCase(Amount.class.getSimpleName())) {
                logger.debug("Инициализация удаления записи Amount");

                response = amountService.getById(id);
                if (response.getEntity() == null) {
                    logger.debug(response.getType() + ":" + response.getMessage());
                    return response;
                }
                response = amountService.delete((Amount) response.getEntity());

            } else if (className.equalsIgnoreCase(Category.class.getSimpleName())) {
                logger.debug("Инициализация удаления записи Category");

                response = categoryService.getById(id);
                if (response.getEntity() == null) {
                    logger.debug(response.getType() + ":" + response.getMessage());
                    return response;
                }
                response = categoryService.delete((Category) response.getEntity());
            } else if (className.equalsIgnoreCase(Product.class.getSimpleName())) {
                logger.debug("Инициализация удаления записи Product");

                response = productService.getById(id);
                if (response.getEntity() == null) {
                    logger.debug(response.getType() + ":" + response.getMessage());
                    return response;
                }
                response = productService.delete((Product) response.getEntity());
            } else if (className.equalsIgnoreCase(Limit.class.getSimpleName())) {
                logger.debug("Инициализация удаления записи Limit");

                response = limitService.getById(id);
                if (response.getEntity() == null) {
                    logger.debug(response.getType() + ":" + response.getMessage());
                    return response;
                }
                response = limitService.delete((Limit) response.getEntity());
            }
        } catch (CrudException e) {
            String message = String.format("Произошла ошибка: \n%s", e.getMessage());

            logger.error(message);

            response = new JsonResponse();
            response.setType(ResponseType.ERROR);
            response.setMessage(message);
        }
        logger.debug(response.getMessage());

        return response;
    }
}
