package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Limit;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.service.*;
import ru.hd.olaf.mvc.validator.LimitValidator;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by d.v.hozyashev on 10.05.2017.
 */
@Controller
public class LimitsController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private LimitService limitService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private LimitValidator limitValidator;
    @Autowired
    private MessageSource messageSource;

    private static final Logger logger = LoggerFactory.getLogger(LimitsController.class);

    /**
     * Функция отображения страницы просмотра таблицы лимитов
     * @param model model
     * @return наименование view(limits)
     */
    @RequestMapping(value = "limits", method = RequestMethod.GET)
    public String getViewLimits(Model model){
        logger.debug(LogUtil.getMethodName());

        //данные для таблицы лимитов
        Map<Byte, String> periods = new TreeMap<Byte, String>();
        periods.put((byte)0, "В день");
        periods.put((byte)1, "В неделю");
        periods.put((byte)2, "В месяц");
        model.addAttribute("periods", periods);

        Map<String, String> types = new HashMap<String, String>();
        types.put("category", "Категория");
        types.put("product", "Товарная группа");
        model.addAttribute("types", types);

        List<Limit> limits = limitService.getAll();
        model.addAttribute("limits", limits);

        return "data/limits";
    }

    /**
     * Функция просмотра сущности таблицы limits
     * @param id id сущности (необязательно)
     * @return  ModelAndView(notification)
     */
    @RequestMapping(value = "/limits/notification", method = RequestMethod.GET)
    public ModelAndView getViewNotification(@RequestParam(value = "id", required = false) Integer id){
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/data/notification");

        Limit limit;
        JsonResponse response = limitService.getById(id);

        if (response.getEntity() != null) {
            limit = (Limit) response.getEntity();
            modelAndView.addObject("id", id);

            logger.debug(String.format("Обрабатывается запись: %s", limit));
        } else {
            limit = new Limit();
        }

        modelAndView.addObject("limitForm", limit);

        fillModelNotification(modelAndView);

        return modelAndView;
    }

    /**
     * Функция сохранения сущности таблицы limits
     * @param limitForm html форма с заполненными атрибутами
     * @param bindingResult для валидации формы
     * @return JsonResponse (результат валидации)
     */
    @RequestMapping(value = "/limits/notification", method = RequestMethod.POST)
    public @ResponseBody JsonResponse saveNotification(@ModelAttribute("limitForm") Limit limitForm,
                                                        BindingResult bindingResult){
        logger.debug(LogUtil.getMethodName());

        JsonResponse response = new JsonResponse();

        limitForm.setUserId(securityService.findLoggedUser());

        logger.debug("Обрабатываем подчиненную сущность");
        if ("category".equalsIgnoreCase(limitForm.getType())){
            response = categoryService.getById(limitForm.getEntityId());

            if (response.getEntity() != null) {
                Category category = (Category) response.getEntity();
                logger.debug(String.format("Родительская категория: %s", category.toString()));

                limitForm.setCategoryId(category);
            } else {
                logger.debug(String.format("Ошибка определения подчиненной категории по id = %d: %s",
                        limitForm.getEntityId(), response.getMessage()));
            }
        } else if ("product".equalsIgnoreCase(limitForm.getType())){
            response = productService.getById(limitForm.getEntityId());

            if (response.getEntity() != null) {
                Product product = (Product) response.getEntity();
                logger.debug(String.format("Родительская товарная группа: %s", product.toString()));

                limitForm.setProductId(product);
            } else {
                logger.debug(String.format("Ошибка определения подчиненной категории по id = %d: %s",
                        limitForm.getEntityId(), response.getMessage()));
            }
        }

        limitValidator.validate(limitForm, bindingResult);

        logger.debug(String.format("Обрабатывается сущность: %s", limitForm));

        if (bindingResult.hasErrors()) {
            logger.info("Ошибка валидиации!");
            String message = "";
            for (Object error : bindingResult.getAllErrors()){

                if(error instanceof FieldError) {
                    message = message + "\n" + messageSource.getMessage((FieldError)error, null);
                }
            }

            logger.debug(message);

            response.setMessage(message);
            response.setType(ResponseType.ERROR);
        } else {
            response = utilService.saveEntity(limitForm);
        }

        return response;
    }

    /**
     * Функция заполнения modelAndView данными из БД
     * @param modelAndView используемая ModelAndView
     */
    private void fillModelNotification(ModelAndView modelAndView) {
        logger.debug(LogUtil.getMethodName());

        Map<String, String> types = new HashMap<String, String>();
        types.put("category", "Категория");
        types.put("product", "Товарная группа");
        modelAndView.addObject("types", types);

        List<Category> categories = categoryService.getAll();
        List<Product> products = productService.getAll();

        modelAndView.addObject("categories", categories);
        modelAndView.addObject("products", products);

        Map<Byte, String> periods = new TreeMap<Byte, String>();
        periods.put((byte)0, "В день");
        periods.put((byte)1, "В неделю");
        periods.put((byte)2, "В месяц");

        modelAndView.addObject("periods", periods);
    }
}
