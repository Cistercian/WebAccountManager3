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
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.time.LocalDate;
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
                                           @RequestParam(value = "period") String period,
                                           @RequestParam(value = "countDays", required = false) Integer countDays) {
        logger.debug(LogUtil.getMethodName());
        logger.debug(String.format("Params: id = %d, period = %s, countDays = %d", id, period, countDays));

        ModelAndView modelAndView = new ModelAndView("/data/page-product");

        Product product = null;
        JsonResponse response = productService.getById(id);

        if (response.getType() == ResponseType.SUCCESS)
            product = (Product) response.getEntity();

        LocalDate today = LocalDate.now();
        LocalDate after = getAfterDate(period, today, countDays);

        List<Amount> amounts = amountService.getByProduct(product, after.minusDays(1), today.plusDays(1));
        modelAndView.addObject("amounts", amounts);

        return modelAndView;
    }
}
