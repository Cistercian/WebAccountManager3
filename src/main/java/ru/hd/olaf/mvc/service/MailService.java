package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Mail;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.util.json.JsonResponse;

import java.util.List;

/**
 * Created by d.v.hozyashev on 24.05.2017.
 */
public interface MailService {
    Mail getOne(Integer id) throws AuthException, IllegalArgumentException;

    JsonResponse getById(Integer id);

    List<Mail> getAll();

    Mail save(Mail mail) throws CrudException;

    JsonResponse delete(Mail mail) throws CrudException;

    JsonResponse checkAllLimits(User user);

    void checkLimit(User user, Object entity);

    List<String> getUnreadTitle(User user);
}
