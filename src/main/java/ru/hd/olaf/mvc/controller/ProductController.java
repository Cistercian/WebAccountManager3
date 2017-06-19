package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.*;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.FormatUtil;
import ru.hd.olaf.util.LogUtil;
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
    private UtilService utilService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    /**
     * Прорисовка страницы просмотра состава товарной группы
     * @param productID productID сущности
     * @param beginDate начальная дата отсечки
     * @param endDate конечная дата отсечки
     * @return ModelAndView (page-product)
     */
    @RequestMapping(value = "/page-product", method = RequestMethod.GET)
    public ModelAndView displayPageProduct(@RequestParam(value = "productID", required = false) Integer productID,
                                           @RequestParam(value = "categoryID", required = false) Integer categoryID,
                                           @RequestParam(value = "after") String beginDate,
                                           @RequestParam(value = "before") String endDate,
                                           @RequestParam(value = "type", required = false) Integer type) {
        logger.debug(LogUtil.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/data/page-product");

        User currentUser = securityService.findLoggedUser();
        LocalDate after = DateUtil.getParsedDate(beginDate);
        LocalDate before = DateUtil.getParsedDate(endDate);
        String title = "";
        String details = "";
        String footer = "";

        List<Amount> amounts;
        Product product;

        if (productID != null) {
            JsonResponse response = productService.getById(productID);
            if (response.getType() == ResponseType.SUCCESS) {
                product = (Product) response.getEntity();

                response = utilService.getById(Category.class, categoryID);
                if (response.getType() == ResponseType.SUCCESS) {
                    Category category = (Category) response.getEntity();

                    amounts = amountService.getByProductAndCategoryAndDate(currentUser,
                                product,
                                category,
                                after,
                                before);

                    modelAndView.addObject("amounts", amounts);

                    modelAndView.addObject("id", product.getId());
                    title = "Просмотр содержимого группы товаров";
                    details = product.getName();
                    footer = "<a href='/product?productID=" + product.getId() + "'>(редактировать)</a>";
                }
            }
        } else if (categoryID != null){
            Category category = (Category) utilService.getById(Category.class, categoryID).getEntity();

            amounts = amountService.getByType(
                    currentUser,
                    category,
                    after,
                    before,
                    type);

            modelAndView.addObject("amounts", amounts);
            switch (type){
                case 0:
                    title = "Просмотр стандартных оборотов по категории";
                    break;
                case 1:
                case 2:
                    title = "Просмотр единоразовых оборотов по категории";
                    break;
                case 3:
                    title = "Просмотр обязательных оборотов по категории";
                    break;
            }
            modelAndView.addObject("type", type);
            modelAndView.addObject("after", after);
            modelAndView.addObject("before", before);
            modelAndView.addObject("categoryID", categoryID);

            details = "Категория: " + (category != null ? category.getName() : "отсутствует");
        }

        modelAndView.addObject("title", title);
        modelAndView.addObject("details", details);
        modelAndView.addObject("footer", footer);

        return modelAndView;
    }

    @RequestMapping(value = "/page-product/regulars", method = RequestMethod.GET)
    public ModelAndView getRegularAmounts(@RequestParam(value = "isBinding", required = false) Boolean isBinding,
                                          @RequestParam(value = "productID", required = false) Integer id,
                                          @RequestParam(value = "regularId", required = false) Integer regularId){
        logger.debug(LogUtil.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/data/page-product");

        String title;
        String details;

        if (isBinding == null) isBinding = false;

        if (isBinding){
            title = "Привязка обязательного оборота";
            details = "Обрабатываемый оборот: новая запись";
            modelAndView.addObject("isSingle", true);
        } else {
            title = "Просмотр обязательных оборотов";
            details = "Полный список";
            modelAndView.addObject("isGetRegulars", true);
        }

        if (regularId != null)
            modelAndView.addObject("regularId", regularId);

        if (id == null) id = 0;
        else {
            JsonResponse response = utilService.getById(Amount.class, id);

            if (response.getType() == ResponseType.SUCCESS){
                Amount amount = (Amount) response.getEntity();

                details = "Обрабатываемый оборот: #" + amount.getId() + " на сумму " +
                        FormatUtil.numberToString(amount.getPrice()) +
                        " (" + amount.getName() + ")";
            }
        }

        User currentUser = securityService.findLoggedUser();

        List<Amount> amounts = amountService.getAllRegular(currentUser);

        logger.debug("Список обязательных оборотов:");
        LogUtil.logList(logger, amounts);

        modelAndView.addObject("amounts", amounts);
        modelAndView.addObject("id", id);
        modelAndView.addObject("title", title);
        modelAndView.addObject("details", details);
        modelAndView.addObject("footer", "");
        modelAndView.addObject("isBinding", isBinding);

        return modelAndView;
    }

    @RequestMapping (value = "/page-product/binding", method = RequestMethod.GET)
    public ModelAndView getBindingTable(@RequestParam (value = "type") Byte type,
                                        @RequestParam (value = "categoryID") Integer categoryID,
                                        @RequestParam(value = "after") String beginDate,
                                        @RequestParam(value = "before") String endDate) {
        logger.debug(LogUtil.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/data/page-product");

        User currentUser = securityService.findLoggedUser();
        LocalDate after = DateUtil.getParsedDate(beginDate);
        LocalDate before = DateUtil.getParsedDate(endDate);

        List<Amount> amounts = null;
        Category category = null;
        JsonResponse response = utilService.getById(Category.class, categoryID);
        if (response.getType() == ResponseType.SUCCESS) {
            category = (Category) response.getEntity();

            amounts = amountService.getAmountsForBindingByType(
                    currentUser,
                    category,
                    after,
                    before,
                    type
            );
        }

        modelAndView.addObject("type", type);
        modelAndView.addObject("title", "Привязка оборотов по категории");
        modelAndView.addObject("details", "Категория: " + (category != null ? category.getName() : "отсутствует"));
        modelAndView.addObject("footer", "");
        modelAndView.addObject("isBinding", true);
        modelAndView.addObject("onclickOk", true);
        modelAndView.addObject("onclickCancel", true);
        modelAndView.addObject("amounts", amounts);

        return modelAndView;
    }
}
