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
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.time.LocalDate;
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
    @Autowired
    private SecurityService securityService;
    @Autowired
    private CategoryService categoryService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    /**
     * Прорисовка страницы просмотра состава товарной группы
     * @param id id сущности
     * @param beginDate начальная дата отсечки
     * @param endDate конечная дата отсечки
     * @return ModelAndView (page-product)
     */
    @RequestMapping(value = "/page-product/{id}", method = RequestMethod.GET)
    public ModelAndView displayPageProduct(@PathVariable("id") Integer id,
                                           @RequestParam(value = "categoryId") Integer categoryId,
                                           @RequestParam(value = "after") String beginDate,
                                           @RequestParam(value = "before") String endDate) {
        logger.debug(LogUtil.getMethodName());

        User currentUser = securityService.findLoggedUser();

        LocalDate after = DateUtil.getParsedDate(beginDate);
        LocalDate before = DateUtil.getParsedDate(endDate);

        ModelAndView modelAndView = new ModelAndView("/data/page-product");

        Product product;
        JsonResponse response = productService.getById(id);

        if (response.getType() == ResponseType.SUCCESS) {
            product = (Product) response.getEntity();

            response = categoryService.getById(categoryId);
            if (response.getType() == ResponseType.SUCCESS) {
                Category category = (Category) response.getEntity();

                List<Amount> amounts = amountService.getByProductAndCategoryAndDate(currentUser,
                        product,
                        category,
                        after,
                        before);

                modelAndView.addObject("amounts", amounts);

                modelAndView.addObject("id", product.getId());
                modelAndView.addObject("name", product.getName());
            }
        }

        return modelAndView;
    }
}
