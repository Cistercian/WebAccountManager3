package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.hd.olaf.util.LogUtil;

/**
 * Created by d.v.hozyashev on 05.06.2017.
 */
@Controller
public class AboutController {

    private static final Logger logger = LoggerFactory.getLogger(AboutController.class);

    @RequestMapping(value = "about", method = RequestMethod.GET)
    public String getViewAbout() {
        logger.debug(LogUtil.getMethodName());

        return "/help/about";
    }
}
