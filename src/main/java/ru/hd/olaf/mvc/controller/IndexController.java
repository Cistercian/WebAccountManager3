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
import ru.hd.olaf.util.DatePeriod;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.ParseUtil;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static ru.hd.olaf.util.DatePeriod.getAfterDate;

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

    /**
     * Функция отрисовки index.html
     *
     * @return
     */
    @RequestMapping(value = {"", "/", "/index"}, method = RequestMethod.GET)
    public ModelAndView getViewIndex() {
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("index");

        List<BarEntity> parentsCategories = new ArrayList<BarEntity>();

        parentsCategories.addAll(getCategoriesByDate(DatePeriod.MONTH.toString(), null));

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

        //текущая дата
        modelAndView.addObject("curDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        //предуставки даты для выбора отчетного периода
        //дата начала текущей недели
        LocalDate afterDate = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().ordinal());
        modelAndView.addObject("afterWeek", afterDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        //дата начала текущего месяца
        afterDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        modelAndView.addObject("afterMonth", afterDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        //дата начала периода "за все время"
        afterDate = LocalDate.of(1900,1,1);
        modelAndView.addObject("afterAllTime", afterDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        logger.debug(String.format("Data for injecting: sumIncome: %s, sumExpense: %s, curDate: %s",
                sumIncome.toString(), sumExpense.toString(), LocalDate.now()));

        return modelAndView;
    }

//    /**
//     * Функция возврата json данных для прорисовки прогресс баров на главной странице по корневым категориям доход/расход
//     * с сортировкой по типу (CategoryIncome\CategoryExpense) и сумме по убыванию
//     *
//     * @param period - период, по которому выводим данные (see DatePeriod)
//     * @return
//     */
//    //TODO: redudant
//    @RequestMapping(value = "getParentsCategories", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    List<BarEntity> getParentsCategories(@RequestParam(value = "period") String period,
//                                         @RequestParam(value = "countDays", required = false) Integer countDays) {
//        logger.debug(LogUtil.getMethodName());
//
//        LocalDate today = LocalDate.now();
//        LocalDate after = getAfterDate(period, today, countDays);
//
//        List<BarEntity> parentsCategories = categoryService.getBarEntityOfSubCategories(null,
//                after.minusDays(1),
//                today.plusDays(1));
//
//        Collections.sort(parentsCategories, new Comparator<BarEntity>() {
//            public int compare(BarEntity o1, BarEntity o2) {
//                int result = o1.getType().compareTo(o2.getType());
//                if (result == 0)
//                    result = o2.getSum().compareTo(o1.getSum());
//
//                return result;
//            }
//        });
//
//        logger.debug(String.format("data for injecting:"));
//        logList(parentsCategories);
//
//        return parentsCategories;
//    }

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

        LocalDate after = ParseUtil.getParsedDate(beginDate);
        LocalDate before = ParseUtil.getParsedDate(endDate);

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
        logList(parentsCategories);

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

        LocalDate after = ParseUtil.getParsedDate(beginDate);
        LocalDate before = ParseUtil.getParsedDate(endDate);

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
        logList(categoryContent);

        return categoryContent;
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