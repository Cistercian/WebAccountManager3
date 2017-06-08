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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.Comparator;
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

        User currentUser = securityService.findLoggedUser();


        List<BarEntity> analyticData = categoryService.getAnalyticData(
                currentUser,
                null,
                after,
                before);

        //сортируем по соотношению текущих трат к усредненным
        Collections.sort(analyticData, new Comparator<BarEntity>() {
            public int compare(BarEntity o1, BarEntity o2) {

                if (o2.getLimit().compareTo(new BigDecimal("0")) > 0 || o1.getLimit().compareTo(new BigDecimal("0")) > 0) {
                    BigDecimal o2Size = o2.getLimit().compareTo(new BigDecimal("0")) != 0 ?
                            o2.getSum().divide(o2.getLimit(), 2, BigDecimal.ROUND_HALF_UP) :
                            new BigDecimal("99999");
                    BigDecimal o1Size = o1.getLimit().compareTo(new BigDecimal("0")) != 0 ?
                            o1.getSum().divide(o1.getLimit(), 2, BigDecimal.ROUND_HALF_UP) :
                            new BigDecimal("99999");
                    return o2Size.compareTo(o1Size);
                } else
                    return o2.getSum().compareTo(o1.getSum());
            }
        });

        return analyticData;
    }

    private LocalDate getAfter(String date){
        if (date != null && !"".equals(date))
            return DateUtil.getParsedDate(date);
        else
            return DateUtil.getStartOfEra();
    }

    private LocalDate getBefore(String date){
        if (date != null && !"".equals(date))
            return DateUtil.getParsedDate(date);
        else
            return DateUtil.getStartOfMonth().minusDays(1);
    }

}
