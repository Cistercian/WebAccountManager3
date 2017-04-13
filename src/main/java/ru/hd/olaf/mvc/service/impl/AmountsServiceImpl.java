package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amounts;
import ru.hd.olaf.mvc.repository.AmountsRepository;
import ru.hd.olaf.mvc.service.AmountsService;

import java.util.List;

/**
 * Created by Olaf on 13.04.2017.
 */
@Service
public class AmountsServiceImpl implements AmountsService {

    @Autowired
    private AmountsRepository amountsRepository;

    public Amounts addAmount(Amounts amounts) {
        return amountsRepository.save(amounts);
    }

    public List<Amounts> getAll() {
        return Lists.newArrayList(amountsRepository.findAll());
    }
}
