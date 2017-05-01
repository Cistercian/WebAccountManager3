package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.CalendarEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olaf on 30.04.2017.
 */
@Controller
public class CalendarController {

    @Autowired
    private AmountService amountService;
    @Autowired
    private SecurityService securityService;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(value = "/statistic/calendar", method = RequestMethod.GET)
    public ModelAndView getViewCalendar(){
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/statistic/calendar");

        modelAndView.addObject("calendarData", getCalendarData());

        return modelAndView;
    }

    private List<CalendarEntity> getCalendarData() {
        logger.debug(LogUtil.getMethodName());
        List<CalendarEntity> calendarEntities = new ArrayList<CalendarEntity>();

        User user = securityService.findLoggedUser();
        //первое и последнее число текущего месяца
        LocalDate date = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        while (date.compareTo(endDate) <= 0) {

            List<Amount> amounts = amountService.getByDate(user, date, date);

            if (amounts.size() > 0) {

                BigDecimal sum = new BigDecimal("0");

                for (Amount amount : amounts) {
                    sum = amount.getCategoryId().getType() == 0 ?
                            sum.add(amount.getPrice()) :
                            sum.subtract(amount.getPrice());
                }

                CalendarEntity calendarEntity = new CalendarEntity();
                calendarEntity.setTitle(sum.toString());
                calendarEntity.setDate(date.toString());

                calendarEntities.add(calendarEntity);

            }
            date = date.plusDays(1);
        }

        return calendarEntities;
    }
}
