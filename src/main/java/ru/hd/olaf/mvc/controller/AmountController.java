package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.json.AnswerType;
import ru.hd.olaf.util.json.JsonAnswer;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Olaf on 14.04.2017.
 */
@org.springframework.stereotype.Controller
public class AmountController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AmountService amountService;
    @Autowired
    private ProductService productService;
    @Autowired
    private SecurityService securityService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Отрисовка пустой страницы page-amount.html
     * @return
     */
    @RequestMapping(value = "/page-amount", method = RequestMethod.GET)
    public ModelAndView getViewPageAmount(){
        logger.debug(String.format("function: %s.", "getViewPageAmount()"));

        return getViewPageAmountByID(null);
    }

    /**
     * Функция просмотра записи amount (заполненная страница page-amount)
     * @param id
     * @return
     */
    @RequestMapping(value = "/page-amount/{id}", method = RequestMethod.GET)
    public ModelAndView getViewPageAmountByID(@PathVariable("id") Integer id){
        logger.debug(String.format("Function %s", "getViewPageAmountByID()", id));

        ModelAndView modelAndView = new ModelAndView("/data/page-amount");

        Amount amount = amountService.getById(id);
        if (amount != null) {
            //TODO: security

            modelAndView.addObject("name", amount.getName());
            modelAndView.addObject("date", amount.getAmountsDate());
            modelAndView.addObject("price", amount.getPrice());
            modelAndView.addObject("details", amount.getDetails());
            modelAndView.addObject("id", amount.getId());
            modelAndView.addObject("categoryId", amount.getCategoryId().getId());
            modelAndView.addObject("categoryName", amount.getCategoryId().getName());

            Product product = amount.getProductId();
            if (product != null) {
                modelAndView.addObject("productId", amount.getProductId().getId());
                modelAndView.addObject("productName", amount.getProductId().getName());
            }

            logger.debug(String.format("Display Amount: %s", amount));
        }

        List<Category> categories = categoryService.getAll();

        logger.debug(String.format("Map 'categories' for injecting: %s.", categories.toString()));

        modelAndView.addObject("categories", categories);

        return modelAndView;
    }

    /**
     * Функция сохранения записи amount в БД
     * @param id
     * @param categoryId
     * @param productName
     * @param name
     * @param price
     * @param amountsDate
     * @param details
     * @return
     */
    @RequestMapping(value = "/page-amount/save", method = RequestMethod.POST)
    public String saveAmount(@RequestParam(value = "id") Integer id,
                             @RequestParam(value = "categoryId") Integer categoryId,
                             @RequestParam(value = "productName") String productName,
                             @RequestParam(value = "name") String name,
                             @RequestParam(value = "price") BigDecimal price,
                             @RequestParam(value = "date") Date amountsDate,
                             @RequestParam(value = "details") String details) {

        //TODO: throw exception
        Category category = categoryService.getById(categoryId);
        if (category == null)
            return null;

        Amount amount = amountService.getById(id);
        if (amount == null) {
            logger.debug(String.format("Not found Amount, id: %d", id));
            amount = new Amount();
        }

        amount.setCategoryId(category);
        amount.setName(name);
        amount.setPrice(price);
        amount.setAmountsDate(amountsDate);
        amount.setDetails(details);
        amount.setUserId(securityService.findLoggedUser());

        Product product;
        try {
            product = productService.getByName(productName);
            logger.debug(String.format("Used existed Product: %s", product));
        } catch (Exception e) {
            logger.debug(String.format("function: %s. Not found Product, reason:",
                    "saveAmount", e.getMessage()));
            product = new Product();
            product.setName(productName);
            product.setUserId(securityService.findLoggedUser());

            logger.debug(String.format("Create new Product: %s", product));

            productService.save(product);
        }
        amount.setProductId(product);

        amountService.save(amount);

        logger.debug(String.format("function: %s. User: %s, Amount=%s",
                "saveAmount", securityService.findLoggedUsername(), amount));

        return "index";
    }

    /**
     * Функция удаления записи по ее id
     * @param id
     * @return
     */
    @RequestMapping(value = "/page-amount/delete", method = RequestMethod.POST)
    public @ResponseBody JsonAnswer deleteAmount(@RequestParam(value = "id") Integer id){
        logger.debug(String.format("Function %s", "deleteAmount()"));

        Amount amount = amountService.getById(id);
        JsonAnswer response = null;
        if (amount != null) {

            logger.debug(String.format("Delete Amount: %s", amount));

            response = amountService.delete(amount);

            logger.debug(String.format("Result: %s", response.getMessage()));

        } else {
            response.setType(AnswerType.ERROR);
            response.setMessage("Entity not found!");
        }

        return response;
    }

    /**
     * Функция получения списка таблицы product по совпадению (быстрый поиск для выпадающего списка)
     * @param query
     * @return
     */
    @RequestMapping(value = "/page-amount/getProducts", method = RequestMethod.GET)
    public @ResponseBody
    List<Product> getProducts(@RequestParam("query") String query) {
        return productService.getByContainedName(query);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}
