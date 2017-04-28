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
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.json.ResponseType;
import ru.hd.olaf.util.json.JsonResponse;

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
     *
     * @return
     */
    @RequestMapping(value = "/page-amount", method = RequestMethod.GET)
    public ModelAndView getViewPageAmount() {
        logger.debug(String.format("function: %s.", "getViewPageAmount()"));

        return getViewPageAmountByID(null);
    }

    /**
     * Функция просмотра записи amount (заполненная страница page-amount)
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/page-amount/{id}", method = RequestMethod.GET)
    public ModelAndView getViewPageAmountByID(@PathVariable("id") Integer id) {
        logger.debug(String.format("Function %s", "getViewPageAmountByID()", id));

        ModelAndView modelAndView = new ModelAndView("/data/page-amount");
        Amount amount;
        JsonResponse response = amountService.getById(id);
        if (response.getType() == ResponseType.ERROR)
            modelAndView.addObject("response", response.getMessage());


        amount = (Amount) response.getEntity();
        if (amount != null) {

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
            String message = String.format("Запрошен объект с id %d: %s", id, amount);
            logger.debug(message);
        } else {
            String message = String.format("Запрошеный объект с id %d не найден", id);
            modelAndView.addObject("response", message);

            logger.debug(message);
        }


        List<Category> categories = categoryService.getAll();

        logger.debug(String.format("Map 'categories' for injecting: %s.", categories.toString()));

        modelAndView.addObject("categories", categories);

        return modelAndView;
    }

    /**
     * Функция сохранения записи amount в БД
     *
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
    public
    @ResponseBody
    JsonResponse saveAmount(@RequestParam(value = "id") Integer id,
                            @RequestParam(value = "categoryId") Integer categoryId,
                            @RequestParam(value = "productName") String productName,
                            @RequestParam(value = "name") String name,
                            @RequestParam(value = "price") BigDecimal price,
                            @RequestParam(value = "date") Date amountsDate,
                            @RequestParam(value = "details") String details) {

        //TODO: throw exception
        Category category = null;
        JsonResponse response = categoryService.getById(categoryId);
        if (response.getType() != ResponseType.SUCCESS)
            return response;

        category = (Category) response.getEntity();

        if (category == null) {
            String message = String.format("Отмена операции: не найдена категория с id %d", categoryId);

            logger.debug(message);

            return new JsonResponse(ResponseType.ERROR, message);
        }

        Amount amount;


        amount = (Amount) amountService.getById(id).getEntity();


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

        try {
            amountService.save(amount);
        } catch (CrudException e) {
            String message = String.format("Возникла ошибка при сохранении данных в БД. \n" +
                    "Error message: %s", e.getMessage());
            logger.error(message);

            return new JsonResponse(ResponseType.ERROR, message);
        }

        String message = String.format("Запись успешно сохранена в БД.");
        logger.debug(message + "\n" + amount);

        return new JsonResponse(ResponseType.SUCCESS, message);
    }

    /**
     * Функция удаления записи по ее id
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/page-amount/delete", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonResponse deleteAmount(@RequestParam(value = "id") Integer id) {
        logger.debug(String.format("Function %s", "deleteAmount()"));

        Amount amount;

        amount = (Amount) amountService.getById(id).getEntity();

        JsonResponse response = new JsonResponse();
        if (amount != null) {

            logger.debug(String.format("Delete Amount: %s", amount));

            try {
                response = amountService.delete(amount);
            } catch (CrudException e) {
                String message = String.format("Произошла неизвестная ошибка: %s", e.getCause());
                response.setType(ResponseType.ERROR);
                response.setMessage(message);
            }

        } else {
            response.setType(ResponseType.ERROR);
            response.setMessage(String.format("Отмена операции: запись с id %d не найдена!", id));
        }

        logger.debug(String.format("Result: %s", response.getMessage()));

        return response;
    }

    /**
     * Функция получения списка таблицы product по совпадению (быстрый поиск для выпадающего списка)
     *
     * @param query
     * @return
     */
    @RequestMapping(value = "/page-amount/getProducts", method = RequestMethod.GET)
    public
    @ResponseBody
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
