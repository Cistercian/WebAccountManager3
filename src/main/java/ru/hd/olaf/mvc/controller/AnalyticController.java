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
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.BarEntity;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * Created by d.v.hozyashev on 07.06.2017.
 */
@Controller
public class AnalyticController {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private CategoryService categoryService;

    private static final Logger logger = LoggerFactory.getLogger(AnalyticController.class);

    @RequestMapping(value = "/statistic/analytic", method = RequestMethod.GET)
    public ModelAndView getViewCompare(@RequestParam(value = "after", required = false) String beginDate,
                                       @RequestParam(value = "averagingPeriod", required = false) Byte averagingPeriod){
        logger.debug(LogUtil.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/statistic/analytic");

        List<BarEntity> analyticData = getData(beginDate, null, averagingPeriod);

        logger.debug("Список категорий для передачи на страницу Прогнозирования");
        LogUtil.logList(logger, analyticData);

        modelAndView.addObject("analyticData", analyticData);
        modelAndView.addObject("after", getAfter(beginDate));
        modelAndView.addObject("before", getBefore(null));

        return modelAndView;
    }

    @RequestMapping(value = "/statistic/analytic/getData", method = RequestMethod.GET)
    public @ResponseBody List<BarEntity> getData(@RequestParam(value = "after", required = false) String beginDate,
                                                 @RequestParam(value = "before", required = false) String endDate,
                                                 @RequestParam(value = "averagingPeriod", required = false) Byte averagingPeriod) {
        logger.debug(LogUtil.getMethodName());

        LocalDate after = getAfter(beginDate);
        LocalDate before = getBefore(endDate);
        if (averagingPeriod == null) averagingPeriod = 1; //defaults = month

        User currentUser = securityService.findLoggedUser();


        List<BarEntity> analyticData = categoryService.getAnalyticData(
                currentUser,
                null,
                after,
                before,
                averagingPeriod);

        return analyticData;
    }

    private LocalDate getAfter(String date){
        if (date != null && !"".equals(date))
            return DateUtil.getParsedDate(date);
        else
            return LocalDate.now().minusMonths(2).with(TemporalAdjusters.firstDayOfMonth());
    }

    private LocalDate getBefore(String date){
        if (date != null && !"".equals(date))
            return DateUtil.getParsedDate(date);
        else
            return DateUtil.getStartOfMonth().minusDays(1);
    }

}
