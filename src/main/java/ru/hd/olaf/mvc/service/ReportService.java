package ru.hd.olaf.mvc.service;

import ru.hd.olaf.util.json.BarEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by d.v.hozyashev on 20.04.2017.
 */
public interface ReportService {

    Map<Map<Integer, String>, BigDecimal> getCategoryContentByIdDepricate(Integer id);

    List<BarEntity> getCategoryContentById(Integer id);

}
