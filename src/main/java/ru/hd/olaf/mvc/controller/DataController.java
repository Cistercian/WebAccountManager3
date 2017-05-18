package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.service.*;
import ru.hd.olaf.mvc.validator.AmountValidator;
import ru.hd.olaf.mvc.validator.CategoryValidator;
import ru.hd.olaf.mvc.validator.ProductValidator;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    @Autowired
    private UtilService utilService;
    @Autowired
    private CategoryValidator categoryValidator;
    @Autowired
    private AmountValidator amountValidator;
    @Autowired
    private ProductValidator productValidator;

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @RequestMapping(value = "/page-data/delete", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonResponse deleteEntity(@RequestParam(value = "className") String className,
                              @RequestParam(value = "id") Integer id) {
        logger.debug(LogUtil.getMethodName());
        return utilService.deleteEntity(className, id);
    }

    /**
     * Функция возвращает список товарных групп для автозаполнения поля
     * @param query строка для поиска
     * @return лист продуктов
     */
    @RequestMapping(value = "/page-data/getProducts", method = RequestMethod.GET)
    public @ResponseBody
    List<Product> getProducts(@RequestParam("query") String query) {
        return productService.getByContainedName(query);
    }

    /**
     * Функция сохранения категории
     * @param categoryForm html форма с заполненными данными сущности
     * @param parentId id корневой категории
     * @param refererParam адрес предыдущей страницы
     * @param bindingResult для отображения ошибок
     * @return ModelAndView "data"
     */
    @RequestMapping(value = "/category/save", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public ModelAndView saveCategory(@RequestHeader(value = "Referer", required = false) String refererHeader,
                                     @ModelAttribute("categoryForm") Category categoryForm,
                                     @RequestParam(value = "parent") Integer parentId,
                                     @RequestParam(value = "referer", required = false) String refererParam,
                                     BindingResult bindingResult){
        logger.debug(LogUtil.getMethodName());

        logger.debug("Обрабатываем родительскую категорию");
        JsonResponse response = categoryService.getById(parentId);
        Category parent = null;
        if (response.getEntity() != null) {
            parent = (Category) response.getEntity();
            logger.debug(String.format("Родительская категория: %s", parent.toString()));

            categoryForm.setParentId(parent);
        } else {
            logger.debug(String.format("Ошибка опрделения родительской категории по id = %d: %s",
                    parentId, response.getMessage()));
        }

        categoryForm.setUserId(securityService.findLoggedUser());
        logger.debug(String.format("Обрабатываемая сущность: %s", categoryForm.toString()));

        ModelAndView modelAndView = new ModelAndView("/data/data");
        modelAndView.addObject("className", "category");

        categoryValidator.validate(categoryForm, bindingResult);

        checkErrorsAndSave(categoryForm, refererHeader, bindingResult, modelAndView);

        modelAndView.addObject("parents", categoryService.getAll());
        modelAndView.addObject("parent", parent);
        modelAndView.addObject("category", categoryForm);

        modelAndView.addObject("previousPage", refererParam);
        if (categoryForm.getId() == null)
            modelAndView.addObject("name", "Новая запись");
        else
            modelAndView.addObject("name", categoryForm.getName());

        return modelAndView;
    }

    /**
     * Функция просмотра страницы редактирования категории
     * @param referer адрес предыдущей страницы
     * @param id id записи (необязательно)
     * @param model model?
     * @return Наименование view("data")
     */
    @RequestMapping(value = "/category", method = RequestMethod.GET)
    public String getViewCategory(@RequestHeader(value = "Referer", required = false) String referer,
                                  @RequestParam(value = "id", required = false) Integer id,
                                  Model model){
        logger.debug(LogUtil.getMethodName());

        Category category;
        JsonResponse response = categoryService.getById(id);

        if (response.getEntity() != null) {

            category = (Category) response.getEntity();
            model.addAttribute("id", id);
            model.addAttribute("name", category.getName());
            model.addAttribute("parent", (category.getParentId() != null ? category.getParentId() : ""));
            logger.debug(String.format("Обрабатывается категория: %s", category.toString()));

        } else {

            category = new Category();
            category.setType((byte)1);
            model.addAttribute("name", "Новая запись");
            logger.debug(String.format("Категория с id = %d не найдена. Создаем новую.", id));

        }

        model.addAttribute("className", "category");
        model.addAttribute("parents", categoryService.getAll());
        model.addAttribute("categoryForm", category);

        model.addAttribute("previousPage", "location.href='" + referer + "';");

        return "data/data";
    }

    /**
     * Функция просмотра страницы редактирования amount
     * @param referer адрес предыдущей страницы
     * @param id id записи (необяхательно)
     * @param model текущеая модель
     * @return наименование view("data")
     */
    @RequestMapping(value = "/amount", method = RequestMethod.GET)
    public String getViewAmount(@RequestHeader(value = "Referer", required = false) String referer,
                                @RequestParam(value = "id", required = false) Integer id,
                                Model model){
        logger.debug(LogUtil.getMethodName());

        Amount amount;
        JsonResponse response = amountService.getById(id);

        if (response.getEntity() != null) {

            amount = (Amount) response.getEntity();
            model.addAttribute("id", id);
            model.addAttribute("product", amount.getProductId());
            model.addAttribute("category", amount.getCategoryId());

            logger.debug(String.format("Обрабатывается запись: %s", amount.toString()));

        } else {
            amount = new Amount();
            amount.setDate(new Date());

            logger.debug(String.format("Категория с id = %d не найдена. Создаем новую.", id));
        }

        model.addAttribute("className", "amount");
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("amountForm", amount);

        model.addAttribute("previousPage", "location.href='" + referer + "';");

        return "data/data";
    }

    /**
     * Функция сохранения сущности amount
     * @param amountForm сама сущность
     * @param productName наименование товарной группы
     * @param categoryId id категории
     * @param refererParam адрес предыдущей страницы
     * @param bindingResult результат валидации
     * @return ModelAndView
     */
    @RequestMapping(value = "/amount/save", method = RequestMethod.POST)
    public ModelAndView saveAmount(@RequestHeader(value = "Referer", required = false) String refererHeader,
                                   @ModelAttribute("amountForm") Amount amountForm,
                                   @RequestParam(value = "productName") String productName,
                                   @RequestParam(value = "category") Integer categoryId,
                                   @RequestParam(value = "referer", required = false) String refererParam,
                                   BindingResult bindingResult) {
        logger.debug(LogUtil.getMethodName());

        //вычисляем категорию
        JsonResponse response = categoryService.getById(categoryId);

        if (response.getType() == ResponseType.SUCCESS) {
            Category category = (Category) response.getEntity();
            amountForm.setCategoryId(category);

            logger.debug(String.format("Категория: %s", category));
        } else {
            logger.debug("Возникла ошибка определения категории");
        }

        //вычисляем товарную группу
        if (productName.trim().length() > 0) {
            Product product = productService.getExistedOrCreated(productName);
            amountForm.setProductId(product);
        }
        //указываем владельца
        amountForm.setUserId(securityService.findLoggedUser());

        logger.debug(String.format("Обрабатываемая сущность: %s, category: %s, product: %s",
                amountForm.toString(),
                amountForm.getCategoryId() != null ? amountForm.getCategoryId() : "",
                amountForm.getProductId() != null ? amountForm.getProductId() : ""));

        ModelAndView modelAndView = new ModelAndView("/data/data");
        modelAndView.addObject("className", "amount");

        amountValidator.validate(amountForm, bindingResult);

        checkErrorsAndSave(amountForm, refererHeader, bindingResult, modelAndView);

        modelAndView.addObject("product", amountForm.getProductId());
        modelAndView.addObject("category", amountForm.getCategoryId());
        modelAndView.addObject("amountForm", amountForm);

        modelAndView.addObject("categories", categoryService.getAll());

        modelAndView.addObject("previousPage", refererParam);

        return modelAndView;
    }

    /**
     * Функция просмотра страницы редактирования товарной группы (product)
     * @param referer адрес предыдущей страницы
     * @param id id сущности (необязательно)
     * @param model model?
     * @return наименование view("data")
     */
    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public String getViewProduct(@RequestHeader(value = "Referer", required = false) String referer,
                                 @RequestParam(value = "id", required = false) Integer id,
                                 Model model){
        logger.debug(LogUtil.getMethodName());

        Product product;
        JsonResponse response = productService.getById(id);

        if (response.getEntity() != null) {
            product = (Product) response.getEntity();
            model.addAttribute("id", id);
            model.addAttribute("name", product.getName());
            logger.debug(String.format("Обрабатывается запись: %s", product.toString()));
        } else {
            model.addAttribute("name", "Новая запись");
            logger.debug(String.format("Товарная группа с id = %d не найдена. ", id));
            return "index";
        }

        model.addAttribute("className", "product");
        model.addAttribute("products", productService.getAll());
        model.addAttribute("productForm", product);

        model.addAttribute("previousPage", "location.href='" + referer + "';");

        return "data/data";
    }

    /**
     * Функция сохранения товарной группы (product)
     * @param productForm html форма с заполненными реквизитами
     * @param mergeId id товарной группы, с которой будем объединять
     * @param refererParam адрес предыдущей страницы
     * @param bindingResult для отображения ошибок валидации
     * @return ModelAndView(data)
     */
    @RequestMapping(value = "/product/save", method = RequestMethod.POST)
    public ModelAndView saveProduct(@RequestHeader(value = "Referer", required = false) String refererHeader,
                                    @ModelAttribute("productForm") Product productForm,
                                    @RequestParam(value = "productMerge") Integer mergeId,
                                    @RequestParam(value = "referer", required = false) String refererParam,
                                    BindingResult bindingResult) {
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/data/data");
        User currentUser = securityService.findLoggedUser();

        modelAndView.addObject("className", "product");

        //вычисляем объединяемую категорию
        Product mergeProduct;
        String message = "";
        JsonResponse response = productService.getById(mergeId);

        if (response.getEntity() != null) {

            mergeProduct = (Product) response.getEntity();
            modelAndView.addObject("mergeProduct", mergeProduct);

            //TODO: transaction
            if (mergeProduct != null) {
                logger.debug(String.format("Товарная группа для объединения: %s", mergeProduct));

                for (Amount amount : amountService.getByProductAndDate(currentUser, mergeProduct, null, null)) {
                    amount.setProductId(productForm);
                    response = utilService.saveEntity(amount);

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
                    message = message + "(Ошибка удаления объединяемой записи: " + e.getMessage() + ")";
                    logger.debug(message);
                }
            }
        }

        productForm.setUserId(currentUser);
        productValidator.validate(productForm, bindingResult);

        checkErrorsAndSave(productForm, refererHeader, bindingResult, modelAndView);

        modelAndView.addObject("productForm", productForm);
        modelAndView.addObject("products", productService.getAll());

        modelAndView.addObject("previousPage", refererParam);

        return modelAndView;
    }

    private void checkErrorsAndSave(Object entity, String url, BindingResult bindingResult, ModelAndView modelAndView) {
        JsonResponse response;
        if (bindingResult.hasErrors()) {
            logger.info("Ошибка валидиации!");
        } else {
            response = utilService.saveEntity(entity);

            modelAndView.addObject("responseMessage", response.getMessage());
            modelAndView.addObject("responseType", response.getType());
            modelAndView.addObject("responseUrl", url);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }


}
