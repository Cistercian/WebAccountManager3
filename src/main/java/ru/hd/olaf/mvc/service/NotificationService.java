package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Notification;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.util.json.JsonResponse;

import java.util.List;

/**
 * Created by Olaf on 08.05.2017.
 */
public interface NotificationService {

    Notification getOne(Integer id) throws AuthException, IllegalArgumentException;
    JsonResponse getById(Integer id);
    List<Notification> getAll();

    Notification save(Notification notification) throws CrudException;
    JsonResponse delete(Notification notification) throws CrudException;
}
