package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.repository.AmountRepository;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.json.AnswerType;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.JsonAnswer;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    /**
     * Функция сохраняет(обновляет) запись amount
     * @param amount
     * @return
     */
    public Amount save(Amount amount) {
        return amountRepository.save(amount);
    }

    /**
     * Функция возвращает список amount с проверкой на текущего пользователя
     * @return
     */
    public List<Amount> getAll() {
        return Lists.newArrayList(amountRepository.findByUserId(securityService.findLoggedUser()));
    }

    /**
     * Функция возвращает список amount по категории и текущему пользователю
     * @param category
     * @return
     */
    public List<Amount> getByCategory(Category category){
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
     * @param product
     * @return
     */
    public List<Amount> getByProduct(Product product) {
        return amountRepository.findByProductIdAndUserId(product, securityService.findLoggedUser());
    }

    /**
     * Функция возвращает список BarEntity, соответствующий набору amount по заданной категории при совпадении amounts.date
     * в заданный период с группировкой по amounts.product
     * @param category
     * @param after
     * @param before
     * @return
     */
    public List<BarEntity> getBarEntitiesByCategory(Category category, LocalDate after, LocalDate before) {
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
     * @param category
     * @param product
     * @param after
     * @param before
     * @return
     */
    public BigDecimal getSumByCategoryAndProduct(Category category, Product product, LocalDate after, LocalDate before) {
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
     * @param categoryId
     * @param productId
     * @return
     */
    public List<Amount> getByCategoryAndProduct(Category categoryId, Product productId) {
        return amountRepository.findByCategoryIdAndProductIdAndUserId(categoryId,
                productId,
                securityService.findLoggedUser());
    }

    /**
     * Функция возвращает запись amount  с проверкой на соответствие текущему пользователю
     * @param id
     * @return
     */
    public Amount getById(Integer id) {
        if (id == null) return null;

        Amount amount = amountRepository.findOne(id);
        if (amount == null) return null;
        
        return amount.getUserId().equals(securityService.findLoggedUser()) ? amount : null;
    }

    /**
     * Функция удаления записи из БД
     * @param amount
     * @return
     */
    public JsonAnswer delete(Amount amount) {
        if (!amount.getUserId().equals(securityService.findLoggedUser()))
            return new JsonAnswer(AnswerType.ERROR, "auth error!");
        //TODO: catch exceptions
        try {
            amountRepository.delete(amount.getId());
            return new JsonAnswer(AnswerType.SUCCESS, "Deleteing complite!");
        } catch (Exception e) {
            return new JsonAnswer(AnswerType.ERROR, e.getMessage());
        }
    }
}
