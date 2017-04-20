package ru.hd.olaf.mvc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ReportService;
import ru.hd.olaf.util.MapComparatorByValue;
import ru.hd.olaf.util.json.BarEntity;

import java.math.BigDecimal;
import java.util.*;

/**
 * Сервис агрегации данных нескольких таблиц
 *
 * Created by d.v.hozyashev on 20.04.2017.
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    CategoryService categoryService;
    @Autowired
    AmountService amountService;

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    public Map<Map<Integer, String>, BigDecimal> getCategoryContentByIdDepricate(Integer id) {
        Category parent = categoryService.getById(id);

        List<Amount> amounts = amountService.getByCategory(parent);
        //собираем таблицу дочерняя категория-сумма по ней.
        Map<Category, BigDecimal> categoryPrices=
                categoryService.getCategoryPrice(categoryService.getByParentId(parent));
        //инициализируем выходную мапу
        Map<Map<Integer, String>, BigDecimal> content = new HashMap<Map<Integer, String>, BigDecimal>();

        //агрегация данных по дочерним категориям
        for (Map.Entry<Category, BigDecimal> entry : categoryPrices.entrySet()) {
            Map<Integer, String> key = new HashMap<Integer, String>();
            key.put(entry.getKey().getId(), entry.getKey().getClass().getSimpleName());

            content.put(key, new BigDecimal(entry.getValue().toString()));
        }

        //агрегация данных по amounts
        for (Amount amount : amounts) {
            Map<Integer, String> key = new HashMap<Integer, String>();
            key.put(amount.getId(), amount.getClass().getSimpleName());

            content.put(key, new BigDecimal(amount.getPrice().toString()));
        }

        //сортировка выходной мапы по убыванию значения
        MapComparatorByValue comparatorByValue = new MapComparatorByValue(content);

        Map<Map<Integer, String>, BigDecimal> sortedContent =
                new TreeMap<Map<Integer, String>, BigDecimal>(comparatorByValue);

        sortedContent.putAll(content);

        return sortedContent;
    }

    public List<BarEntity> getCategoryContentById(Integer id) {
        logger.debug(String.format("Function %s", "getCategoryContentById"));

        List<BarEntity> barEntities = new ArrayList<BarEntity>();
        Category parent = categoryService.getById(id);

        //собираем данные по таблице amount
        List<Amount> amounts = amountService.getByCategory(parent);
        //собираем данные по таблице categories
        Map<Category, BigDecimal> categoryPrices=
                categoryService.getCategoryPrice(categoryService.getByParentId(parent));

        //агрегируем данные
        for (Map.Entry<Category, BigDecimal> entry : categoryPrices.entrySet()) {
            BarEntity barEntity = new BarEntity(
                    entry.getKey().getClass().getSimpleName(),
                    entry.getKey().getId(),
                    new BigDecimal(entry.getValue().toString()),
                    entry.getKey().getName());

            barEntities.add(barEntity);
        }
        for (Amount amount : amounts) {
            BarEntity barEntity = new BarEntity(
                    amount.getClass().getSimpleName(),
                    amount.getId(),
                    new BigDecimal(amount.getPrice().toString()),
                    amount.getName());

            barEntities.add(barEntity);
        }

        Collections.sort(barEntities, new Comparator<BarEntity>() {
            public int compare(BarEntity o1, BarEntity o2) {
                return o2.getSum().compareTo(o1.getSum());
            }
        });

        logger.debug(String.format("Sorted list:"));
        for (BarEntity barEntity : barEntities){

            logger.debug(String.format("%s", barEntity));
            logger.debug(String.format("%s", barEntity.getSum().toString()));
        }

        return barEntities;
    }
}
