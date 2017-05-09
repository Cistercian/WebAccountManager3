package ru.hd.olaf.mvc.service.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Notification;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.repository.NotificationRepository;
import ru.hd.olaf.mvc.service.NotificationService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.mvc.service.UtilService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.util.List;

/**
 * Created by Olaf on 08.05.2017.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UtilService utilService;

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    public Notification getOne(Integer id) throws AuthException, IllegalArgumentException {
        logger.debug(LogUtil.getMethodName());

        if (id == null) throw new IllegalArgumentException();

        Notification notification = notificationRepository.findOne(id);
        if (notification == null) return null;

        if (!notification.getUserId().equals(securityService.findLoggedUser()))
            throw new AuthException(String.format("Запрошенный объект с id %d Вам не принадлежит.", id));

        return notification;
    }

    public JsonResponse getById(Integer id) {
        logger.debug(LogUtil.getMethodName());
        return utilService.getById(Notification.class, id);
    }

    public List<Notification> getAll() {
        logger.debug(LogUtil.getMethodName());
        User user = securityService.findLoggedUser();

        return notificationRepository.findByUserId(user);
    }

    public Notification save(Notification notification) throws CrudException {
        logger.debug(LogUtil.getMethodName());

        Notification entity = null;

        try {
            entity = notificationRepository.save(notification);
        } catch (Exception e) {
            throw new CrudException(ExceptionUtils.getRootCause(e).getMessage());
        }

        return entity;
    }

    public JsonResponse delete(Notification notification) throws CrudException {
        logger.debug(LogUtil.getMethodName());

        try {
            notificationRepository.delete(notification.getId());
            return new JsonResponse(ResponseType.SUCCESS, "Удаление успешно завершено.");
        } catch (Exception e) {
            throw new CrudException(ExceptionUtils.getRootCause(e).getMessage());
        }
    }
}
