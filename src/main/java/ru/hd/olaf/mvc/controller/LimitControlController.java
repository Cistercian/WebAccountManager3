package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.LimitService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.BarEntity;

import java.util.List;

/**
 * Created by d.v.hozyashev on 10.05.2017.
 */
@Controller
public class LimitControlController {

    @Autowired
    private LimitService limitService;
    @Autowired
    private AmountService amountService;

    private static final Logger logger = LoggerFactory.getLogger(LimitControlController.class);

    @RequestMapping(value = "/statistic/limit-control", method = RequestMethod.GET)
    public ModelAndView getViewLimit(){
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/statistic/limit-control");

        List<BarEntity> limits = limitService.getLimit((byte) 0);
        modelAndView.addObject("limitsDaily", limits);
        logger.debug("Контроль лимитов за день");
        LogUtil.logList(logger, limits);

        limits = limitService.getLimit((byte) 1);
        modelAndView.addObject("limitsWeekly", limits);
        logger.debug("Контроль лимитов за неделю");
        LogUtil.logList(logger, limits);

        limits = limitService.getLimit((byte) 2);
        modelAndView.addObject("limitsMonthly", limits);
        logger.debug("Контроль лимитов за месяц");
        LogUtil.logList(logger, limits);

        return modelAndView;
    }
}
