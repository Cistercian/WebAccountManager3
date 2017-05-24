package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Limit;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.JsonResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Olaf on 08.05.2017.
 */
public interface LimitService {

    Limit getOne(Integer id) throws AuthException, IllegalArgumentException;

    JsonResponse getById(Integer id);

    Limit getByPeriodAndEntity(Byte period, Object entity);

    List<Limit> getAll();

    List<BarEntity> getLimits(User user, Byte period, LocalDate after, LocalDate before);

    List<BarEntity> getLimitsByProduct(User user, Product product);

    List<BarEntity> getLimitsByCategory(User user, Category category);

    Limit save(Limit limit) throws CrudException;

    JsonResponse delete(Limit limit) throws CrudException;
}
