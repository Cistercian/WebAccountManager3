package ru.hd.olaf.mvc.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Olaf on 11.04.2017.
 */
@org.springframework.stereotype.Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AmountService amountService;

    @RequestMapping(value = "/getAllCategoriesWithTotalSum", method = RequestMethod.GET)
    public @ResponseBody Map<Category, BigDecimal> getAllCategoriesWithTotalSum() {
        System.out.println("Controller getAllCategoriesWithTotalSum() is called");
        return categoryService.getAllWithTotalSum();
    }

    @RequestMapping(value = "/getAmountsByCategoryId", params = {"categoryId"}, method = RequestMethod.GET)
    public @ResponseBody List<Amount> getAmountsByCategoryId(@RequestParam(value = "categoryId") Integer categoryId){
        System.out.println("Controller getAmountsByCategoryId() is called");
        Category category = categoryService.getById(categoryId);

        List<Amount> amounts = amountService.getAllByCategoryId(category);
        printData(amounts);

        return amounts;
    }

    @RequestMapping(value = "/getAmounts", method = RequestMethod.GET)
    public @ResponseBody List<Amount> getAmounts() {
        System.out.println("Controller getAmounts() is called");
        return amountService.getAll();
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