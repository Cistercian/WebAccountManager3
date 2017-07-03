package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.mvc.service.UserService;
import ru.hd.olaf.mvc.validator.UserValidator;
import ru.hd.olaf.util.LogUtil;

import java.util.Locale;

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
    private MessageSource messageSource;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Функция вывода страницы регистрации
     * @param model model
     * @return view(registration)
     */
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model){
        logger.debug(LogUtil.getMethodName());
        model.addAttribute("userForm", new User());
        return "/login/registration";
    }

    /**
     * Функция регистрации нового пользователя
     * @param userForm html форма с атрибутами
     * @param bindingResult для валидации
     * @param model model
     * @return наименование view (registration при ошибках или index при успехе)
     */
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User userForm,
                               BindingResult bindingResult,
                               Model model) {
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

    /**
     * Функция отображения страницы логина
     * @param model model
     * @param error ошибки валидации
     * @param logout ??
     * @return наименование view (login)
     */
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout){
        logger.debug(LogUtil.getMethodName());

        if (error != null){
            model.addAttribute("error", messageSource.getMessage("label.login.error", null, new Locale("RU")));
        }
        if (logout != null) {
            return "index";
        }

        return "/login/login";
    }
}
