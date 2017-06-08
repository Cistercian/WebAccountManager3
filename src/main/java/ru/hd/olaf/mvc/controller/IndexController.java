package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.*;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.LogUtil;
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
    @Autowired
    private MailService mailService;
    @Autowired
    private UtilService utilService;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    /**
     * Функция возврата favicon.ico (иконка сайта для умных браузеров...)
     *
     * @return ico-файл
     */
    @RequestMapping(value = "favicon.ico", method = RequestMethod.GET)
    public String getFavicon() {
        return "forward:resources/img/favicon.ico";
    }

    /**
     * Функция отрисовки index.html
     *
     * @return ModelAndView index
     */
    @RequestMapping(value = {"", "/", "/index"}, method = RequestMethod.GET)
    public ModelAndView getViewIndex() {
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("index");

        //текущая дата
        LocalDate curDate = LocalDate.now();
        //дата начала текущей недели
        LocalDate weekDate = DateUtil.getStartOfWeek();
        //дата начала текущего месяца
        LocalDate monthDate = DateUtil.getStartOfMonth();
        //дата начала периода "за все время"
        LocalDate allTimeDate = DateUtil.getStartOfEra();

        modelAndView.addObject("curDate", DateUtil.getFormattedDate(curDate));
        modelAndView.addObject("afterWeek", DateUtil.getFormattedDate(weekDate));
        modelAndView.addObject("afterMonth", DateUtil.getFormattedDate(monthDate));
        modelAndView.addObject("afterAllTime", DateUtil.getFormattedDate(allTimeDate));

        List<BarEntity> parentsCategories = getCategoriesByDate(DateUtil.getFormattedDate(monthDate),
                DateUtil.getFormattedDate(curDate));

        logger.debug("Список категорий:");
        LogUtil.logList(logger, parentsCategories);

        modelAndView.addObject("categories", parentsCategories);

        //Получение суммарных данных
        User currentUser = securityService.findLoggedUser();
        BigDecimal sumIncome = amountService.getSumByCategoryType((byte) 0,
                currentUser,
                monthDate,
                curDate);

        BigDecimal sumExpense = amountService.getSumByCategoryType((byte) 1,
                currentUser,
                monthDate,
                curDate);

        modelAndView.addObject("sumIncome", sumIncome);
        modelAndView.addObject("sumExpense", sumExpense);

        //TODO: jpa query, devidebyzero?
        BigDecimal maxIncome = new BigDecimal("0");
        BigDecimal maxExpense = new BigDecimal("0");

        for (BarEntity barEntity : parentsCategories) {
            if ("CategoryIncome".equalsIgnoreCase(barEntity.getType())) {
                maxIncome = maxIncome.compareTo(barEntity.getSum()) > 0 ? maxIncome : barEntity.getSum();
            } else {
                maxExpense = maxExpense.abs().compareTo(barEntity.getSum().abs()) > 0 ? maxExpense : barEntity.getSum();
            }
        }

        modelAndView.addObject("maxIncome", maxIncome);
        modelAndView.addObject("maxExpense", maxExpense);

        logger.debug(String.format("Data for injecting: sumIncome: %s, sumExpense: %s, curDate: %s",
                sumIncome.toString(), sumExpense.toString(), LocalDate.now()));

        return modelAndView;
    }

    /**
     * Функция возврата json данных для прорисовки прогресс баров на главной странице по корневым категориям доход/расход
     * с сортировкой по типу (CategoryIncome\CategoryExpense) и сумме по убыванию
     *
     * @param beginDate начальная дата отсечки
     * @param endDate   конечная дата отсечки
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
        User currentUser = securityService.findLoggedUser();

        List<BarEntity> parentsCategories = categoryService.getBarEntityOfSubCategories(currentUser,
                null,
                after,
                before,
                false);

        //сортировка по типу категории (доход/расход) и по сумме
        parentsCategories = utilService.sortListByTypeAndSum(parentsCategories);

        logger.debug("Список категорий:");
        LogUtil.logList(logger, parentsCategories);

        return parentsCategories;
    }


    @RequestMapping(value = "/getContentByCategoryId", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BarEntity> getCategoryContentByCategoryId(@RequestParam(value = "categoryId") Integer categoryId,
                                                   @RequestParam(value = "after") String beginDate,
                                                   @RequestParam(value = "before") String endDate,
                                                   @RequestParam(value = "isGetAnalyticData") boolean isGetAnalyticData,
                                                   @RequestParam(value = "averagingPeriod", required = false) Byte averagingPeriod) {
        logger.debug(LogUtil.getMethodName());
        logger.debug(String.format("Выводятся ли среднемесячные данные: %s", isGetAnalyticData));

        List<BarEntity> categoryContent = new ArrayList<BarEntity>();

        Category category;
        JsonResponse response = categoryService.getById(categoryId);
        if (response.getType() == ResponseType.SUCCESS)
            category = (Category) response.getEntity();
        else {
            logger.debug("Возникла ошибка: " + response.getMessage());
            return null;
        }

        LocalDate after = DateUtil.getParsedDate(beginDate);
        LocalDate before = DateUtil.getParsedDate(endDate);
        User currentUser = securityService.findLoggedUser();

        //данные по дочерним категориям
        categoryContent.addAll(categoryService.getBarEntityOfSubCategories(currentUser,
                category,
                after,
                before,
                isGetAnalyticData));
        //данные по товарным группам(amounts с группировкой по product)
        categoryContent.addAll(amountService.getBarEntitiesByCategory(currentUser,
                category,
                after,
                before,
                isGetAnalyticData));

        if (isGetAnalyticData) {
            categoryContent = utilService.calcAvgSum(categoryContent, after, before, averagingPeriod);
        }

        //сортировка
        Collections.sort(categoryContent, new Comparator<BarEntity>() {
            public int compare(BarEntity o1, BarEntity o2) {
                return o2.getSum().compareTo(o1.getSum());
            }
        });

        logger.debug("Список категорий:");
        LogUtil.logList(logger, categoryContent);

        return categoryContent;
    }

    @RequestMapping(value = "/index/getAlerts", method = RequestMethod.GET)
    public @ResponseBody List<String> getAlerts(){
        logger.debug(LogUtil.getMethodName());

        User currentUser = securityService.findLoggedUser();

        return mailService.getUnreadTitle(currentUser);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        format.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}