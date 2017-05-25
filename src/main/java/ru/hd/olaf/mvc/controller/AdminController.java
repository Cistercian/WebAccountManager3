package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Mail;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.MailService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.mvc.service.UserService;
import ru.hd.olaf.mvc.service.UtilService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

/**
 * Created by d.v.hozyashev on 24.05.2017.
 */
@Controller
public class AdminController {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserService userService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @RequestMapping(value = "/admin-panel", method = RequestMethod.GET)
    public ModelAndView viewAdminPanel(){
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/login/admin-panel");

        modelAndView.addObject("mail", mailService.getAll());

        modelAndView.addObject("users", userService.getAll());

        return modelAndView;
    }

    @RequestMapping(value = "/admin-panel/refreshLimits", method = RequestMethod.GET)
    public @ResponseBody
    JsonResponse refreshLimits(){
        logger.debug(LogUtil.getMethodName());

        JsonResponse response = new JsonResponse();

        for (User user : userService.getAll()) {
            logger.debug(String.format("Пересчет лимитов пользователя %s", user.getUsername()));
            JsonResponse responseCheck= mailService.checkAllLimits(user);
            response.setMessage(response.getMessage() + " " + responseCheck.getMessage());
        }

        response.setType(ResponseType.SUCCESS);

        return response;
    }
}
