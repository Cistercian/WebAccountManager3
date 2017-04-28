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
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.math.BigDecimal;
import java.util.Date;
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
    @Autowired
    private SecurityService securityService;

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
        JsonResponse response;
        Object entity = null;
        if (className.equalsIgnoreCase(Amount.class.getSimpleName())) {
            logger.debug("Обрабатывается класс Amount");

            modelAndView = new ModelAndView("/data/page-amount");
            response = amountService.getById(id);

            if (response.getType() == ResponseType.ERROR)
                modelAndView.addObject("response", response.getMessage());
            else if (response.getType() == ResponseType.SUCCESS) {
                entity = response.getEntity();

                logger.debug(String.format("Entity: %s", entity));

                modelAndView = fillViewAmount(modelAndView, entity);
            }

        } else if (className.equalsIgnoreCase(Category.class.getSimpleName())) {
            logger.debug("Обрабатывается класс Category");

            modelAndView = new ModelAndView("/data/page-category");

            response = categoryService.getById(id);
            if (response.getType() == ResponseType.ERROR)
                modelAndView.addObject("response", response.getMessage());
            else if (response.getType() == ResponseType.SUCCESS) {
                entity = response.getEntity();

                logger.debug(String.format("Entity: %s", entity));

                modelAndView = fillViewCategory(modelAndView, entity);
            }
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
                            @RequestParam(value = "id") Integer id,
                            @RequestParam(value = "categoryId", required = false) Integer categoryId,
                            @RequestParam(value = "parentId", required = false) Integer parentId,
                            @RequestParam(value = "productName", required = false) String productName,
                            @RequestParam(value = "name") String name,
                            @RequestParam(value = "price", required = false) BigDecimal price,
                            @RequestParam(value = "date", required = false) Date amountsDate,
                            @RequestParam(value = "type", required = false) Byte type,
                            @RequestParam(value = "details") String details) {
        logger.debug(LogUtil.getMethodName());

        JsonResponse response = null;

        if (className.equalsIgnoreCase(Amount.class.getSimpleName())) {
            logger.debug("Инициализация сохранения записи Amount");

            response = saveAmount(id, categoryId, productName, name, price, amountsDate, details);

        } else if (className.equalsIgnoreCase(Category.class.getSimpleName())) {
            logger.debug("Инициализация сохранения записи Category");

            response = saveCategory(id, parentId, name, type, details);
        }

        return response;
    }

    @RequestMapping(value = "/page-data/delete/{className}/{id}", method = RequestMethod.POST)
    public @ResponseBody JsonResponse deleteEntity(@PathVariable(value = "className") String className,
                                      @PathVariable(value = "id") Integer id) {
        logger.debug(LogUtil.getMethodName());
        JsonResponse response = null;

        try {
            if (className.equalsIgnoreCase(Amount.class.getSimpleName())) {
                logger.debug("Инициализация удаления записи Amount");

                response = amountService.getById(id);
                if (response.getType() != ResponseType.SUCCESS)
                    return response;
                response = amountService.delete((Amount)response.getEntity());

            } else if (className.equalsIgnoreCase(Category.class.getSimpleName())) {
                logger.debug("Инициализация удаления записи Category");

                response = categoryService.getById(id);
                if (response.getType() != ResponseType.SUCCESS)
                    return response;
                response = categoryService.delete((Category) response.getEntity());
            }
        } catch (CrudException e) {
            String message = String.format("Произошла неизвестная ошибка: %s", e.getCause());
            response = new JsonResponse();
            response.setType(ResponseType.ERROR);
            response.setMessage(message);
        }

        return response;
    }

    /**
     * Функция сохранения записи Amount
     * @param id
     * @return
     */
    private JsonResponse saveAmount(Integer id,
                                   Integer categoryId,
                                   String productName,
                                   String name,
                                   BigDecimal price,
                                   Date amountsDate,
                                   String details) {
        logger.debug(LogUtil.getMethodName());
        JsonResponse response;
        Amount amount;

        response = amountService.getById(id);
        if (response.getType() == ResponseType.ERROR)
            return response;
        else
            amount = (Amount) response.getEntity();

        //если запись с переданным id не существует, то создаем новую.
        if (amount == null) {
            logger.debug("Запись Amount c id = %d не найдена. Создаем новую.", id);
            amount = new Amount();
        }

        //вычисляем категорию
        response = categoryService.getById(categoryId);

        if (response.getType() != ResponseType.SUCCESS) {
            response.setType(ResponseType.ERROR);
            return response;
        }

        Category category = (Category) response.getEntity();

        if (category == null) {
            String message = String.format("Отмена операции: не найдена категория с id %d", categoryId);
            logger.debug(message);
            return new JsonResponse(ResponseType.ERROR, message);
        }

        //вычисляем товарную группу
        Product product = productService.getExistedOrCreated(productName);

        amount.setCategoryId(category);
        amount.setName(name);
        amount.setPrice(price);
        amount.setAmountsDate(amountsDate);
        amount.setDetails(details);
        amount.setUserId(securityService.findLoggedUser());
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
     * Функция сохранаяет/обновляет запись в таблице Category
     * @param id
     * @param parentId
     * @param name
     * @param type
     * @param details
     * @return
     */
    private JsonResponse saveCategory(Integer id,
                                      Integer parentId,
                                      String name,
                                      Byte type,
                                      String details){
        logger.debug(LogUtil.getMethodName());

        JsonResponse response = categoryService.getById(id);
        if (response.getType() == ResponseType.ERROR)
            return response;

        Category category = (Category) response.getEntity();

        if (category == null) {
            logger.debug("Запись Сategory c id = %d не найдена. Создаем новую.", id);
            category = new Category();
        }

        //обрабатываем рожительскую категорию
        logger.debug("Обрабатываем родительскую категорию");
        response = categoryService.getById(parentId);
        if (response.getType() == ResponseType.ERROR)
            return response;

        Category parent = (Category) response.getEntity();
        if (parent != null) {
            category.setParentId(parent);
        }

        category.setName(name);
        category.setType(type);
        category.setDetails(details);
        category.setUserId(securityService.findLoggedUser());

        try {
            categoryService.save(category);
        } catch (CrudException e) {
            String message = String.format("Возникла ошибка при сохранении данных в БД. \n" +
                    "Error message: %s", e.getMessage());
            logger.error(message);

            return new JsonResponse(ResponseType.ERROR, message);
        }

        String message = String.format("Запись успешно сохранена в БД.");
        logger.debug(message + "\n" + category);

        return new JsonResponse(ResponseType.SUCCESS, message);
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
