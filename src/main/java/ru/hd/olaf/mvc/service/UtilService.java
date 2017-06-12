package ru.hd.olaf.mvc.service;

import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.DBData;
import ru.hd.olaf.util.json.JsonResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Olaf on 08.05.2017.
 */
public interface UtilService {
    JsonResponse getById(Class classez, Integer id);

    JsonResponse saveEntity(Object entity);

    JsonResponse deleteEntity(String className, Integer id);

    List<BarEntity> calcAvgSum(List<BarEntity> barEntities, LocalDate after, LocalDate before, byte averagingPeriod);

    <T extends DBData> List<T> sortListByTypeAndSum(List<T> barEntities);

    <T extends DBData> List<T> sortByLimit(List<T> entities);
}
