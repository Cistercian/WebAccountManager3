package ru.hd.olaf.mvc.controller;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.CategoriesEntity;
import ru.hd.olaf.mvc.repository.ContactRepository;

import java.util.List;

/**
 * Created by Olaf on 11.04.2017.
 */
@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    private ContactRepository jpaCategoriesService1;

    @RequestMapping(value = "/getAllCategories", method = RequestMethod.GET)
    public ModelAndView selectAllCategories() {
        System.out.println("Controller selectAllCategories() is called");
        List<CategoriesEntity> categories = Lists.newArrayList(jpaCategoriesService1.findAll());
        printData(categories);
        return new ModelAndView("index", "resultObject", categories);
    }

//    @RequestMapping(value = "/getAllCategoriesInJson", method = RequestMethod.GET, produces = "application/json")
//    public @ResponseBody  CategoriesEntity getAllCategoriesInJson() {
//        System.out.println("Controller getAllCategoriesInJson() is called");
//        List<CategoriesEntity> categories = Lists.newArrayList(jpaCategoriesService1.findAll());
//        return categories.get(0);
//    }

    private void printData(List<CategoriesEntity> list) {
        for (CategoriesEntity category : list) {
            System.out.println(category);
        }
    }
}
