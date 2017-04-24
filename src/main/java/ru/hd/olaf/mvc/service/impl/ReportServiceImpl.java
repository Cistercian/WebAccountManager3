package ru.hd.olaf.mvc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.mvc.service.ReportService;
import ru.hd.olaf.util.json.BarEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    public List<BarEntity> getCategoryContentById(Integer id, LocalDate after, LocalDate before) {
        logger.debug(String.format("Function %s", "getCategoryContentById"));

        List<BarEntity> barEntities = new ArrayList<BarEntity>();
        Category category = categoryService.getById(id);

        //собираем данные по таблице categories
        barEntities = categoryService.getBarEntityOfSubCategories(category, after, before);

        //собираем данные по таблице amount с группировкой по Product
        //TODO: query?
        getBarEntitiesByCategory(after, before, barEntities, category);


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

    public void getBarEntitiesByCategory(LocalDate after, LocalDate before, List<BarEntity> barEntities, Category category) {
        List<Product> products = productService.getAll();
        for (Product product : products) {

            BigDecimal sumAmounts = amountService.getSumByCategoryAndProduct(category,
                    product,
                    after,
                    before);

            if (sumAmounts.compareTo(new BigDecimal("0")) > 0) {
                BarEntity barEntity = new BarEntity(
                        product.getClass().getSimpleName(),
                        product.getId(),
                        sumAmounts,
                        product.getName());

                barEntities.add(barEntity);
            }
        }
    }
}
