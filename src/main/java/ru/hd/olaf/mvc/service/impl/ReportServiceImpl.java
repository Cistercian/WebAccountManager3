package ru.hd.olaf.mvc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ProductService;
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
    private CategoryService categoryService;
    @Autowired
    private AmountService amountService;
    @Autowired
    private ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    public List<BarEntity> getCategoryContentById(Integer id) {
        logger.debug(String.format("Function %s", "getCategoryContentById"));

        List<BarEntity> barEntities = new ArrayList<BarEntity>();
        Category category = categoryService.getById(id);

        //собираем данные по таблице categories
        Map<Category, BigDecimal> categoryPrices=
                categoryService.getCategoryPrice(categoryService.getByParentId(category));

        //агрегируем данные
        for (Map.Entry<Category, BigDecimal> entry : categoryPrices.entrySet()) {
            BarEntity barEntity = new BarEntity(
                    entry.getKey().getClass().getSimpleName(),
                    entry.getKey().getId(),
                    new BigDecimal(entry.getValue().toString()),
                    entry.getKey().getName());

            barEntities.add(barEntity);
        }

        //собираем данные по таблице amount с группировкой по Product
        //TODO: query?
        List<Product> products = productService.getAll();
        for (Product product : products) {

            BigDecimal sumAmounts = amountService.getSumByCategoryAndProduct(category, product);
            if (sumAmounts.compareTo(new BigDecimal("0")) > 0) {
                BarEntity barEntity = new BarEntity(
                        product.getClass().getSimpleName(),
                        product.getId(),
                        sumAmounts,
                        product.getName());

                barEntities.add(barEntity);
            }

        }


        Collections.sort(barEntities, new Comparator<BarEntity>() {
            public int compare(BarEntity o1, BarEntity o2) {
                return o2.getSum().compareTo(o1.getSum());
            }
        });

        logger.debug(String.format("Sorted list for injecting:"));
        for (BarEntity barEntity : barEntities){
            logger.debug(String.format("%s", barEntity));
        }

        return barEntities;
    }
}
