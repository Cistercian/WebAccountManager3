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
import ru.hd.olaf.mvc.repository.ProductRepository;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.SecurityService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "/page-amount", method = RequestMethod.GET)
    public ModelAndView getPageAmount(){
        logger.debug(String.format("function: %s.", "getPageAmount"));

        ModelAndView modelAndView = new ModelAndView("/data/page-amount");

        Map<Integer, String> categories = categoryService.getIdAndNameByCurrentUser();
        modelAndView.addObject("categories", categories);

        logger.debug(String.format("Map 'categories' for injecting: %s.", categories.toString()));

        return modelAndView;
    }

    @RequestMapping(value = "/page-amount/getCategoriesIdAndName", method = RequestMethod.GET)
    public @ResponseBody Map<Integer, String> getCategoriesIdAndName() {
        return categoryService.getIdAndNameByCurrentUser();
    }

    @RequestMapping(value = "/page-amount/save", method = RequestMethod.POST)
    public String saveAmount(@RequestParam(value = "id") Integer id,
                            @RequestParam(value = "categoryId") Integer categoryId,
                            @RequestParam(value = "productName") String productName,
                            @RequestParam(value = "name") String name,
                            @RequestParam(value = "price") BigDecimal price,
                            @RequestParam(value = "date") Date amountsDate,
                            @RequestParam(value = "details") String details,
                            @RequestParam(value = "submitAmmount") String submitAmmount) {

        Category category = categoryService.getById(categoryId);
        Amount amount;
        try {
            amount = amountService.getById(id);
        } catch (Exception e) {
            logger.debug(String.format("function: %s. Not found Amount, reason:",
                    "saveAmount", e.getMessage()));
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

        amountService.add(amount);

        logger.debug(String.format("function: %s. User: %s, Amount=%s",
                "saveAmount", securityService.findLoggedUsername(), amount));

        return "index";
    }

    @RequestMapping(value = "/page-amount/{id}", method = RequestMethod.GET)
    public ModelAndView displayAmount(@PathVariable("id") int id){
        logger.debug(String.format("%s. id: %d",
                "displayAmount()", id));
        //TODO: NPE
        Amount amount = amountService.getById(id);

        ModelAndView modelAndView = new ModelAndView("/data/page-amount");
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

        logger.debug(String.format("Amount: %s",
                amount));

        Map<Integer, String> categories = categoryService.getIdAndNameByCurrentUser();
        modelAndView.addObject("categories", categories);

        logger.debug(String.format("Map 'categories' for injecting: %s.", categories.toString()));

        return modelAndView;
    }

    @RequestMapping(params = {"id"}, value = "/page-amount/delete", method = RequestMethod.POST)
    public String deleteAmount(@RequestParam(value = "id") Integer id){
        logger.debug(String.format("%s. id: %d",
                "deleteAmount()", id));
        //TODO: NPE
        Amount amount = amountService.getById(id);

        logger.debug(String.format("Amount: %s",
                amount));

        String result = amountService.delete(id);

        logger.debug(String.format("Result: %s",
                result));

        return "index";
    }

    @RequestMapping(value = "/page-amount/getProducts", method = RequestMethod.GET)
    public @ResponseBody
    List<Product> getProducts(@RequestParam("query") String query) {
        logger.debug(String.format("Function %s. params: %s", "getProducts()", query));

        return productService.getByContainedName(query);
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}
