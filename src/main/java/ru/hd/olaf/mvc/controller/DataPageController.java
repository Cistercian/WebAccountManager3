package ru.hd.olaf.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.hd.olaf.entities.Amounts;
import ru.hd.olaf.entities.Categories;
import ru.hd.olaf.mvc.service.AmountsService;
import ru.hd.olaf.mvc.service.CategoriesService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Olaf on 14.04.2017.
 */
@org.springframework.stereotype.Controller
public class DataPageController {

    @Autowired
    private CategoriesService categoriesService;
    @Autowired
    private AmountsService amountsService;

    @RequestMapping(value = "/edit-page/getCategoriesIdAndName", method = RequestMethod.GET)
    public @ResponseBody Map<Integer, String> getCategoriesIdAndName() {
        return categoriesService.getIdAndName();
    }

    @RequestMapping(params = {"categoryId", "name", "price", "amountsDate", "details", "submitAmmount"}, value = "/edit-page-amount/addAmounts", method = RequestMethod.POST)
    public String addAmount(@RequestParam(value = "categoryId") Integer categoryId,
                            @RequestParam(value = "name") String name,
                            @RequestParam(value = "price") BigDecimal price,
                            @RequestParam(value = "amountsDate") Date amountsDate,
                            @RequestParam(value = "details") String details,
                            @RequestParam(value = "submitAmmount") String submitAmmount) {
        System.out.println("Controller add()_amount is called");

        Categories categories = categoriesService.getById(categoryId);
        Amounts amounts = new Amounts();

        amounts.setCategoryId(categories);
        amounts.setName(name);
        amounts.setPrice(price);
        amounts.setAmountsDate(amountsDate);
        amounts.setDetails(details);

        amountsService.add(amounts);
        return "index";
    }

    @RequestMapping(params = {"id", "name", "details", "type"}, value = "/edit-page/addCategory", method = RequestMethod.POST)
    public String addCategory(@RequestParam(value = "id") Integer id,
                            @RequestParam(value = "name") String name,
                            @RequestParam(value = "details") String details,
                            @RequestParam(value = "type") Byte type) {
        System.out.println("Controller add()_category is called");

        Categories category = null;
        category = id == -1 ? new Categories() : categoriesService.getById(id);

        category.setType(type);
        category.setName(name);
        category.setDetails(details);

        categoriesService.add(category);
        return "index";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}
