package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Category;
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
    public ModelAndView getViewCompare(){
        logger.debug(LogUtil.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/statistic/analytic");

        User currentUser = securityService.findLoggedUser();
        //дата начала текущего месяца
        LocalDate before = DateUtil.getStartOfMonth().minusDays(1);
        //дата начала периода "за все время"
        LocalDate after = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());

        List<BarEntity> analyticData = categoryService.getAnalyticData(
                currentUser,
                null,
                after,
                before);

        logger.debug("Список категорий для передачи на страницу Прогнозирования");
        LogUtil.logList(logger, analyticData);

        modelAndView.addObject("analyticData", analyticData);
        modelAndView.addObject("after", after);
        modelAndView.addObject("before", before);

        return modelAndView;
    }
}
