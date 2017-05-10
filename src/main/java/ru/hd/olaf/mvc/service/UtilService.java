package ru.hd.olaf.mvc.service;

import ru.hd.olaf.util.json.JsonResponse;

/**
 * Created by Olaf on 08.05.2017.
 */
public interface UtilService {
    JsonResponse getById(Class classez, Integer id);
    JsonResponse saveEntity(Object entity);
    JsonResponse deleteEntity(String className, Integer id);
}
