package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Limit;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.JsonResponse;

import java.util.List;

/**
 * Created by Olaf on 08.05.2017.
 */
public interface LimitService {

    Limit getOne(Integer id) throws AuthException, IllegalArgumentException;
    JsonResponse getById(Integer id);
    List<Limit> getAll();

    List<BarEntity> getLimit();

    Limit save(Limit limit) throws CrudException;
    JsonResponse delete(Limit limit) throws CrudException;
}
