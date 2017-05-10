package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
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
public class NotificationController {

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

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @RequestMapping(value = "/account/notification", method = RequestMethod.GET)
    public ModelAndView getViewNotification(@RequestParam(value = "id", required = false) Integer id){
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/login/notification");

        Limit limit = null;
        JsonResponse response = limitService.getById(id);

        if (response.getType() != ResponseType.ERROR && response.getEntity() != null) {
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

    @RequestMapping(value = "/account/notification", method = RequestMethod.POST)
    public @ResponseBody JsonResponse saveNotification(@ModelAttribute("limitForm") Limit limitForm,
                                                        BindingResult bindingResult){
        logger.debug(LogUtil.getMethodName());

        JsonResponse response = new JsonResponse();

        limitForm.setUserId(securityService.findLoggedUser());

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
