package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.mvc.repository.CategoryRepository;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.SecurityService;

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

    public Category add(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getAll() {
        return Lists.newArrayList(categoryRepository.findAll());
    }

    public Category getById(int id) {
        return categoryRepository.findOne(id);
    }

    public Map<Integer, String> getIdAndName() {
        Map<Integer, String> map = new HashMap<Integer, String>();
        List<Category> categories = getAll();

        for(Category category : categories) {
            map.put(category.getId(), category.getName());
        }

        return map;
    }

    public Map<Category, BigDecimal> getAllWithTotalSum() {
        List<Category> categories = Lists.newArrayList(getAllByCurrentUser());
        Map<Category, BigDecimal> nonSortedMap = new HashMap<Category, BigDecimal>();
        for (Category category : categories) {

            BigDecimal sum = new BigDecimal(0);
            for (Amount amount : category.getAmounts()) {
                sum = sum.add(amount.getPrice());
            }
            nonSortedMap.put(category, sum);

        }
        MapComparatorByValue comparator = new MapComparatorByValue(nonSortedMap);
        Map<Category, BigDecimal> map = new TreeMap<Category, BigDecimal>(comparator);
        map.putAll(nonSortedMap);

        return map;
    }

    private class MapComparatorByValue implements Comparator<Object> {
        private Map map;

        public MapComparatorByValue(Map map) {
            this.map = map;
        }

        public int compare(Object o1, Object o2) {
            BigDecimal i1 = new BigDecimal(map.get(o1).toString());
            BigDecimal i2 = new BigDecimal(map.get(o2).toString());

            return i2.compareTo(i1);
        }
    }

    public List<Category> getAllByCurrentUser() {
        return categoryRepository.findByUserId(securityService.findLoggedUser());
    }
}
