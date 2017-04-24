package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.mvc.repository.CategoryRepository;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.MapComparatorByValue;
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
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private AmountService amountService;

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    /**
     * Функция создания/обновления записи
     * @param category
     * @return
     */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Функция возвращает список category  с проверкой на текущего пользователя
     * @return
     */
    public List<Category> getAll() {
        logger.debug(String.format("Function %s", "getAll()"));

        return getAllByCurrentUser();
    }

    /**
     * Функция возвращает список Category по текущему пользователю
     * @return
     */
    public List<Category> getAllByCurrentUser() {
        logger.debug(String.format("Function %s", "getAllByCurrentUser()"));
        logger.debug(String.format("Cerrent user:", securityService.findLoggedUser()));

        return categoryRepository.findByUserId(securityService.findLoggedUser());
    }

    /**
     * Функция возвращения записи category с проверкой на текущего пользователя
     * @param id
     * @return
     */
    public Category getById(Integer id) {
        if (id == null) return null;

        Category category = categoryRepository.findOne(id);
        if (category == null) return null;

        return category.getUserId().equals(securityService.findLoggedUser()) ? category : null;
    }

    /**
     * Функция возвращает коллекцию служебного класса BarEntity, элемент которой - дочерняя категория, указанной родительской
     с итоговой по ней суммой за заданный период с исключением нулевых сумм.

     * @param parent
     * @param after
     * @param before
     * @return
     */
    public List<BarEntity> getBarEntityOfSubCategories(Category parent, LocalDate after, LocalDate before) {
        logger.debug(String.format("Function %s", "getBarEntityOfSubCategories()"));
        logger.debug(String.format("Dates: after: %s, before %s", after.toString(), before.toString()));

        List<Category> categories = getByParentId(parent);
        List<BarEntity> parents = new ArrayList<BarEntity>();

        for (Category category : categories) {
            BigDecimal sum = getSumCategory(category, after, before);

            if (sum.compareTo(new BigDecimal("0")) > 0){
                String type = category.getType() == 0 ? "CategoryIncome" : "CategoryExpense";

                BarEntity barEntity = new BarEntity(type, category.getId(), sum, category.getName());
                parents.add(barEntity);
            }
        }
        return parents;
    }

    /**
     * Функция получения итоговой суммы по категории с учетом вложенных (дочерних) категорий
     * @param category
     * @param after
     * @param before
     * @return
     */
    private BigDecimal getSumCategory(Category category, LocalDate after, LocalDate before) {
        logger.debug(String.format("Function %s", "getSumCategory()"));

        BigDecimal sum = new BigDecimal(0);
        for (Amount amount : category.getAmounts()) {
            //convert amounts.date to LocalDate
            LocalDate amountDate = amount.getLocalDate();

            if (amountDate.isAfter(after) && amountDate.isBefore(before)) {
                sum = sum.add(amount.getPrice());
            }
        }
        //учитываем дочерние категории
        for (Category children : getByParentId(category)){

            sum = sum.add(getSumCategory(children, after, before));

        }
        return sum;
    }

    /**
     * Функция возвращает список дочерних категорий
     * @param parentId
     * @return
     */
    private List<Category> getByParentId(Category parentId) {
        List<Category> categories =
                categoryRepository.findByParentIdAndUserId(parentId, securityService.findLoggedUser());

        return categories;
    }

    /**
     * Функция удаления записи Category
     * @param category
     * @return
     */
    public JsonAnswer delete(Category category) {

        //Проверка существуют ли записи amount с данной категорией
        if (amountService.getByCategory(category).size() > 0) {
            logger.debug(String.format("Deleting is aborted. Found available Amounts!"));

            String message = String.format("Deleting is aborted. Found exists Amounts!");
            return new JsonAnswer(AnswerType.ERROR, message);
        }

        if (!category.getUserId().equals(securityService.findLoggedUser()))
            return new JsonAnswer(AnswerType.ERROR, "auth error!");
        //TODO: catch exceptions
        try {
            categoryRepository.delete(category);
            return new JsonAnswer(AnswerType.SUCCESS, "Deleting complite");
        } catch (Exception e) {
            return new JsonAnswer(AnswerType.ERROR, e.getMessage());
        }
    }
}
