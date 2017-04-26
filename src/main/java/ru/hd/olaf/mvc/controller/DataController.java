package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Olaf on 21.04.2017.
 */
@Controller
public class DataController {

    @Autowired
    private AmountService amountService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Функция прорисовки страницы page-data с произвольным содержимым
     *
     * @param className amount/category/product
     * @return new ModelAndView("/data/page-data")
     */
    @RequestMapping(value = "/page-data/custom/{class}", method = RequestMethod.GET)
    public ModelAndView displayPageData(@PathVariable("class") String className) {
        ModelAndView modelAndView = new ModelAndView("/data/page-data");
        Map<Class, Object> map = new HashMap<Class, Object>();

        if ("amount".equalsIgnoreCase(className)) {
            map.put(Amount.class, amountService.getAll());
        } else if ("category".equalsIgnoreCase(className)) {
            map.put(Category.class, categoryService.getAll());
        } else if ("product".equalsIgnoreCase(className)) {
            map.put(Product.class, productService.getAll());
        }

        modelAndView.addObject("data", map);

        return modelAndView;
    }

    /**
     * Функция просмотра пустой страницы заполнения Amount
     * @return ModelAndView
     */
    @RequestMapping(value = "/page-data/amount", method = RequestMethod.GET)
    public ModelAndView getViewAmount() {
        logger.debug(LogUtil.getMethodName());

        return getViewEntity(Amount.class.getSimpleName(), null);
    }

    /**
     * Функция просмотра пустой страницы заполнения Category
     * @return ModelAndView
     */
    @RequestMapping(value = "/page-data/category", method = RequestMethod.GET)
    public ModelAndView getViewCategory() {
        logger.debug(LogUtil.getMethodName());

        return getViewEntity(Category.class.getSimpleName(), null);
    }

    @RequestMapping(value = "/page-data/display/{className}/{id}", method = RequestMethod.GET)
    public ModelAndView getViewEntity(@PathVariable(value = "className") String className,
                                      @PathVariable(value = "id") Integer id) {
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = null;
        Object entity;

        try {

            if (className.equalsIgnoreCase(Amount.class.getSimpleName())) {
                logger.debug("Обрабатывается класс Amount");

                modelAndView = new ModelAndView("/data/page-amount");
                entity = amountService.getById(id);

                logger.debug(String.format("Entity: %s", entity));

                modelAndView = fillViewAmount(modelAndView, entity);

            } else if (className.equalsIgnoreCase(Category.class.getSimpleName())) {
                logger.debug("Обрабатывается класс Category");

                modelAndView = new ModelAndView("/data/page-category");
                entity = categoryService.getById(id);

                logger.debug(String.format("Entity: %s", entity));

                modelAndView = fillViewCategory(modelAndView, entity);
            }

        } catch (AuthException e) {
            logger.error(e.getMessage());

            modelAndView.addObject("response", e.getMessage());
        } catch (IllegalArgumentException e) {
            String message = String.format("Запрошенный объект с id %d не найден. \n" +
                    "Error message: %s", id, e.getMessage());

            logger.debug(message);
        }

        List<Category> categories = categoryService.getAll();
        logger.debug(String.format("Список 'categories' для заполнения выпадающего списка: %s.",
                categories.toString()));

        modelAndView.addObject("categories", categories);

        return modelAndView;
    }

    @RequestMapping(value = "/page-data/save/{className}", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse saveEntity(@PathVariable(value = "className") String className,
                            @RequestParam(value = "id") Integer id) {
        logger.debug(LogUtil.getMethodName());



        return null;
    }

    /**
     * Функция заполняет ModelAndView класса Amount
     *
     * @param modelAndView заполняемый ModelAndView
     * @param entity       сам объект
     * @return заполненный ModelAndView
     */
    private ModelAndView fillViewAmount(ModelAndView modelAndView, Object entity) {
        Amount amount = (Amount) entity;

        if (amount != null) {

            logger.debug(String.format("Обрабатываемый объект: %s", amount));

            modelAndView.addObject("name", amount.getName());
            modelAndView.addObject("date", amount.getAmountsDate());
            modelAndView.addObject("price", amount.getPrice());
            modelAndView.addObject("details", amount.getDetails());
            modelAndView.addObject("id", amount.getId());
            modelAndView.addObject("categoryId", amount.getCategoryId().getId());
            modelAndView.addObject("categoryName", amount.getCategoryId().getName());

            Product product = amount.getProductId();
            if (product != null) {
                logger.debug(String.format("Данные товарной группы: %s", product));

                modelAndView.addObject("productId", amount.getProductId().getId());
                modelAndView.addObject("productName", amount.getProductId().getName());
            } else {
                logger.debug(String.format("Товарная группа не определена"));

                //TODO: Логическая ошибка данных БД?
            }
        } else {
            String message = String.format("Запрошеный объект не найден");
            modelAndView.addObject("response", message);

            logger.debug(message);
        }

        return modelAndView;
    }

    /**
     * Функция заполняет ModelAndView класса Category
     *
     * @param modelAndView заполняемый ModelAndView
     * @param entity       сам объект
     * @return заполненный ModelAndView
     */
    private ModelAndView fillViewCategory(ModelAndView modelAndView, Object entity) {
        Category category = (Category) entity;

        if (category != null) {
            logger.debug(String.format("Обрабатываемый объект: %s", category));

            modelAndView.addObject("id", category.getId());
            modelAndView.addObject("name", category.getName());
            modelAndView.addObject("details", category.getDetails());

            Category parent = category.getParentId();
            if (parent != null) {
                logger.debug(String.format("Родительская категория: %s", parent));

                modelAndView.addObject("parentId", parent.getId());
                modelAndView.addObject("parentName", parent.getName());
            } else {
                logger.debug(String.format("Родительская категория отсутствует"));

                modelAndView.addObject("parentName", "Отсутствует");
            }


            if (category.getType() == 0)
                modelAndView.addObject("typeIncome", "true");


        } else {
            String message = String.format("Запрошеный объект не найден");
            modelAndView.addObject("response", message);

            logger.debug(message);
        }

        return modelAndView;
    }
}
