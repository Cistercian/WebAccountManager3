package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ReportService;
import ru.hd.olaf.util.DataPeriod;
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
    @Autowired
    private ReportService reportService;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(value = {"", "/", "/index"}, method = RequestMethod.GET)
    public ModelAndView getViewIndex(){
        logger.debug(String.format("Function %s", "getViewIndex()"));
        ModelAndView modelAndView = new ModelAndView("index");

        //TODO: refactoring to BarEntity?
        LocalDate today = LocalDate.now();
        LocalDate after = LocalDate.of(today.getYear(), today.now().getMonth(), 1);
        Map<Category, BigDecimal> categories = categoryService.getParentsWithTotalSum(after, today.plusDays(1));

        modelAndView.addObject("categories", categories);

        //get total sum income and expense
        BigDecimal sumIncome = new BigDecimal("0");
        BigDecimal maxIncome = new BigDecimal("0");

        BigDecimal sumExpense = new BigDecimal("0");
        BigDecimal maxExpense = new BigDecimal("0");

        for (Map.Entry<Category, BigDecimal> entry : categories.entrySet()){
            //if current category is income entry
            if (entry.getKey().getType() == 0) {
                sumIncome = sumIncome.add(entry.getValue());
                maxIncome = maxIncome.compareTo(entry.getValue()) > 0 ? maxIncome : entry.getValue();
            } else {
                sumExpense = sumExpense.add(entry.getValue());
                maxExpense = maxExpense.compareTo(entry.getValue()) > 0 ? maxExpense : entry.getValue();
            }
        }

        modelAndView.addObject("sumIncome", sumIncome);
        modelAndView.addObject("sumExpense", sumExpense);
        modelAndView.addObject("maxIncome", maxIncome);
        modelAndView.addObject("maxExpense", maxExpense);

        //Calendar today = Calendar.getInstance();
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        modelAndView.addObject("curDate", today);

        logger.debug(String.format("Data for injecting: sumIncome: %s, sumExpense: %s, curDate: %s",
                sumIncome.toString(), sumExpense.toString(), today));

        return modelAndView;
    }

    @RequestMapping(value = "getParentsWithTotalSum", method = RequestMethod.GET)
    public @ResponseBody List<BarEntity> getParentsWithTotalSum(@RequestParam(value = "period") String period){
        logger.debug(String.format("Function %s", "getParentsWithTotalSum()"));

        LocalDate today = LocalDate.now();

        LocalDate after = getAfterDate(period, today);


        List<BarEntity> barEntities = categoryService.getCategoriesSum(null, after.minusDays(1), today.plusDays(1));

        Collections.sort(barEntities, new Comparator<BarEntity>() {
            public int compare(BarEntity o1, BarEntity o2) {
                int result = o1.getType().compareTo(o2.getType());
                if (result !=0)
                    return (int) result/Math.abs(result);

                result = o2.getSum().compareTo(o2.getSum());
                return (result != 0) ? (int) result/Math.abs(result) : 0;
            }
        });

        return barEntities;
    }

    private LocalDate getAfterDate(@RequestParam(value = "period") String period, LocalDate today) {
        DataPeriod dataPeriod = null;
        try {
            dataPeriod = DataPeriod.valueOf(period.toUpperCase());
            logger.debug(String.format("Period: %s", dataPeriod));
        } catch (IllegalArgumentException e) {
            dataPeriod = DataPeriod.MONTH;
            logger.debug(String.format("Cant parsed param Period. Request: %s, using default value: %s", period, dataPeriod));
        }

        LocalDate after = null;
        switch (dataPeriod){
            case DAY:
                after = today;
                break;
            case WEEK:
                after = today.minusDays(today.getDayOfWeek().ordinal() + 1);
                break;
            case ALL:
                after = LocalDate.MIN;
                break;
            default:
                after = today.minusDays(today.getDayOfMonth());
                break;
        }
        return after;
    }

    //TODO: depricate?
    @RequestMapping(value = "/getAmountsByCategoryId", params = {"categoryId"}, method = RequestMethod.GET)
    public
    @ResponseBody
    List<Amount> getAmountsByCategoryId(@RequestParam(value = "categoryId") Integer categoryId) {
        logger.debug(String.format("Function %s", "getAmountsByCategoryId"));
        Category category = categoryService.getById(categoryId);

        List<Amount> amounts = amountService.getByCategory(category);
        printData(amounts);

        return amounts;
    }

    @RequestMapping(value = "/getContentByCategoryId", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BarEntity> getCategoryContentByCategoryId(@RequestParam(value = "categoryId") Integer categoryId,
                                                   @RequestParam(value = "period") String period){
        logger.debug(String.format("Function %s, id: %d", "getCategoryContentByCategoryId", categoryId));

        LocalDate today = LocalDate.now();
        LocalDate after = getAfterDate(period, today);

        return reportService.getCategoryContentById(categoryId, after.minusDays(1), today.plusDays(1));
    }

    private <T> void printData(List<T> list) {
        for (T T : list) {
            System.out.println(T);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}