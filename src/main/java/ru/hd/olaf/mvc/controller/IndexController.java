package ru.hd.olaf.mvc.controller;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amounts;
import ru.hd.olaf.entities.Categories;
import ru.hd.olaf.mvc.repository.CategoriesRepository;
import ru.hd.olaf.mvc.service.AmountsService;
import ru.hd.olaf.mvc.service.CategoriesService;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Olaf on 11.04.2017.
 */
@org.springframework.stereotype.Controller
public class IndexController {
    @Autowired
    private CategoriesService categoriesService;
    @Autowired
    private AmountsService amountsService;

    @RequestMapping(value = "/getAllCategories", method = RequestMethod.GET)
    public ModelAndView selectAllCategories() {
        System.out.println("Controller selectAllCategories() is called");
        List<Categories> categories = Lists.newArrayList(categoriesService.getAll());
        printData(categories);
        return new ModelAndView("index", "resultObject", categories);
    }

    @RequestMapping(value = "/getAllCategoriesInJson", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Categories> getAllCategoriesInJson() {
        System.out.println("Controller getAllCategoriesInJson() is called");
        List<Categories> categories = Lists.newArrayList(categoriesService.getAll());
        return categories;
    }

    @RequestMapping(value = "/getSumByCategory", method = RequestMethod.GET)
    public @ResponseBody Map<BigDecimal, String> getAmountsByCategory() {

        Map<Categories, BigDecimal> map = amountsService.getSumByCategory();
        Map<BigDecimal, String> amounts = new TreeMap<BigDecimal, String>(Collections.reverseOrder());

        for (Map.Entry<Categories, BigDecimal> entry : map.entrySet()) {
            amounts.put(entry.getValue(), entry.getKey().getName());
        }

        return amounts;
    }

    @RequestMapping(value = "/getAmounts", method = RequestMethod.GET)
    public @ResponseBody List<Amounts> getAmounts() {
        System.out.println("Controller getAmounts() is called");
        return amountsService.getAll();
    }

    private <T> void printData(List<T> list) {
        for (T T : list) {
            System.out.println(T);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}