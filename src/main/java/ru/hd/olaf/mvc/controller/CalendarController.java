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
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.*;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.FormatUtil;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.CalendarEntity;
import ru.hd.olaf.util.json.JsonResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Olaf on 30.04.2017.
 */
@Controller
public class CalendarController {

    @Autowired
    private AmountService amountService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private SecurityService securityService;

    private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);

    /**
     * Функция прорисовки окна просмотра календаря
     *
     * @return ModelAndView (calendar)
     */
    @RequestMapping(value = "/statistic/calendar", method = RequestMethod.GET)
    public ModelAndView getViewCalendar() {
        logger.debug(LogUtil.getMethodName());

        ModelAndView modelAndView = new ModelAndView("/statistic/calendar");

        modelAndView.addObject("categories", categoryService.getAll());
        modelAndView.addObject("products", productService.getAll());

        return modelAndView;
    }

    /**
     * Функция заполнения календаря FullCalendar данными
     * @param categoryID id просматриваемой категории (при пустом значении - обрабатываме "Все")
     * @param productID id просматриваемой группы товаров (при пустом значении - обрабатываме "Все")
     * @param startDate начальная дата отсечки периода
     * @param endDate   конечная дата отсечки
     * @return список CalendarData
     */
    @RequestMapping(value = "/statistic/calendar/getCalendarData", method = RequestMethod.GET)
    public
    @ResponseBody
    List<CalendarEntity> getCalendarData(@RequestParam(value = "categoryID") Integer categoryID,
                                         @RequestParam(value = "productID") Integer productID,
                                         @RequestParam(value = "start") String startDate,
                                         @RequestParam(value = "end") String endDate) {
        logger.debug(LogUtil.getMethodName() + String.format(". Интервал: %s - %s", startDate, endDate));

        List<CalendarEntity> calendarEntities;

        User currentUser = securityService.findLoggedUser();

        LocalDate after = DateUtil.getParsedDate(startDate);
        LocalDate before = DateUtil.getParsedDate(endDate);

        JsonResponse response = utilService.getById(Category.class, categoryID);
        Category category = response.getEntity() != null ? (Category) response.getEntity() : null;
        response = utilService.getById(Product.class, productID);
        Product product = response.getEntity() != null ? (Product) response.getEntity() : null;

        calendarEntities = amountService.getCalendarEntities(currentUser, after, before, category, product);

        return calendarEntities;
    }

    /**
     * Функция возвращает ModelAndView с таблицей amount за заданную дату
     *
     * @param startDate дата начала (строка)
     * @param endDate   дата конца периода (строка)
     * @return ModelAndView /data/page-product
     */
    @RequestMapping(value = "/statistic/calendar/getAmountsByDate", method = RequestMethod.GET)
    public ModelAndView getAmountsByDate(@RequestParam(value = "start") String startDate,
                                         @RequestParam(value = "end") String endDate) {
        logger.debug(LogUtil.getMethodName() + String.format("Интервал: %s - %s", startDate, endDate));

        ModelAndView modelAndView = new ModelAndView("/data/page-product");
        User user = securityService.findLoggedUser();
        LocalDate after = DateUtil.getParsedDate(startDate);
        LocalDate before = DateUtil.getParsedDate(endDate);

        List<Amount> amounts = amountService.getByDate(user, after, before);

        modelAndView.addObject("amounts", amounts);
        modelAndView.addObject("title", "Просмотр оборотов за дату");
        modelAndView.addObject("details", FormatUtil.localDateToString(after));
        modelAndView.addObject("footer", "");

        return modelAndView;
    }

    /**
     * Функция возвращает список групп товаров по переданной категории
     * @param categoryID id обрабатываемой категории, при null - "все"
     * @return List<Product>
     */
    @RequestMapping(value = "/statistic/calendar/getProducts", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Product> getProducts(@RequestParam(value = "categoryID") Integer categoryID) {
        logger.debug(LogUtil.getMethodName());

        List<Product> products;
        User currentUser = securityService.findLoggedUser();

        Category category = (Category) utilService.getById(Category.class, categoryID).getEntity();
        if (category != null)
            products = productService.getByCategory(currentUser, category);
        else
            products = productService.getAll();

        return products;
    }
}
