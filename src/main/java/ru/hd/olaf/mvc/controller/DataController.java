package ru.hd.olaf.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ProductService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Olaf on 21.04.2017.
 */
@Controller
public class DataController {

    @Autowired
    private AmountService amountService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/page-data/{class}", method = RequestMethod.GET)
    public ModelAndView displayPageData(@PathVariable("class") String className){
        ModelAndView modelAndView = new ModelAndView("/data/page-data");
        Map<Class, Object> map = new HashMap<Class, Object>();

        if ("amount".equalsIgnoreCase(className)) {
            map.put(Amount.class, amountService.getAll());
        } else if ("category".equalsIgnoreCase(className)) {
            map.put(Category.class, categoryService.getAll());
        } else if ("product".equalsIgnoreCase(className)) {
            map.put(Product.class, productService.getAll());
        }

        modelAndView.addObject("data", map);

        return modelAndView;
    }
}
