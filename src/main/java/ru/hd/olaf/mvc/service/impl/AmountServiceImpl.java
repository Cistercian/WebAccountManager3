package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.repository.AmountRepository;
import ru.hd.olaf.mvc.service.AmountService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Olaf on 13.04.2017.
 */
@Service
public class AmountServiceImpl implements AmountService {

    @Autowired
    private AmountRepository amountRepository;

    public Amount add(Amount amount) {
        return amountRepository.save(amount);
    }

    public List<Amount> getAll() {
        return Lists.newArrayList(amountRepository.findAll());
    }

    public List<Amount> getByCategory(Category categoryId){
        List<Amount> amounts = amountRepository.findByCategoryId(categoryId);
        Collections.sort(amounts, new Comparator<Amount>() {
            public int compare(Amount o1, Amount o2) {
                return o2.getPrice().compareTo(o1.getPrice());
            }
        });

        return amounts;
    }

    public List<Amount> getByCategoryAndProduct(Category categoryId, Product productId) {
        return amountRepository.findByCategoryIdAndProductId(categoryId, productId);
    }

    public List<Amount> getByProduct(Product product) {
        return amountRepository.findByProductId(product);
    }

    public BigDecimal getSumByCategoryAndProduct(Category category, Product product, LocalDate after, LocalDate before) {
        List<Amount> amounts = getByCategoryAndProduct(category, product);

        BigDecimal sumAmounts = new BigDecimal("0");
        for (Amount amount : amounts) {

            LocalDate amountDate = amount.getLocalDate();
            if (amountDate.isAfter(after) && amountDate.isBefore(before)) {
                sumAmounts = sumAmounts.add(amount.getPrice());
            }
        }

        return sumAmounts;
    }

    public Amount getById(int id) {
        return amountRepository.findOne(id);
    }

    public String delete(Integer id) {
        try {
            amountRepository.delete(id);
            return "delete successfully";
        } catch (Exception e) {
            return "delete was not coplited";
        }
    }
}
