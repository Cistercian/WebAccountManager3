package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.mvc.repository.AmountRepository;
import ru.hd.olaf.mvc.service.AmountService;

import java.math.BigDecimal;
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

    public List<Amount> getAllByCategoryId(Category categoryId){
        List<Amount> amounts = amountRepository.findByCategoryId(categoryId);
        Collections.sort(amounts, new Comparator<Amount>() {
            public int compare(Amount o1, Amount o2) {
                return o2.getPrice().compareTo(o1.getPrice());
            }
        });

        return amounts;
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
