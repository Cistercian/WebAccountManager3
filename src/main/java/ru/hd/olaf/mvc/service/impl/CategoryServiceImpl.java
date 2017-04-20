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

import java.math.BigDecimal;
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

    public Category add(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getAll() {
        return Lists.newArrayList(categoryRepository.findAll());
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

    public Map<Category, BigDecimal> getWithTotalSum() {
        List<Category> categories = Lists.newArrayList(getAllByCurrentUser());

        Map<Category, BigDecimal> nonSortedMap = getCategoryPrice(categories);

        MapComparatorByValue comparator = new MapComparatorByValue(nonSortedMap);
        Map<Category, BigDecimal> map = new TreeMap<Category, BigDecimal>(comparator);

        map.putAll(nonSortedMap);

        return map;
    }

    public Map<Category, BigDecimal> getCategoryPrice(List<Category> categories) {
        Map<Category, BigDecimal> categoryPrices = new HashMap<Category, BigDecimal>();

        for (Category category : categories) {

            BigDecimal sum = new BigDecimal(0);
            for (Amount amount : category.getAmounts()) {
                sum = sum.add(amount.getPrice());
            }
            categoryPrices.put(category, sum);

        }
        return categoryPrices;
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
