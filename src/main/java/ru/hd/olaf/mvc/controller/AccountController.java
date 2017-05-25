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
import ru.hd.olaf.util.json.ResponseType;

import java.util.List;

/**
 * Created by d.v.hozyashev on 25.05.2017.
 */
@Controller
public class AccountController {

    @Autowired
    private UserService userService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserValidator userValidator;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @RequestMapping(value = "/account/sendMail", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse sendMail(@RequestParam(value = "username", required = false) String username,
                          @RequestParam(value = "title") String title,
                          @RequestParam(value = "text") String text){
        logger.debug(LogUtil.getMethodName());

        if (title == null || text == null || "".equals(title) || "".equals(text))
            return new JsonResponse(ResponseType.ERROR, "Переданы некоррекные параметры.");

        JsonResponse response = new JsonResponse();
        User currentUser = securityService.findLoggedUser();
        if ("ROLE_ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            if (username.equalsIgnoreCase("ALL")) {
                for (User user : userService.getAll()) {
                    Mail mail = new Mail("Admin", title, text, user);
                    JsonResponse response1 = utilService.saveEntity(mail);
                    response.setMessage(response.getMessage() + "<p>User: " + user.getUsername() +
                            ", result: " +
                            response1.getMessage() + "</p>");
                }
            } else {
                User user = userService.findByUsername(username);
                if (user != null) {
                    Mail mail = new Mail("Admin", title, text, user);
                    response = utilService.saveEntity(mail);
                } else
                    response.setMessage("Пользователь не найден");
            }
        } else {
            for (User admin : userService.getAdmins()) {
                Mail mail = new Mail(currentUser.getUsername(), title, text, admin);
                JsonResponse response1 = utilService.saveEntity(mail);
            }
            response.setMessage("Сообщение отправлено");
        }

        response.setType(ResponseType.INFO);
        return response;
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

        List<Mail> list = mailService.getAll();
        model.addAttribute("mail", list);

        return "login/account";
    }

    @RequestMapping(value = "/account/getMail", method = RequestMethod.GET)
    public ModelAndView getMail(@RequestParam(value = "id") Integer id){
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/login/mail/mail");

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

    @RequestMapping(value = "/account/getMailForm", method = RequestMethod.GET)
    public ModelAndView viewMailForm(){
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/login/mail/sendMailForm");

        return modelAndView;
    }
}
