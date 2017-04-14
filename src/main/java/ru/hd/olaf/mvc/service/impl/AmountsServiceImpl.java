package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amounts;
import ru.hd.olaf.entities.Categories;
import ru.hd.olaf.mvc.repository.AmountsRepository;
import ru.hd.olaf.mvc.service.AmountsService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Olaf on 13.04.2017.
 */
@Service
public class AmountsServiceImpl implements AmountsService {

    @Autowired
    private AmountsRepository amountsRepository;

    public Amounts add(Amounts amounts) {
        return amountsRepository.save(amounts);
    }

    public List<Amounts> getAll() {
        return Lists.newArrayList(amountsRepository.findAll());
    }

    public Map<Categories, BigDecimal> getSumByCategory() {
        Map<Categories, BigDecimal> map = new HashMap<Categories, BigDecimal>();
        List<Amounts> amounts = getAll();

        for (Amounts amount : amounts) {
            BigDecimal price = map.containsKey(amount.getCategoryId()) ? map.get(amount.getCategoryId()) :
                    new BigDecimal(0);
            map.put(amount.getCategoryId(), price.add(amount.getPrice()));
        }
        return map;
    }

    public List<Amounts> getListByCategory(){
        //return Lists.newArrayList(amountsRepository.findAllOrderByPriceDesc());
        return getAll();
    }
}
