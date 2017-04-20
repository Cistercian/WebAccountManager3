package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ReportService;
import ru.hd.olaf.util.json.BarEntity;

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
    @Autowired
    private ReportService reportService;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @CrossOrigin
    @RequestMapping(value = "/getAllCategoriesWithTotalSum", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<Category, BigDecimal> getAllCategoriesWithTotalSum() {
        logger.debug(String.format("Function %s", "getAllCategoriesWithTotalSum"));
        return categoryService.getAllWithTotalSum();
    }

    @RequestMapping(value = "/getAmountsByCategoryId", params = {"categoryId"}, method = RequestMethod.GET)
    public
    @ResponseBody
    List<Amount> getAmountsByCategoryId(@RequestParam(value = "categoryId") Integer categoryId) {
        logger.debug(String.format("Function %s", "getAmountsByCategoryId"));
        Category category = categoryService.getById(categoryId);

        List<Amount> amounts = amountService.getByCategory(category);
        printData(amounts);

        return amounts;
    }

    @RequestMapping(value = "/getContentByCategoryId", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BarEntity> getCategoryContentByCategoryId(@RequestParam(value = "categoryId") Integer categoryId){
        logger.debug(String.format("Function %s, id: %d", "getCategoryContentByCategoryId", categoryId));
        return reportService.getCategoryContentById(categoryId);
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