package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
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
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
     *
     * @return ModelAndView
     */
    @RequestMapping(value = "/page-data/amount", method = RequestMethod.GET)
    public ModelAndView getViewAmount() {
        logger.debug(LogUtil.getMethodName());

        return getViewEntity(Amount.class.getSimpleName(), null);
    }

    /**
     * Функция просмотра пустой страницы заполнения Category
     *
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

        ModelAndView modelAndView = new ModelAndView("/data/data");
        JsonResponse response;

        if (className.equalsIgnoreCase(Amount.class.getSimpleName())) {
            logger.debug("Обрабатывается класс Amount");

            response = amountService.getById(id);

            if (response.getType() == ResponseType.ERROR)
                modelAndView.addObject("response", response.getMessage());
            else {
                logger.debug(String.format("Response: %s", response));

                modelAndView = fillViewAmount(modelAndView, response);
            }

        } else if (className.equalsIgnoreCase(Category.class.getSimpleName())) {
            logger.debug("Обрабатывается класс Category");

            response = categoryService.getById(id);
            if (response.getType() == ResponseType.ERROR)
                modelAndView.addObject("response", response.getMessage());
            else {
                logger.debug(String.format("Response: %s", response));

                modelAndView = fillViewCategory(modelAndView, response);
            }
        } else if (className.equalsIgnoreCase(Product.class.getSimpleName())) {
            logger.debug("Обрабатывается класс Product");

            response = productService.getById(id);
            if (response.getType() == ResponseType.ERROR)
                modelAndView.addObject("response", response.getMessage());
            else {
                logger.debug(String.format("Response: %s", response));

                modelAndView = fillViewProduct(modelAndView, response);
            }
        }

        //TODO: refactoring
        if (!className.equalsIgnoreCase(Product.class.getSimpleName())) {
            List<Category> list = categoryService.getAll();

            logger.debug(String.format("Список 'list' для заполнения выпадающего списка: %s.",
                    list.toString()));
            modelAndView.addObject("list", list);
        } else {
            List<Product> list = productService.getAll();

            logger.debug(String.format("Список 'list' для заполнения выпадающего списка: %s.",
                    list.toString()));
            modelAndView.addObject("list", list);
        }

        return modelAndView;
    }

    @RequestMapping(value = "/page-data/save", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonResponse saveEntity(@RequestParam(value = "className") String className,
                            @RequestParam(value = "id") Integer id,
                            @RequestParam(value = "categoryId", required = false) Integer categoryId,
                            @RequestParam(value = "productName", required = false) String productName,
                            @RequestParam(value = "name") String name,
                            @RequestParam(value = "price", required = false) BigDecimal price,
                            @RequestParam(value = "date", required = false) Date amountsDate,
                            @RequestParam(value = "parentId", required = false) Integer parentId,
                            @RequestParam(value = "type", required = false) Byte type,
                            @RequestParam(value = "details", required = false) String details,
                            @RequestParam(value = "mergeProductId", required = false) Integer mergeProductId) {
        logger.debug(LogUtil.getMethodName());

        JsonResponse response = null;

        if (className.equalsIgnoreCase(Amount.class.getSimpleName())) {
            logger.debug("Инициализация сохранения записи Amount");

            response = saveAmount(id, categoryId, productName, name, price, amountsDate, details);

        } else if (className.equalsIgnoreCase(Category.class.getSimpleName())) {
            logger.debug("Инициализация сохранения записи Category");

            response = saveCategory(id, parentId, name, type, details);
        } else if (className.equalsIgnoreCase(Product.class.getSimpleName())) {
            logger.debug("Инициализация сохранения записи Product");

            response = saveProduct(id, name, mergeProductId);
        }

        return response;
    }

    @RequestMapping(value = "/page-data/delete", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonResponse deleteEntity(@RequestParam(value = "className") String className,
                              @RequestParam(value = "id") Integer id) {
        logger.debug(LogUtil.getMethodName());
        JsonResponse response = new JsonResponse();

        try {
            if (className.equalsIgnoreCase(Amount.class.getSimpleName())) {
                logger.debug("Инициализация удаления записи Amount");

                response = amountService.getById(id);
                if (response.getEntity() == null) {
                    logger.debug(response.getType() + ":" + response.getMessage());
                    return response;
                }
                response = amountService.delete((Amount) response.getEntity());

            } else if (className.equalsIgnoreCase(Category.class.getSimpleName())) {
                logger.debug("Инициализация удаления записи Category");

                response = categoryService.getById(id);
                if (response.getEntity() == null) {
                    logger.debug(response.getType() + ":" + response.getMessage());
                    return response;
                }
                response = categoryService.delete((Category) response.getEntity());
            } else if (className.equalsIgnoreCase(Product.class.getSimpleName())) {
                logger.debug("Инициализация удаления записи Product");

                response = productService.getById(id);
                if (response.getEntity() == null) {
                    logger.debug(response.getType() + ":" + response.getMessage());
                    return response;
                }
                response = productService.delete((Product) response.getEntity());
            }
        } catch (CrudException e) {
            String message = String.format("Произошла неизвестная ошибка: %s", e.getCause());

            logger.error(message);

            response = new JsonResponse();
            response.setType(ResponseType.ERROR);
            response.setMessage(message);
        }

        logger.debug(response.getMessage());

        return response;
    }

    /**
     * Функция возвращает список товарных групп для автозаполнения поля
     * @param query строка для поиска
     * @return лист продуктов
     */
    @RequestMapping(value = "/page-data/getProducts", method = RequestMethod.GET)
    public @ResponseBody
    List<Product> getProducts(@RequestParam("query") String query) {
        logger.debug(String.format("Function %s. params: %s", "getProducts()", query));

        return productService.getByContainedName(query);
    }


    /**
     * Функция сохранения записи Amount
     *
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

        String message = "Запись успешно сохранена в БД.";
        logger.debug(message + "\n" + amount);

        return new JsonResponse(ResponseType.SUCCESS, message);
    }

    /**
     * Функция сохранаяет/обновляет запись в таблице Category
     *
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
                                      String details) {
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

        String message = "Запись успешно сохранена в БД.";
        logger.debug(message + "\n" + category);

        return new JsonResponse(ResponseType.SUCCESS, message);
    }

    /**
     * Функция сохраняет запись Product
     *
     * @param id             редактируемой записи
     * @param name           имя записи
     * @param mergeProductId id сущности, с которой необходимо произвести объединение
     * @return объект JsonResponse
     */
    private JsonResponse saveProduct(Integer id,
                                     String name,
                                     Integer mergeProductId) {
        logger.debug(LogUtil.getMethodName());

        JsonResponse response = productService.getById(id);
        if (response.getType() == ResponseType.ERROR)
            return response;

        Product product = (Product) response.getEntity();

        if (product == null) {
            response.setType(ResponseType.ERROR);
            response.setMessage("Запись не найдена.");
            return response;
        }

        product.setName(name);

        try {
            productService.save(product);
        } catch (CrudException e) {
            String message = String.format("Возникла ошибка при сохранении данных в БД. \n" +
                    "Error message: %s", e.getMessage());
            logger.error(message);

            return new JsonResponse(ResponseType.ERROR, message);
        }

        String message = "Запись успешно сохранена в БД.";
        logger.debug(message + "\n" + product);

        //брабатываем объединение записей
        if (mergeProductId != null && mergeProductId != -1) {
            response = productService.getById(mergeProductId);

            if (response.getEntity() == null) {
                message += "Возникла ошибка при поиске записи товарной группы для объединения. Откат объединения.";
                logger.error(message);
            } else {
                Product mergeProduct = (Product) response.getEntity();

                for (Amount amount : amountService.getByProduct(mergeProduct, LocalDate.MIN, LocalDate.MAX)) {

                    response = saveAmount(amount.getId(),
                            amount.getCategoryId().getId(),
                            product.getName(),
                            amount.getName(),
                            amount.getPrice(),
                            amount.getAmountsDate(),
                            amount.getDetails());

                    logger.debug(String.format("Редактирование записи amount (перевод в другую товарную группу):" +
                            "результат %s (%s).", response.getType(), response.getMessage()));

                }

                //удаляем объединяемую сущность
                try {
                    response = productService.delete(mergeProduct);
                    logger.debug(String.format("Результат удаления объединяемой записи: %s (%s)",
                            response.getType(), response.getMessage()));
                    message = message + "(Редактирование объединяемой записи: " + response.getMessage() + ")";
                } catch (CrudException e) {
                    message += e.getMessage();
                }
            }
        }

        return new JsonResponse(ResponseType.SUCCESS, message);
    }

    /**
     * Функция заполняет ModelAndView класса Amount
     *
     * @param modelAndView заполняемый ModelAndView
     * @param response     ранее полученный объект ответа сервиса
     * @return заполненный ModelAndView
     */
    private ModelAndView fillViewAmount(ModelAndView modelAndView, JsonResponse response) {
        logger.debug(LogUtil.getMethodName());
        Amount amount = (Amount) response.getEntity();

        modelAndView.addObject("className", "amount");

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
                logger.debug("Товарная группа не определена");
                //TODO: Логическая ошибка данных БД?
            }
        } else if (response.getType() == ResponseType.SUCCESS) {
            String message = "Запрошеный объект не найден";
            modelAndView.addObject("response", message);

            logger.debug(message);
        } else {
            //рисуем пустую форму
            logger.debug("Инициализация пустой формы");

            modelAndView.addObject("date", LocalDate.now());
        }

        return modelAndView;
    }

    /**
     * Функция заполняет ModelAndView класса Category
     *
     * @param modelAndView заполняемый ModelAndView
     * @param response     Ответ, полученный от сервиса
     * @return заполненный ModelAndView
     */
    private ModelAndView fillViewCategory(ModelAndView modelAndView, JsonResponse response) {
        logger.debug(LogUtil.getMethodName());
        Category category = (Category) response.getEntity();

        modelAndView.addObject("className", "category");

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
                logger.debug("Родительская категория отсутствует");

                modelAndView.addObject("parentName", "Отсутствует");
            }


            if (category.getType() == 0)
                modelAndView.addObject("typeIncome", "true");


        } else if (response.getType() == ResponseType.INFO) {
            String message = "Запрошеный объект не найден";
            modelAndView.addObject("response", message);

            logger.debug(message);
        }

        return modelAndView;
    }

    /**
     * Функция заполняет ModelAndView класса Product
     *
     * @param modelAndView заполняемый ModelAndView
     * @param response     Ответ, полученный от сервиса
     * @return заполненный ModelAndView
     */
    private ModelAndView fillViewProduct(ModelAndView modelAndView, JsonResponse response) {
        logger.debug(LogUtil.getMethodName());
        Product product = (Product) response.getEntity();

        modelAndView.addObject("className", "product");

        if (product != null) {
            logger.debug(String.format("Обрабатываемый объект: %s", product));

            modelAndView.addObject("id", product.getId());
            modelAndView.addObject("name", product.getName());

        } else if (response.getType() == ResponseType.INFO) {
            String message = "Запрошеный объект не найден";
            modelAndView.addObject("response", message);

            logger.debug(message);
        }

        return modelAndView;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}
