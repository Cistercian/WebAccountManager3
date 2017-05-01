package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.repository.AmountRepository;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.ResponseType;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.JsonResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by Olaf on 13.04.2017.
 */
@Service
public class AmountServiceImpl implements AmountService {

    @Autowired
    private AmountRepository amountRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private SecurityService securityService;

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    /**
     * Функция возвращает список amount с проверкой на текущего пользователя
     *
     * @return
     */
    public List<Amount> getAll() {
        logger.debug(LogUtil.getMethodName());
        return Lists.newArrayList(amountRepository.findByUserId(securityService.findLoggedUser()));
    }

    /**
     * Функция возвращает список amount по категории и текущему пользователю
     *
     * @param category
     * @return
     */
    public List<Amount> getByCategory(Category category) {
        logger.debug(LogUtil.getMethodName());
        List<Amount> amounts = amountRepository.findByCategoryIdAndUserId(category, securityService.findLoggedUser());

        Collections.sort(amounts, new Comparator<Amount>() {
            public int compare(Amount o1, Amount o2) {
                return o2.getPrice().compareTo(o1.getPrice());
            }
        });

        return amounts;
    }

    /**
     * Функция возвращает список amount по продукту с учетом текущего пользователя
     *
     * @param product
     * @return
     */
    public List<Amount> getByProductAndDate(Product product, LocalDate after, LocalDate before) {
        logger.debug(LogUtil.getMethodName());
        logger.debug(String.format("Dates interval: %s - %s", after.toString(), before.toString()));

        Date begin = Date.from(after.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(before.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<Amount> amounts = Lists.newArrayList(amountRepository.findByProductIdAndUserIdAndAmountsDateBetween(
                product,
                securityService.findLoggedUser(),
                begin,
                end
        ));

        return amounts;
    }

    public List<Amount> getByDate(User user, LocalDate after, LocalDate before) {
        logger.debug(LogUtil.getMethodName() + String.format(". Интервал: %s - %s", after, before));

        Date begin = Date.from(after.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(before.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return Lists.newArrayList(amountRepository.findByUserIdAndAmountsDateBetween(user,
                begin,
                end));
    }

    /**
     * Функция возвращает список BarEntity, соответствующий набору amount по заданной категории при совпадении amounts.date
     * в заданный период с группировкой по amounts.product
     *
     * @param category
     * @param after
     * @param before
     * @return
     */
    public List<BarEntity> getBarEntitiesByCategory(Category category, LocalDate after, LocalDate before) {
        logger.debug(LogUtil.getMethodName());
        List<BarEntity> barEntities = new ArrayList<BarEntity>();

        List<Product> products = productService.getAll();
        for (Product product : products) {

            BigDecimal sumAmounts = getSumByCategoryAndProduct(category,
                    product,
                    after,
                    before);

            if (sumAmounts.compareTo(new BigDecimal("0")) > 0) {
                BarEntity barEntity = new BarEntity(
                        product.getClass().getSimpleName(),
                        product.getId(),
                        sumAmounts,
                        product.getName());

                barEntities.add(barEntity);
            }
        }

        return barEntities;
    }

    /**
     * Функция возвращает общую сумму по всем amount, относящимся к данной категории, продукту и попадающим
     * в указанный временной интервал
     *
     * @param category
     * @param product
     * @param after
     * @param before
     * @return
     */
    public BigDecimal getSumByCategoryAndProduct(Category category, Product product, LocalDate after, LocalDate before) {
        logger.debug(LogUtil.getMethodName());
        List<Amount> amounts = getByCategoryAndProduct(category, product);

        BigDecimal sumAmounts = new BigDecimal("0");
        for (Amount amount : amounts) {

            LocalDate amountDate = amount.getLocalDate();
            if (amountDate.isAfter(after) && amountDate.isBefore(before)) {
                sumAmounts = sumAmounts.add(amount.getPrice());
            }
        }

        return sumAmounts;
    }

    /**
     * Функция возвращает список amount по категории и продукту с контролем пользователя
     *
     * @param categoryId
     * @param productId
     * @return
     */
    public List<Amount> getByCategoryAndProduct(Category categoryId, Product productId) {
        logger.debug(LogUtil.getMethodName());
        return amountRepository.findByCategoryIdAndProductIdAndUserId(categoryId,
                productId,
                securityService.findLoggedUser());
    }

    /**
     * Функция возвращает запись amount  с проверкой на соответствие текущему пользователю
     *
     * @param id
     * @return
     */
    private Amount getOne(Integer id) throws AuthException, IllegalArgumentException {
        logger.debug(LogUtil.getMethodName());
        if (id == null) throw new IllegalArgumentException();

        Amount amount = amountRepository.findOne(id);
        if (amount == null) return null;

        if (!amount.getUserId().equals(securityService.findLoggedUser()))
            throw new AuthException(String.format("Запрошенный объект с id %d Вам не принадлежит.", id));

        return amount;
    }

    /**
     * Функция возвращает объект JsonResponse, содержащий результат поиска объекта и, при возможности, сам объект
     *
     * @param id id записи
     * @return JsonResponse
     */
    public JsonResponse getById(Integer id) {
        logger.debug(LogUtil.getMethodName());

        Amount amount = null;

        JsonResponse jsonResponse = new JsonResponse();
        try {
            amount = getOne(id);

            String message = String.format("Запись с id = %d найдена: %s", id, amount);
            jsonResponse.setType(ResponseType.SUCCESS);
            jsonResponse.setMessage(message);
            jsonResponse.setEntity(amount);
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
     * Функция сохраняет(обновляет) запись amount
     *
     * @param amount
     * @return
     */
    public Amount save(Amount amount) throws CrudException {
        Amount entity = null;

        try {
            entity = amountRepository.save(amount);
        } catch (Exception e) {
            throw new CrudException(e.getMessage());
        }

        return entity;
    }

    /**
     * Функция удаления записи из БД
     *
     * @param amount
     * @return
     */
    public JsonResponse delete(Amount amount) throws CrudException {
        try {
            amountRepository.delete(amount.getId());
            return new JsonResponse(ResponseType.SUCCESS, "Удаление успешно завершено.");
        } catch (Exception e) {
            throw new CrudException(e.getMessage());
        }
    }
}
