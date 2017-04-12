package ru.hd.olaf.mvc.controller;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.CategoriesEntity;
import ru.hd.olaf.services.CategoriesService;
import ru.hd.olaf.services.impl.CategoriesServiceImpl;

import java.util.List;

/**
 * Created by Olaf on 11.04.2017.
 */

@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    private CategoriesServiceImpl jpaCategoriesService1;

    @RequestMapping(value = "/getAllCategories", method = RequestMethod.GET)
    public ModelAndView selectAllCategories() {
        System.out.println("Controller selectAllCategories() is called");
        List<CategoriesEntity> categories = Lists.newArrayList(jpaCategoriesService1.findAll());
        return new ModelAndView("/index.jsp", "resultObject", categories);
    }
}
