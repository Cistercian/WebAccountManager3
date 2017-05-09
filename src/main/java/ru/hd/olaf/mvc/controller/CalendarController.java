package ru.hd.olaf.mvc.controller;

import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.ParseUtil;
import ru.hd.olaf.util.json.CalendarEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by Olaf on 30.04.2017.
 */
@Controller
public class CalendarController {

    @Autowired
    private AmountService amountService;
    @Autowired
    private SecurityService securityService;

    private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);

    @RequestMapping(value = "/statistic/calendar", method = RequestMethod.GET)
    public ModelAndView getViewCalendar(){
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/statistic/calendar");

        //TODO: redudant?
        //modelAndView.addObject("calendarData", getCalendarData());

        return modelAndView;
    }

    @RequestMapping(value = "/statistic/calendar/getCalendarData", method = RequestMethod.GET)
    public @ResponseBody List<CalendarEntity> getCalendarData(@RequestParam (value = "start") String startDate,
                                                              @RequestParam (value = "end") String endDate) {
        logger.debug(LogUtil.getMethodName() + String.format("Интервал: %s - %s", startDate, endDate));

        List<CalendarEntity> calendarEntities = new ArrayList<CalendarEntity>();

        User user = securityService.findLoggedUser();

        LocalDate after = ParseUtil.getParsedDate(startDate);
        LocalDate before = ParseUtil.getParsedDate(endDate);

        long countOfDays = after.until(before, ChronoUnit.DAYS);

        while (after.compareTo(before) <= 0) {

            List<Amount> amounts = amountService.getByDate(user, after, after);
            //в форме просмотра "за месяц" не отображаем даты с нулевой суммой
            if (amounts.size() > 0 || countOfDays < 8) {

                BigDecimal sum = new BigDecimal("0");

                for (Amount amount : amounts) {
                    sum = amount.getCategoryId().getType() == 0 ?
                            sum.add(amount.getPrice()) :
                            sum.subtract(amount.getPrice());
                }

                CalendarEntity calendarEntity = new CalendarEntity();
                calendarEntity.setTitle(sum.toString());
                calendarEntity.setDate(after.toString());

                calendarEntities.add(calendarEntity);

            }
            after = after.plusDays(1);
        }

        return calendarEntities;
    }

    /**
     * Функция возвращает ModelAndView с таблицей amount за заданную дату
     * @param startDate дата начала (строка)
     * @param endDate дата конца периода (строка)
     * @return ModelAndView /data/page-product
     */
    @RequestMapping(value="/statistic/calendar/getAmountsByDate", method = RequestMethod.GET)
    public ModelAndView getAmountsByDate(@RequestParam (value = "start") String startDate,
                                         @RequestParam (value = "end") String endDate) {
        logger.debug(LogUtil.getMethodName() + String.format("Интервал: %s - %s", startDate, endDate));

        ModelAndView modelAndView = new ModelAndView("/data/page-product");
        User user = securityService.findLoggedUser();
        LocalDate after = ParseUtil.getParsedDate(startDate);
        LocalDate before = ParseUtil.getParsedDate(endDate);

        List<Amount> amounts = amountService.getByDate(user, after, before);

        Collections.sort(amounts, new Comparator<Amount>() {
            public int compare(Amount o1, Amount o2) {
                return o2.getPrice().compareTo(o1.getPrice());
            }
        });

        modelAndView.addObject("amounts", amounts);

        return modelAndView;
    }
}
