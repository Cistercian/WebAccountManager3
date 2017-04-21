package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.ProductService;

import java.util.List;

/**
 * Created by d.v.hozyashev on 21.04.2017.
 */
@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private AmountService amountService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/page-product/{id}", method = RequestMethod.GET)
    public ModelAndView displayPageProduct(@PathVariable("id") Integer id) {
        logger.debug(String.format("Function %s", "displayPageProduct()"));

        Product product = productService.getById(id);
        List<Amount> amounts = amountService.getByProduct(product);
        ModelAndView modelAndView = new ModelAndView("/data/page-product");

        modelAndView.addObject("amounts", amounts);

        return modelAndView;
    }
}
