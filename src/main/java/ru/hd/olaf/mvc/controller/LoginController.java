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
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.mvc.service.UserService;
import ru.hd.olaf.mvc.validator.UserValidator;
import ru.hd.olaf.util.LogUtil;

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
}
