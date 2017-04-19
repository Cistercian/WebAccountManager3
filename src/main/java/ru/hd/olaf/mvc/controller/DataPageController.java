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
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.SecurityService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Olaf on 14.04.2017.
 */
@org.springframework.stereotype.Controller
public class DataPageController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AmountService amountService;
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

    @RequestMapping(params = {"id", "categoryId", "name", "price", "date", "details", "submitAmmount"},
            value = "/page-amount/amount/save", method = RequestMethod.POST)
    public String saveAmount(@RequestParam(value = "id") Integer id,
                            @RequestParam(value = "categoryId") Integer categoryId,
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
            logger.debug(String.format("function: %s. Not found amount, reson:",
                    "saveAmount", e.getMessage()));
            amount = new Amount();
        }

        amount.setCategoryId(category);
        amount.setName(name);
        amount.setPrice(price);
        amount.setAmountsDate(amountsDate);
        amount.setDetails(details);
        amount.setUserId(securityService.findLoggedUser());

        amountService.add(amount);

        logger.debug(String.format("function: %s. User: %s, Amount=%s",
                "saveAmount", securityService.findLoggedUsername(), amount));

        return "index";
    }

    @RequestMapping(value = "/page-amount/amount/{id}/display", method = RequestMethod.GET)
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

        logger.debug(String.format("Amount: %s",
                amount));

        Map<Integer, String> categories = categoryService.getIdAndNameByCurrentUser();
        modelAndView.addObject("categories", categories);

        logger.debug(String.format("Map 'categories' for injecting: %s.", categories.toString()));

        return modelAndView;
    }

    @RequestMapping(value = "/page-amount/amount/{id}/delete", method = RequestMethod.GET)
    public String deleteAmount(@PathVariable("id") int id){
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

    @RequestMapping(params = {"id", "name", "details", "type"}, value = "/page-amount/addCategory", method = RequestMethod.POST)
    public String addCategory(@RequestParam(value = "id") Integer id,
                            @RequestParam(value = "name") String name,
                            @RequestParam(value = "details") String details,
                            @RequestParam(value = "type") Byte type) {
        System.out.println("Controller add()_category is called");

        Category category = null;
        category = id == -1 ? new Category() : categoryService.getById(id);

        category.setType(type);
        category.setName(name);
        category.setDetails(details);
        category.setUserId(securityService.findLoggedUser());

        logger.debug(String.format("called function: %s. User: %s. CategoryEntity: %s",
                "addCategory", securityService.findLoggedUsername(), category));

        categoryService.add(category);
        return "index";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}
