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
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.CompareEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Olaf on 05.06.2017.
 */
@Controller
public class CompareController {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private AmountService amountService;

    private static final Logger logger = LoggerFactory.getLogger(CompareController.class);

    @RequestMapping(value = "/statistic/compare", method = RequestMethod.GET)
    public ModelAndView getViewCompare() {
        logger.debug(LogUtil.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/statistic/compare");

        return modelAndView;
    }

    @RequestMapping(value = "/statistic/compare/getCompareData", method = RequestMethod.GET)
    public
    @ResponseBody
    CompareEntity getCompareData(@RequestParam(value = "query") String query) {
        logger.debug(LogUtil.getMethodName());
        logger.debug(String.format("query for matching: %s", query));

        User currentUser = securityService.findLoggedUser();
        LocalDate after = DateUtil.getStartOfEra();
        LocalDate before = LocalDate.now();
        query = query != null ? query.trim() : "";

        List<Amount> amounts = amountService.getByMatchingName(currentUser, query, after, before);
        BigDecimal avgPrice = amountService.getCompareAvgPrice(currentUser, query, after, before);

        logger.debug("Список по совпадению в наименованиях:");
        LogUtil.logList(logger, amounts);
        logger.debug(String.format("Средняя цена: %s", avgPrice.toString()));

        Collections.sort(amounts, new Comparator<Amount>() {
            public int compare(Amount o1, Amount o2) {
                int i = o1.getPrice().compareTo(o2.getPrice());
                if (i == 0)
                    return o2.getDate().compareTo(o1.getDate());

                return i;
            }
        });

        CompareEntity entity;
        if (amounts.size() > 0) {
            entity = new CompareEntity(
                    amounts.get(0).getPrice(),
                    amounts.get(0).getDate(),
                    amounts.get(amounts.size() - 1).getPrice(),
                    amounts.get(amounts.size() - 1).getDate(),
                    avgPrice,
                    amounts
            );

            //узнаем последюю цену
            Collections.sort(amounts, new Comparator<Amount>() {
                public int compare(Amount o1, Amount o2) {
                    return o2.getDate().compareTo(o1.getDate());
                }
            });

            entity.setLastSum(amounts.get(0).getPrice());
            entity.setLastDate(amounts.get(0).getDate());

        } else {
            entity = new CompareEntity(
                    new BigDecimal("0"),
                    null,
                    new BigDecimal("0"),
                    null,
                    new BigDecimal("0"),
                    null
            );
            entity.setLastSum(new BigDecimal("0"));
        }

        return entity;
    }
}
