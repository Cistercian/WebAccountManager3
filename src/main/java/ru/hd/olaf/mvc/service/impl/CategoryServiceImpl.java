package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.mvc.repository.CategoryRepository;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.MapComparatorByValue;
import ru.hd.olaf.util.json.BarEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
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

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getAll() {
        return Lists.newArrayList(categoryRepository.findByUserId(securityService.findLoggedUser()));
    }

    public Category getById(int id) {
        return categoryRepository.findOne(id);
    }

    public Map<Integer, String> getIdAndNameByCurrentUser() {
        Map<Integer, String> map = new HashMap<Integer, String>();
        List<Category> categories = getAllByCurrentUser();

        for(Category category : categories) {
            map.put(category.getId(), category.getName());
        }

        return map;
    }

    public Map<Category, BigDecimal> getParentsWithTotalSum(LocalDate after, LocalDate before) {
        List<Category> categories = Lists.newArrayList(getByParentId(null));

        Map<Category, BigDecimal> nonSortedMap = getCategoryPrice(categories, after, before);

        MapComparatorByValue comparator = new MapComparatorByValue(nonSortedMap);
        Map<Category, BigDecimal> map = new TreeMap<Category, BigDecimal>(comparator);

        map.putAll(nonSortedMap);

        return map;
    }

    public List<BarEntity> getCategoriesSum(Category parentId, LocalDate after, LocalDate before) {
        //logger.debug(String.format("Function %s", "getParents()"));
        logger.debug(String.format("Dates: after: %s, before %s", after.toString(), before.toString()));

        List<Category> categories = getByParentId(parentId);
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

    public Map<Category, BigDecimal> getCategoryPrice(List<Category> categories, LocalDate after, LocalDate before) {
        Map<Category, BigDecimal> categoryPrices = new HashMap<Category, BigDecimal>();

        for (Category category : categories) {

            BigDecimal sum = getSumCategory(category, after, before);

            if (sum.compareTo(new BigDecimal("0")) > 0) {
                categoryPrices.put(category, sum);
            }
        }
        return categoryPrices;
    }

    private BigDecimal getSumCategory(Category category, LocalDate after, LocalDate before) {
        logger.debug(String.format("Function %s", "getSumCategory()"));

        BigDecimal sum = new BigDecimal(0);
        for (Amount amount : category.getAmounts()) {

            //convert amounts.date to LocalDate
            LocalDate amountDate = amount.getLocalDate();

            //logger.debug(String.format("Amount: %s, date: %s", amount, amountDate));

            if (amountDate.isAfter(after) && amountDate.isBefore(before)) {
                sum = sum.add(amount.getPrice());
                //logger.debug(String.format("Amount is inside to period"));
            }


        }
        //учитываем дочерние категории
        for (Category children : categoryRepository.findByParentId(category)){
            sum = sum.add(getSumCategory(children, after, before));
        }
        return sum;
    }

    public List<Category> getAllByCurrentUser() {
        logger.debug(String.format("current User = %s", securityService.findLoggedUser().getUsername()));

        return categoryRepository.findByUserId(securityService.findLoggedUser());
    }

    public String delete(Integer id) {
        try {
            categoryRepository.delete(id);
            return "delete successfully";
        } catch (Exception e) {
            return "delete was not coplited";
        }
    }

    public List<Category> getByParentId(Category parentId) {
        List<Category> categories = categoryRepository.findByParentId(parentId);

        return categories;
    }
}
