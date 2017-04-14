package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Amounts;
import ru.hd.olaf.entities.Categories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface AmountsService {

    Amounts add(Amounts amounts);
    List<Amounts> getAll();
    Map<Categories, BigDecimal> getSumByCategory();
    List<Amounts> getListByCategory();
}
