package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.LimitService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.BarEntity;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * Created by d.v.hozyashev on 10.05.2017.
 */
@Controller
public class LimitControlController {

    @Autowired
    private LimitService limitService;
    @Autowired
    private SecurityService securityService;

    private static final Logger logger = LoggerFactory.getLogger(LimitControlController.class);

    /**
     * Функция возвращает страницу просмотра исполнения лимитов
     * @return ModelAndView(limit-control)
     */
    @RequestMapping(value = "/statistic/limit-control", method = RequestMethod.GET)
    public ModelAndView getViewLimit(){
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/statistic/limit-control");
        User currentUser = securityService.findLoggedUser();

        //вычисляем период
        //текущая дата
        LocalDate curDate = LocalDate.now();
        modelAndView.addObject("curDate", DateUtil.getFormattedDate(curDate));

        //начальная дата периода:
        //текущий день
        List<BarEntity> limits = limitService.getLimits(currentUser, (byte) 0, curDate, curDate);
        modelAndView.addObject("limitsDaily", limits);
        modelAndView.addObject("dateDaily", DateUtil.getFormattedDate(curDate));
        logger.debug("Контроль лимитов за день");
        LogUtil.logList(logger, limits);

        //текущая неделя
        limits = limitService.getLimits(currentUser, (byte) 1, DateUtil.getStartOfWeek(), curDate);
        modelAndView.addObject("limitsWeekly", limits);
        modelAndView.addObject("dateWeekly", DateUtil.getFormattedDate(DateUtil.getStartOfWeek()));
        logger.debug("Контроль лимитов за неделю");
        LogUtil.logList(logger, limits);

        //текущий месяц
        limits = limitService.getLimits(currentUser, (byte) 2, DateUtil.getStartOfMonth(), curDate);
        modelAndView.addObject("limitsMonthly", limits);
        modelAndView.addObject("dateMonthly", DateUtil.getFormattedDate(DateUtil.getStartOfMonth()));
        logger.debug("Контроль лимитов за месяц");
        LogUtil.logList(logger, limits);

        return modelAndView;
    }
}
