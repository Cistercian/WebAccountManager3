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
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.DBData;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

        JsonResponse response = new JsonResponse();
        try {
            if (entity.getClass().isAssignableFrom(Category.class)) {
                Category category = (Category) entity;
                //Проверяем редактируем ли мы запись или создаем новую
                if (category.getId() ==  null) {
                    logger.debug("Создается новая запись.");
                    response.setType(ResponseType.SUCCESS_CREATE_NEW_ENTITY);
                }
                categoryService.save(category);
            } else if (entity.getClass().isAssignableFrom(Amount.class)) {
                Amount amount = (Amount) entity;

                if (amount.getId() ==  null) {
                    logger.debug("Создается новая запись.");
                    response.setType(ResponseType.SUCCESS_CREATE_NEW_ENTITY);
                }
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

            response.setMessage(message);
            response.setType(ResponseType.ERROR);

            return response;
        }

        String message = "Запись успешно сохранена в БД.";
        logger.debug(message);

        response.setMessage(message);
        if (response.getType() == null)
            response.setType(ResponseType.SUCCESS);

        return response;
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

    /**
     * Функция возвращает переданный список с расчитанными средними значения суммы.
     * @param barEntities список BarEntity
     * @param after Дата начала периода
     * @param before Дата конца периода
     * @return Список
     */
    public List<BarEntity> calcAvgSum(List<BarEntity> barEntities, LocalDate after, LocalDate before, byte averagingPeriod) {
        logger.debug(LogUtil.getMethodName());

        ChronoUnit chronoUnit;
        switch (averagingPeriod){
            case 0:
                chronoUnit = ChronoUnit.WEEKS;
                break;
            case 1:
            default:
                chronoUnit = ChronoUnit.MONTHS;
                break;
        }

        long distance = after.until(before, chronoUnit);
        distance = distance == 0 ? 1 : distance;
        logger.debug(String.format("Период усреднения: %s, кол-во периодов: %s", chronoUnit.toString(), distance));

        for (BarEntity entity : barEntities){
            entity.setSum(entity.getSum().abs().divide(new BigDecimal(distance), 2, BigDecimal.ROUND_HALF_UP));
        }

        return barEntities;
    }

    /**
     * Функция сортирует список по полю type и дале по полю Sum
     * @param entities Список
     * @return Список
     */
    public <T extends DBData> List<T> sortListByTypeAndSum(List<T> entities) {
        logger.debug(LogUtil.getMethodName());

        Collections.sort(entities, new Comparator<T>() {
            public int compare(T o1, T o2) {
                DBData entity1 = (DBData) o1;
                DBData entity2 = (DBData) o2;
                int result = entity1.getType().compareTo(entity2.getType());
                if (result == 0)
                    result = entity2.getSum().abs().compareTo(entity1.getSum().abs());

                return result;
            }
        });

        return entities;
    }

    public <T extends DBData> List<T> sortByLimit(List<T> entities) {
        Collections.sort(entities, new Comparator<T>() {
            public int compare(T o1, T o2) {
                DBData entity1 = (DBData) o1;
                DBData entity2 = (DBData) o2;
                if (entity2.getLimit().compareTo(new BigDecimal("0")) > 0 || entity1.getLimit().compareTo(new BigDecimal("0")) > 0) {
                    BigDecimal o2Size = entity2.getLimit().compareTo(new BigDecimal("0")) != 0 ?
                            o2.getSum().divide(entity2.getLimit(), 2, BigDecimal.ROUND_HALF_UP) :
                            new BigDecimal("99999");
                    BigDecimal o1Size = entity1.getLimit().compareTo(new BigDecimal("0")) != 0 ?
                            o1.getSum().divide(entity1.getLimit(), 2, BigDecimal.ROUND_HALF_UP) :
                            new BigDecimal("99999");
                    return o2Size.compareTo(o1Size);
                } else
                    return entity2.getSum().compareTo(entity1.getSum());
            }
        });

        return entities;
    }
}
