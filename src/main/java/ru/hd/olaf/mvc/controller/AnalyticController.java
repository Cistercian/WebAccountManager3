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
import ru.hd.olaf.util.FormatUtil;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.AnalyticEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public ModelAndView getViewCompare(@RequestParam(value = "after", required = false) String beginDate){
        logger.debug(LogUtil.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/statistic/analytic");

        List<AnalyticEntity> analyticData = getData(beginDate, null);

        logger.debug("Список категорий для передачи на страницу Прогнозирования");
        LogUtil.logList(logger, analyticData);

        modelAndView.addObject("analyticData", analyticData);
        modelAndView.addObject("after", DateUtil.getStartOfMonth());
        modelAndView.addObject("before", LocalDate.now());

        //данные для Суммарной информации
        int currentDay = LocalDate.now().getDayOfMonth();
        int countDays = DateUtil.getCountDaysInMonth(LocalDate.now());

        BigDecimal rate = new BigDecimal(countDays).divide(new BigDecimal(currentDay), 2, BigDecimal.ROUND_HALF_UP);
        modelAndView.addObject("rate", rate);

        BigDecimal incomeSum = new BigDecimal("0");
        BigDecimal incomeAvg = new BigDecimal("0");
        BigDecimal expenseSum = new BigDecimal("0");
        BigDecimal expenseAvg = new BigDecimal("0");

        for (AnalyticEntity entity : analyticData) {
            if (entity.getType().endsWith("Income")) {
                incomeSum = incomeSum.add(entity.getSum().subtract(entity.getOneTimeSum()).multiply(rate)
                    .add(entity.getOneTimeSum()).add(entity.getRegularSum()));
                incomeAvg = incomeAvg.add(entity.getAvgSum());
            } else {
                expenseSum = expenseSum.add(entity.getSum().subtract(entity.getOneTimeSum()).multiply(rate)
                        .add(entity.getOneTimeSum()).add(entity.getRegularSum()));
                expenseAvg = expenseAvg.add(entity.getAvgSum());
            }
        }

        logger.debug(String.format("Итоговые суммы: средний доход: %s, средний расход: %s, текущая сумма доходов: %s, " +
                "текущая сумма расходов: %s", incomeAvg, expenseAvg, incomeSum, expenseSum));

        modelAndView.addObject("incomeLimit", FormatUtil.numberToString(incomeAvg));
        modelAndView.addObject("expenseLimit", FormatUtil.numberToString(expenseAvg));

        modelAndView.addObject("incomeSum", FormatUtil.numberToString(incomeSum));
        modelAndView.addObject("expenseSum", FormatUtil.numberToString(expenseSum));

        modelAndView.addObject("total", FormatUtil.numberToString(incomeSum.subtract(expenseSum)));
        modelAndView.addObject("totalAvg", FormatUtil.numberToString(incomeAvg.subtract(expenseAvg)));

        logger.debug(String.format("Анализ прогнозируемых данных. Текущее число месяца: %d из %d, коэффициент одного " +
                "дня: %s прогнозируемый доход: %s, прогнозируемый расход %s", currentDay, countDays, rate,
                incomeSum, expenseSum));

        return modelAndView;
    }

    @RequestMapping(value = "/statistic/analytic/getData", method = RequestMethod.GET)
    public @ResponseBody List<AnalyticEntity> getData(@RequestParam(value = "after", required = false) String beginDate,
                                              @RequestParam(value = "before", required = false) String endDate) {
        logger.debug(LogUtil.getMethodName());

        LocalDate after = getAfter(beginDate);
        LocalDate before = getBefore(endDate);

        User currentUser = securityService.findLoggedUser();

        List<AnalyticEntity> analyticData = categoryService.getAnalyticData(
                currentUser,
                null,
                after,
                before);

        //сортируем по соотношению текущих трат к усредненным
        Collections.sort(analyticData, new Comparator<AnalyticEntity>() {
            public int compare(AnalyticEntity o1, AnalyticEntity o2) {

                if (o2.getCurrentSum().compareTo(new BigDecimal("0")) > 0 || o1.getCurrentSum().compareTo(new BigDecimal("0")) > 0) {
                    BigDecimal o2Size = o2.getAvgSum().compareTo(new BigDecimal("0")) != 0 ?
                            o2.getSum().divide(o2.getAvgSum(), 2, BigDecimal.ROUND_HALF_UP) :
                            new BigDecimal("99999");
                    BigDecimal o1Size = o1.getAvgSum().compareTo(new BigDecimal("0")) != 0 ?
                            o1.getSum().divide(o1.getAvgSum(), 2, BigDecimal.ROUND_HALF_UP) :
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
