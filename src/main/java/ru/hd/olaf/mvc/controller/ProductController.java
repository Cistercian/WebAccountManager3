package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.mvc.service.UtilService;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.FormatUtil;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by d.v.hozyashev on 21.04.2017.
 */
@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private AmountService amountService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UtilService utilService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    /**
     * Прорисовка страницы просмотра состава товарной группы
     *
     * @param productID productID сущности
     * @param beginDate начальная дата отсечки
     * @param endDate   конечная дата отсечки
     * @return ModelAndView (page-product)
     */
    @RequestMapping(value = "/page-product", method = RequestMethod.GET)
    public ModelAndView displayPageProduct(@RequestParam(value = "productID", required = false) Integer productID,
                                           @RequestParam(value = "categoryID", required = false) Integer categoryID,
                                           @RequestParam(value = "after") String beginDate,
                                           @RequestParam(value = "before") String endDate,
                                           @RequestParam(value = "type", required = false) Integer type) {
        logger.debug(LogUtil.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/data/page-product");

        User currentUser = securityService.findLoggedUser();
        LocalDate after = DateUtil.getParsedDate(beginDate);
        LocalDate before = DateUtil.getParsedDate(endDate);
        String title = "";
        String details = "";
        String footer = "";

        List<Amount> amounts;
        Product product;

        if (productID != null) {
            JsonResponse response = productService.getById(productID);
            if (response.getType() == ResponseType.SUCCESS) {
                product = (Product) response.getEntity();

                response = utilService.getById(Category.class, categoryID);
                if (response.getType() == ResponseType.SUCCESS) {
                    Category category = (Category) response.getEntity();

                    if (type == null || type != 3) {
                        amounts = amountService.getByProductAndCategoryAndDate(
                                currentUser,
                                product,
                                category,
                                after,
                                before);
                    } else {
                        amounts = amountService.getAvgOfPrevMonths(
                                currentUser,
                                product,
                                category);
                        LogUtil.logList(logger, amounts);
                    }
                } else {
                    amounts = amountService.getByProductAndDate(
                            currentUser,
                            product,
                            after,
                            before
                    );
                }

                modelAndView.addObject("amounts", amounts);

                modelAndView.addObject("id", product.getId());
                title = "Просмотр содержимого группы товаров";
                details = product.getName();
                footer = "<a href='/product?id=" + product.getId() + "'>(редактировать)</a>";

            } else
                logger.debug(String.format("Не найдена товарная граппа с id %d", productID));
        } else if (categoryID != null) {
            Category category = (Category) utilService.getById(Category.class, categoryID).getEntity();

            amounts = amountService.getByType(
                    currentUser,
                    category,
                    after,
                    before,
                    type);

            modelAndView.addObject("amounts", amounts);
            switch (type) {
                case 0:
                    title = "Просмотр стандартных оборотов по категории";
                    break;
                case 1:
                case 2:
                    title = "Просмотр единоразовых оборотов по категории";
                    break;
                case 3:
                    title = "Просмотр обязательных оборотов по категории";
                    break;
            }
            modelAndView.addObject("type", type);
            modelAndView.addObject("after", after);
            modelAndView.addObject("before", before);
            modelAndView.addObject("categoryID", categoryID);

            details = "Категория: " + (category != null ? category.getName() : "отсутствует");
        }

        modelAndView.addObject("title", title);
        modelAndView.addObject("details", details);
        modelAndView.addObject("footer", footer);

        return modelAndView;
    }

    /**
     * Страница просмотра таблицы обязательных оборотов
     *
     * @param isBinding флаг находимся ли мы на странице привзяки оборота
     * @param id        id оборота, к которому осуществляется привязка
     * @param regularId id текущего привязанного оборота
     * @return ModelAndView page-product
     */
    @RequestMapping(value = "/page-product/regulars", method = RequestMethod.GET)
    public ModelAndView getRegularAmounts(@RequestParam(value = "isBinding", required = false) Boolean isBinding,
                                          @RequestParam(value = "productID", required = false) Integer id,
                                          @RequestParam(value = "regularId", required = false) Integer regularId) {
        logger.debug(LogUtil.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/data/page-product");

        String title;
        String details;

        if (isBinding == null) isBinding = false;

        if (isBinding) {
            title = "Привязка обязательного оборота";
            details = "Обрабатываемый оборот: новая запись";
            modelAndView.addObject("isSingle", true);
        } else {
            title = "Просмотр обязательных оборотов";
            details = "Полный список";
            modelAndView.addObject("isGetRegulars", true);
        }

        if (regularId != null)
            modelAndView.addObject("regularId", regularId);

        if (id == null) id = 0;
        else {
            JsonResponse response = utilService.getById(Amount.class, id);

            if (response.getType() == ResponseType.SUCCESS) {
                Amount amount = (Amount) response.getEntity();

                details = "Обрабатываемый оборот: #" + amount.getId() + " на сумму " +
                        FormatUtil.numberToString(amount.getPrice()) +
                        " (" + amount.getName() + ")";
            }
        }

        User currentUser = securityService.findLoggedUser();

        List<Amount> amounts = amountService.getAllRegular(currentUser);

        logger.debug("Список обязательных оборотов:");
        LogUtil.logList(logger, amounts);

        modelAndView.addObject("amounts", amounts);
        modelAndView.addObject("id", id);
        modelAndView.addObject("title", title);
        modelAndView.addObject("details", details);
        modelAndView.addObject("footer", "");
        modelAndView.addObject("isBinding", isBinding);

        return modelAndView;
    }

    /**
     * Функция сохранения привязки обязательного оборота
     *
     * @param amount      обрабатываемая сущность оборота
     * @param productName имя группы товаров
     * @param categoryId  id категории
     * @param regularId   id текущего привязанного обязательного оборота
     * @param referer     ссылка на предыдущую стрианицу для кнопки "Назад"
     * @return ModelAndView page-product
     */
    @RequestMapping(value = "/page-product/regulars", method = RequestMethod.POST)
    public ModelAndView getRegulars(@ModelAttribute("amountForm") Amount amount,
                                    @RequestParam(value = "productName") String productName,
                                    @RequestParam(value = "category") Integer categoryId,
                                    @RequestParam(value = "regular", required = false) Integer regularId,
                                    @RequestParam(value = "referer", required = false) String referer) {
        logger.debug(LogUtil.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/data/page-product");

        if (amount.getPrice() == null)
            amount.setPrice(new BigDecimal("0"));

        String title;
        StringBuilder details = new StringBuilder("Обрабатываемый оборот: #");

        title = "Привязка обязательного оборота";
        details.append(amount.getId() != null ? amount.getId() : "б/н");
        details.append(" ");
        details.append("(").append(amount.getName()).append(")</p><p>на сумму ")
                .append(FormatUtil.numberToString(amount.getPrice())).append(" руб.");

        modelAndView.addObject("isSingle", true);

        if (amount.getRegularId() != null)
            modelAndView.addObject("regularId", amount.getRegularId().getId());

        Integer id = (amount != null && amount.getId() != null) ? amount.getId() : 0;

        if (amount != null) {
            logger.debug(String.format("Переданная сущность: %s", amount));
        } else
            logger.debug("Не удается прочитать переданную сущность.");

        User currentUser = securityService.findLoggedUser();
        List<Amount> regulars = amountService.getAllRegular(currentUser);

        logger.debug("Список обязательных оборотов:");
        LogUtil.logList(logger, regulars);

        if (categoryId != null) {
            modelAndView.addObject("category", categoryId);
        }
        if (regularId != null) {
            modelAndView.addObject("regular", regularId);
        }
        modelAndView.addObject("amounts", regulars);
        modelAndView.addObject("amountForm", amount);
        modelAndView.addObject("id", id);
        modelAndView.addObject("title", title);
        modelAndView.addObject("details", details);
        modelAndView.addObject("footer", "");
        modelAndView.addObject("isBinding", "true");
        modelAndView.addObject("referer", referer);
        modelAndView.addObject("productName", productName);

        return modelAndView;
    }

    /**
     * Страница просмотра привязанных оборотов для ознакомления на странице просмотра статистики
     *
     * @param type       тип просматриваемых оборотов
     * @param categoryID id просматриваемой категории
     * @param beginDate  начальная дата периода, за который просматриваем обороты
     * @param endDate    конечная дата периода
     * @return ModelAndView page-product
     */
    @RequestMapping(value = "/page-product/binding", method = RequestMethod.GET)
    public ModelAndView getBindingTable(@RequestParam(value = "type") Byte type,
                                        @RequestParam(value = "categoryID") Integer categoryID,
                                        @RequestParam(value = "after") String beginDate,
                                        @RequestParam(value = "before") String endDate) {
        logger.debug(LogUtil.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/data/page-product");

        User currentUser = securityService.findLoggedUser();
        LocalDate after = DateUtil.getParsedDate(beginDate);
        LocalDate before = DateUtil.getParsedDate(endDate);

        List<Amount> amounts = null;
        Category category = null;
        JsonResponse response = utilService.getById(Category.class, categoryID);
        if (response.getType() == ResponseType.SUCCESS) {
            category = (Category) response.getEntity();

            amounts = amountService.getAmountsForBindingByType(
                    currentUser,
                    category,
                    after,
                    before,
                    type
            );
        }

        modelAndView.addObject("type", type);
        modelAndView.addObject("title", "Привязка оборотов по категории");
        modelAndView.addObject("details", "Категория: " + (category != null ? category.getName() : "отсутствует"));
        modelAndView.addObject("footer", "");
        modelAndView.addObject("isBinding", true);
        modelAndView.addObject("onclickOk", "setTypes(" + type + ");");
        modelAndView.addObject("onclickCancel", "location.href=document.referrer;");
        modelAndView.addObject("amounts", amounts);

        return modelAndView;
    }

    /**
     * Функция групповой смены типа у оборотов
     *
     * @param ids  массив id обрабатываемых оборотов
     * @param type тип, к которому привязываются обороты
     * @return JsonResponse с результатом привязки
     */
    @RequestMapping(value = "/page-product/binding", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonResponse setTypes(@RequestParam(value = "ids[]") Integer ids[],
                          @RequestParam(value = "type") Byte type) {
        logger.debug(LogUtil.getMethodName());

        JsonResponse response = new JsonResponse();
        StringBuilder message = new StringBuilder();
        String text;

        for (Integer id : ids) {
            logger.debug(String.format("Обработка id %s", id));

            JsonResponse response1 = utilService.getById(Amount.class, id);
            if (response1.getType() == ResponseType.SUCCESS) {
                Amount amount = (Amount) response1.getEntity();

                amount.setType(type);

                response1 = utilService.saveEntity(amount);
                text = "Оборот #" + id + ": смена типа: " + response1.getMessage() + "<p>";
            } else {
                text = "Оборот #" + id + ": ошибка захвата объекта: " + response.getMessage() + "<p>";
            }
            message.append(text);
        }

        logger.debug(message.toString());

        response.setType(ResponseType.INFO);
        response.setMessage(message.toString());

        return response;
    }

    /**
     * Служеная функция для парсинга дат
     * @param binder WebDataBinder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}
