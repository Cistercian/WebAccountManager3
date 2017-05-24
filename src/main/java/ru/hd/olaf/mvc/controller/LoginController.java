package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Mail;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.MailService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.mvc.service.UserService;
import ru.hd.olaf.mvc.service.UtilService;
import ru.hd.olaf.mvc.validator.UserValidator;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private MailService mailService;
    @Autowired
    private UtilService utilService;

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

        //TODO: link to message.properties
        if (error != null){
            model.addAttribute("error", "Your username and password is invalid.");
        }
        if (logout != null) {
            model.addAttribute("logout", "You have been logged out successfully.");
        }

        return "/login/login";
    }

    /**
     * Функция отрисовки окна профиля (меня пароля)
     * @param model model
     * @return наименование view (account)
     */
    @RequestMapping(value = "account", method = RequestMethod.GET)
    public String getViewAccount(Model model){
        logger.debug(LogUtil.getMethodName());

        model.addAttribute("passwordForm", new User());

        List<Mail> list = mailService.getAll();

        Collections.sort(list, new Comparator<Mail>() {
            public int compare(Mail o1, Mail o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });

        model.addAttribute("mail", list);

        return "login/account";
    }

    /**
     * Функция смены пароля
     * @param userForm html форма с атрибутами User
     * @param bindingResult для валидации
     * @param model model
     * @return наименование view (account)
     */
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

    @RequestMapping(value = "/account/getMail", method = RequestMethod.GET)
    public ModelAndView getMail(@RequestParam(value = "id") Integer id){
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/login/mail");

        JsonResponse response = utilService.getById(Mail.class, id);
        if (response.getEntity() != null){
            Mail mail = (Mail) response.getEntity();
            logger.debug(String.format("Обрабатываемый объект: %s", mail));

            modelAndView.addObject("mail", mail);

            mail.setIsRead((byte)1);
            response = utilService.saveEntity(mail);

            logger.debug(String.format("Уведомление прочитано. Результат сохранения: %s", response.getMessage()));
        }

        return modelAndView;
    }
}
