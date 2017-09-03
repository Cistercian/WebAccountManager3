package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.MailService;
import ru.hd.olaf.mvc.service.UserService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

/**
 * Created by d.v.hozyashev on 24.05.2017.
 */
@Controller
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    /**
     * Функция прорисовка окна администратора (расширенное окно профиля)
     *
     * @return ModelAndView admin-panel
     */
    @RequestMapping(value = "/admin-panel", method = RequestMethod.GET)
    public ModelAndView viewAdminPanel() {
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/login/admin-panel");

        modelAndView.addObject("mail", mailService.getAll());

        modelAndView.addObject("users", userService.getAll());

        return modelAndView;
    }

    /**
     * Функция администратора "Пересчет лимитов" для генерации почтовых уведомлений. Предназначена для первичной
     * обработки после появления функционала лимитов. Сейчас неактуальна
     *
     * @return JsonResponse с результатом
     */
    @RequestMapping(value = "/admin-panel/refreshLimits", method = RequestMethod.GET)
    public
    @ResponseBody
    JsonResponse refreshLimits() {
        logger.debug(LogUtil.getMethodName());

        JsonResponse response = new JsonResponse();

        for (User user : userService.getAll()) {
            logger.debug(String.format("Пересчет лимитов пользователя %s", user.getUsername()));
            JsonResponse responseCheck = mailService.checkAllLimits(user);
            response.setMessage(response.getMessage() + " " + responseCheck.getMessage());
        }

        response.setType(ResponseType.SUCCESS);

        return response;
    }

    /**
     * наглая функция для ресолва картинки на github'e
     *
     */
    @RequestMapping(value = "/db_structure.png", method = RequestMethod.GET)
    public String getDbStructure() {
        logger.debug(LogUtil.getMethodName());

        return "forward:resources/img/xml_parser/db.png";
    }
    @RequestMapping(value = "/schema_account_db.png", method = RequestMethod.GET)
    public String getAccountDbStructure() {
        logger.debug(LogUtil.getMethodName());

        return "forward:resources/img/xml_parser/schema_account_db.png";
    }

    @RequestMapping(value = "/TextSearch_schema.png", method = RequestMethod.GET)
    public String getTextSearchSchema() {
        logger.debug(LogUtil.getMethodName());

        return "forward:resources/img/xml_parser/TextSearch_schema.png";
    }
}
