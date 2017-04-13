package ru.hd.olaf.mvc.controller;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amounts;
import ru.hd.olaf.entities.Categories;
import ru.hd.olaf.mvc.repository.CategoriesRepository;
import ru.hd.olaf.mvc.service.AmountsService;
import ru.hd.olaf.mvc.service.CategoriesService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Olaf on 11.04.2017.
 */
@org.springframework.stereotype.Controller
public class Controller {
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
    public @ResponseBody List<Categories> getAllCategoriesInJson() {
        System.out.println("Controller getAllCategoriesInJson() is called");
        List<Categories> categories = Lists.newArrayList(categoriesService.getAll());
        return categories;
    }

    @RequestMapping(params = {"categoryId", "name", "price", "amountsDate", "details", "submitAmmount"}, value = "/amounts/add", method = RequestMethod.POST)
    public String addAmount(@RequestParam(value = "categoryId") Integer categoryId,
                            @RequestParam(value = "name") String name,
                            @RequestParam(value = "price") BigDecimal price,
                            @RequestParam(value = "amountsDate") Date amountsDate,
                            @RequestParam(value = "details") String details,
                            @RequestParam(value = "submitAmmount") String submitAmmount){
        System.out.println("Controller addAmount() is called");

        Amounts amounts = new Amounts();

        amounts.setCategoryId(categoryId);
        amounts.setName(name);
        amounts.setPrice(price);
        amounts.setAmountsDate(amountsDate);
        amounts.setDetails(details);

        System.out.println(amounts);
        amountsService.addAmount(amounts);
        return "index";
    }

    @RequestMapping(value = "/getAmounts", method = RequestMethod.GET)
    public @ResponseBody List<Amounts> getAmmounts(){
        System.out.println("Controller getAmmounts() is called");
        return amountsService.getAll();
    }

    private void printData(List<Categories> list) {
        for (Categories category : list) {
            System.out.println(category);
        }
    }
}
