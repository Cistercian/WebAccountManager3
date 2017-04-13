package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Amounts;

import java.util.List;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface AmountsService {

    Amounts addAmount(Amounts amounts);
    List<Amounts> getAll();

}
