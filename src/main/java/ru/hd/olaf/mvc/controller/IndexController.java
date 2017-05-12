package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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
    @Autowired
    private SecurityService securityService;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);


    @RequestMapping(value = "favicon.ico", method = RequestMethod.GET)
    public String getFavicon(){
        return "forward:resources/img/favicon.ico";
    }

    /**
     * Функция отрисовки index.html
     *
     * @return
     */
    @RequestMapping(value = {"", "/", "/index"}, method = RequestMethod.GET)
    public ModelAndView getViewIndex() {
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("index");

        //текущая дата
        LocalDate curDate = LocalDate.now();
        //дата начала текущей недели
        LocalDate weekDate = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().ordinal());
        //дата начала текущего месяца
        LocalDate monthDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        //дата начала периода "за все время"
        LocalDate allTimeDate = LocalDate.of(1900,1,1);

        modelAndView.addObject("curDate", DateUtil.getFormattedDate(curDate));
        modelAndView.addObject("afterWeek", DateUtil.getFormattedDate(weekDate));
        modelAndView.addObject("afterMonth", DateUtil.getFormattedDate(monthDate));
        modelAndView.addObject("afterAllTime", DateUtil.getFormattedDate(allTimeDate));

        List<BarEntity> parentsCategories = new ArrayList<BarEntity>();

        parentsCategories.addAll(getCategoriesByDate(DateUtil.getFormattedDate(monthDate),
                DateUtil.getFormattedDate(curDate)));

        logger.debug(String.format("data for injecting:"));
        LogUtil.logList(logger, parentsCategories);

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

        logger.debug(String.format("Data for injecting: sumIncome: %s, sumExpense: %s, curDate: %s",
                sumIncome.toString(), sumExpense.toString(), LocalDate.now()));

        return modelAndView;
    }

    /**
     * Функция возврата json данных для прорисовки прогресс баров на главной странице по корневым категориям доход/расход
     * с сортировкой по типу (CategoryIncome\CategoryExpense) и сумме по убыванию
     * @param beginDate начальная дата отсечки
     * @param endDate конечная дата отсечки
     * @return List<BarEntity>
     */
    @RequestMapping(value = "getCategoriesByDate", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BarEntity> getCategoriesByDate(@RequestParam(value = "after") String beginDate,
                                        @RequestParam(value = "before") String endDate) {
        logger.debug(LogUtil.getMethodName());

        LocalDate after = DateUtil.getParsedDate(beginDate);
        LocalDate before = DateUtil.getParsedDate(endDate);

        List<BarEntity> parentsCategories = categoryService.getBarEntityOfSubCategories(null,
                after.minusDays(1),
                before.plusDays(1));

        Collections.sort(parentsCategories, new Comparator<BarEntity>() {
            public int compare(BarEntity o1, BarEntity o2) {
                int result = o1.getType().compareTo(o2.getType());
                if (result == 0)
                    result = o2.getSum().compareTo(o1.getSum());

                return result;
            }
        });

        logger.debug(String.format("data for injecting:"));
        LogUtil.logList(logger, parentsCategories);

        return parentsCategories;
    }


    @RequestMapping(value = "/getContentByCategoryId", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BarEntity> getCategoryContentByCategoryId(@RequestParam(value = "categoryId") Integer categoryId,
                                                   @RequestParam(value = "after") String beginDate,
                                                   @RequestParam(value = "before") String endDate) {
        logger.debug(LogUtil.getMethodName());

        List<BarEntity> categoryContent = new ArrayList<BarEntity>();

        Category category = null;
        JsonResponse response = categoryService.getById(categoryId);
        if (response.getType() == ResponseType.SUCCESS)
            category = (Category) response.getEntity();
        else {
            logger.debug("Возникла ошибка: " + response.getMessage());
            return null;
        }

        LocalDate after = DateUtil.getParsedDate(beginDate);
        LocalDate before = DateUtil.getParsedDate(endDate);

        //данные по дочерним категориям
        categoryContent.addAll(categoryService.getBarEntityOfSubCategories(category, after.minusDays(1),
                before.plusDays(1)));
        //данные по товарным группам(amounts с группировкой по product)
        categoryContent.addAll(amountService.getBarEntitiesByCategory(category, after.minusDays(1),
                before.plusDays(1)));

        //сортировка
        Collections.sort(categoryContent, new Comparator<BarEntity>() {
            public int compare(BarEntity o1, BarEntity o2) {
                return o2.getSum().compareTo(o1.getSum());
            }
        });

        logger.debug(String.format("data for injecting:"));
        LogUtil.logList(logger, categoryContent);

        return categoryContent;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        format.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}