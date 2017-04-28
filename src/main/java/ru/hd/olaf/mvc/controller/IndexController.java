package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.util.DatePeriod;
import ru.hd.olaf.util.json.BarEntity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Olaf on 11.04.2017.
 */
@org.springframework.stereotype.Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AmountService amountService;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    /**
     * Функция отрисовки index.html
     * @return
     */
    @RequestMapping(value = {"", "/", "/index"}, method = RequestMethod.GET)
    public ModelAndView getViewIndex() {
        logger.debug(String.format("Function %s", "getViewIndex()"));

        ModelAndView modelAndView = new ModelAndView("index");

        List<BarEntity> parentsCategories = new ArrayList<BarEntity>();

        parentsCategories.addAll(getParentsCategories(DatePeriod.MONTH.toString()));

        logger.debug(String.format("data for injecting:"));
        logList(parentsCategories);

        modelAndView.addObject("categories", parentsCategories);

        //Получение суммарных данных
        BigDecimal sumIncome = new BigDecimal("0");
        BigDecimal maxIncome = new BigDecimal("0");

        BigDecimal sumExpense = new BigDecimal("0");
        BigDecimal maxExpense = new BigDecimal("0");

        for (BarEntity barEntity : parentsCategories) {
            if ("CategoryIncome".equalsIgnoreCase(barEntity.getType())) {
                sumIncome = sumIncome.add(barEntity.getSum());
                maxIncome = maxIncome.compareTo(barEntity.getSum()) > 0 ? maxIncome : barEntity.getSum();
            } else {
                sumExpense = sumExpense.add(barEntity.getSum());
                maxExpense = maxExpense.compareTo(barEntity.getSum()) > 0 ? maxExpense : barEntity.getSum();
            }
        }

        modelAndView.addObject("sumIncome", sumIncome);
        modelAndView.addObject("sumExpense", sumExpense);
        modelAndView.addObject("maxIncome", maxIncome);
        modelAndView.addObject("maxExpense", maxExpense);

        modelAndView.addObject("curDate", LocalDate.now());

        logger.debug(String.format("Data for injecting: sumIncome: %s, sumExpense: %s, curDate: %s",
                sumIncome.toString(), sumExpense.toString(), LocalDate.now()));

        return modelAndView;
    }

    /**
     * Функция возврата json данных для прорисовки прогресс баров на главной странице по корневым категориям доход/расход
     * с сортировкой по типу (CategoryIncome\CategoryExpense) и сумме по убыванию
     * @param period - период, по которому выводим данные (see DatePeriod)
     * @return
     */
    @RequestMapping(value = "getParentsCategories", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BarEntity> getParentsCategories(@RequestParam(value = "period") String period) {
        logger.debug(String.format("Function %s", "getParentsCategories()"));

        LocalDate today = LocalDate.now();
        LocalDate after = getAfterDate(period, today);

        List<BarEntity> parentsCategories = categoryService.getBarEntityOfSubCategories(null, after.minusDays(1), today.plusDays(1));

        Collections.sort(parentsCategories, new Comparator<BarEntity>() {
            public int compare(BarEntity o1, BarEntity o2) {
                int result = o1.getType().compareTo(o2.getType());
                if (result == 0)
                    result = o2.getSum().compareTo(o1.getSum());

                return result;
            }
        });

        logger.debug(String.format("data for injecting:"));
        logList(parentsCategories);

        return parentsCategories;
    }


    @RequestMapping(value = "/getContentByCategoryId", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BarEntity> getCategoryContentByCategoryId(@RequestParam(value = "categoryId") Integer categoryId,
                                                   @RequestParam(value = "period") String period) {
        logger.debug(String.format("Function %s, id: %d", "getCategoryContentByCategoryId", categoryId));

        List<BarEntity> categoryContent = new ArrayList<BarEntity>();

        Category category = null;
        try {
            category = categoryService.getOne(categoryId);
        } catch (AuthException e) {
            //TODO: modal?
            //e.printStackTrace();
        }

        LocalDate today = LocalDate.now();
        LocalDate after = getAfterDate(period, today);

        //данные по дочерним категориям
        categoryContent.addAll(categoryService.getBarEntityOfSubCategories(category, after.minusDays(1),
                today.plusDays(1)));
        //данные по товарным группам(amounts с группировкой по product)
        categoryContent.addAll(amountService.getBarEntitiesByCategory(category, after.minusDays(1),
                today.plusDays(1)));

        //сортировка
        Collections.sort(categoryContent, new Comparator<BarEntity>() {
            public int compare(BarEntity o1, BarEntity o2) {
                return o2.getSum().compareTo(o1.getSum());
            }
        });

        logger.debug(String.format("data for injecting:"));
        logList(categoryContent);

        return categoryContent;
    }

    /**
     * Функция парсинга переданного в запросе периода (see DatePeriod)
     *
     * @param period see DatePeriod
     * @param today LocalDate
     * @return LocalDate value
     */
    private LocalDate getAfterDate(String period, LocalDate today) {
        DatePeriod datePeriod = null;
        try {
            datePeriod = DatePeriod.valueOf(period.toUpperCase());
            logger.debug(String.format("Period: %s", datePeriod));
        } catch (IllegalArgumentException e) {
            datePeriod = DatePeriod.MONTH;
            logger.debug(String.format("Cant parsed param Period. Request: %s, using default value: %s", period, datePeriod));
        }

        LocalDate after = null;
        switch (datePeriod) {
            case DAY:
                after = today;
                break;
            case WEEK:
                after = today.minusDays(today.getDayOfWeek().ordinal() + 1);
                break;
            case ALL:
                after = LocalDate.MIN;
                break;
            case MONTH:
            default:
                after = today.minusDays(today.getDayOfMonth());
                break;
        }
        return after;
    }

    private <T> void logList(List<T> list) {
        for (T T : list) {
            logger.debug(String.format("%s", T));
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}