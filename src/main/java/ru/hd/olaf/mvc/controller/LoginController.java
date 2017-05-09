package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Notification;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.service.*;
import ru.hd.olaf.mvc.validator.NotificationValidator;
import ru.hd.olaf.mvc.validator.UserValidator;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by d.v.hozyashev on 18.04.2017.
 */
@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationValidator notificationValidator;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model){
        logger.debug(LogUtil.getMethodName());
        model.addAttribute("userForm", new User());
        return "/login/registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        logger.debug(LogUtil.getMethodName());

        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()){
            return "/login/registration";
        }

        //mock data
        userForm.setFullName("fullName");
        userForm.setRole("ROLE_USER");
        userForm.setEnabled((byte)1);
        userService.save(userForm);

        securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/index";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout){
        logger.debug(LogUtil.getMethodName());

        //TODO: link to message.properties
        if (error != null){
            model.addAttribute("error", "Your username and password is invalid.");
        }
        if (logout != null) {
            model.addAttribute("logout", "You have been logged out successfully.");
        }

        return "/login/login";
    }

//    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
//    public String welcome(Model model) {
//        logger.debug(String.format("Controller: %s, called function: %s",
//                LoginController.class.getSimpleName(), "welcome"));
//
//        return "index";
//    }

    @RequestMapping(value = "account", method = RequestMethod.GET)
    public String getViewAccount(Model model){
        logger.debug(LogUtil.getMethodName());

        model.addAttribute("passwordForm", new User());

        return "login/account";
    }

    @RequestMapping(value = "account", method = RequestMethod.POST)
    public String setPassword(@ModelAttribute("passwordForm") User userForm, BindingResult bindingResult, Model model){
        logger.debug(LogUtil.getMethodName());

        User currentUser = securityService.findLoggedUser();
        userForm.setUsername(currentUser.getUsername());
        userForm.setFullName(currentUser.getFullName());
        userForm.setId(currentUser.getId());
        userForm.setEnabled(currentUser.getEnabled());
        userForm.setRole(currentUser.getRole());

        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()){
            logger.debug("Валидация не пройдена");

            return "login/account";
        }

        logger.debug(String.format("Сохранение записи %s", userForm));

        userService.save(userForm);

        userForm.setPassword("");
        userForm.setPasswordConfirm("");
        userForm.setPasswordOld("");

        model.addAttribute("response", "Пароль успешно изменен");

        return "login/account";
    }

    @RequestMapping(value = "/account/notification", method = RequestMethod.GET)
    public ModelAndView getViewNotification(){
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/login/notification");

        modelAndView.addObject("notificationForm", new Notification());

        fillModelNotification(modelAndView);

        return modelAndView;
    }

    private void fillModelNotification(ModelAndView modelAndView) {
        Map<String, String> types = new HashMap<String, String>();
        types.put("category", "Категория");
        types.put("product", "Товарная группа");
        modelAndView.addObject("types", types);

        List<Category> categories = categoryService.getAll();
        List<Product> products = productService.getAll();

        modelAndView.addObject("categories", categories);
        modelAndView.addObject("products", products);

        Map<Integer, String> periods = new TreeMap<Integer, String>();
        periods.put(0, "В день");
        periods.put(1, "В неделю");
        periods.put(2, "В месяц");

        modelAndView.addObject("periods", periods);
    }

    @RequestMapping(value = "/account/notification", method = RequestMethod.POST)
    public String saveNotification(@ModelAttribute("notificationForm") Notification notificationForm,
                                   BindingResult bindingResult){
        logger.debug(LogUtil.getMethodName());

        JsonResponse response = new JsonResponse();

        notificationForm.setUserId(securityService.findLoggedUser());



        notificationValidator.validate(notificationForm, bindingResult);

        if (bindingResult.hasErrors()) {
            logger.info("Ошибка валидиации!");
            return "login/notification";
        } else {
            response = utilService.saveEntity(notificationForm);
        }

        return "login/account";
    }
}
