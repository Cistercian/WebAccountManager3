package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.ParseUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.hd.olaf.util.DatePeriod.getAfterDate;

/**
 * Created by d.v.hozyashev on 21.04.2017.
 */
@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private AmountService amountService;
    @Autowired
    private SecurityService securityService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/page-product/{id}", method = RequestMethod.GET)
    public ModelAndView displayPageProduct(@PathVariable("id") Integer id,
                                           @RequestParam(value = "after") String beginDate,
                                           @RequestParam(value = "before") String endDate) {
        logger.debug(LogUtil.getMethodName());

        LocalDate after = ParseUtil.getParsedDate(beginDate);
        LocalDate before = ParseUtil.getParsedDate(endDate);

        ModelAndView modelAndView = new ModelAndView("/data/page-product");

        Product product = null;
        JsonResponse response = productService.getById(id);

        if (response.getType() == ResponseType.SUCCESS)
            product = (Product) response.getEntity();

        List<Amount> amounts = amountService.getByProductAndDate(product, after, before);
        modelAndView.addObject("amounts", amounts);

        return modelAndView;
    }
}
