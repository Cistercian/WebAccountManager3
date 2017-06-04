package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Mail;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.repository.MailRepository;
import ru.hd.olaf.mvc.service.LimitService;
import ru.hd.olaf.mvc.service.MailService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.mvc.service.UtilService;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by d.v.hozyashev on 24.05.2017.
 */
@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private MailRepository mailRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private LimitService limitService;

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    /**
     * Функция возвращает product по id с проверкой на пользователя
     * @param id
     * @return
     */
    public Mail getOne(Integer id) throws AuthException, IllegalArgumentException {
        logger.debug(LogUtil.getMethodName());
        if (id == null) throw new IllegalArgumentException();

        Mail mail = mailRepository.findOne(id);
        if (mail == null) return null;

        if (!mail.getUserId().equals(securityService.findLoggedUser()))
            throw new AuthException(String.format("Запрошенный объект с id %d Вам не принадлежит.", id));

        return mail;
    }

    public JsonResponse getById(Integer id) {
        logger.debug(LogUtil.getMethodName());

        return utilService.getById(Product.class, id);
    }

    /**
     * Функция возвращает список всех Product текущего пользователя
     * @return
     */
    public List<Mail> getAll() {
        logger.debug(LogUtil.getMethodName());
        return Lists.newArrayList(mailRepository.findByUserId(securityService.findLoggedUser()));
    }

    /**
     * Функция сохраняет(обновляет) запись amount
     *
     * @param mail Сохраняемая сущность
     * @return ссылка на сохраненный объект
     */
    public Mail save(Mail mail) throws CrudException {
        logger.debug(LogUtil.getMethodName());

        Mail entity;

        try {
            entity = mailRepository.save(mail);
        } catch (Exception e) {
            throw new CrudException(ExceptionUtils.getRootCause(e).getMessage());
        }

        return entity;
    }

    /**
     * Функция удаления записи из БД
     *
     * @param mail Удаляемая сущность
     * @return ответ JsonResponse
     */
    public JsonResponse delete(Mail mail) throws CrudException {
        logger.debug(LogUtil.getMethodName());

        try {
            mailRepository.delete(mail.getId());
            return new JsonResponse(ResponseType.SUCCESS, "Удаление успешно завершено.");
        } catch (Exception e) {
            throw new CrudException(ExceptionUtils.getRootCause(e).getMessage());
        }
    }

    /**
     * Функция проверяет нарушения лимитов и при их существовании создает запись в таблице mail
     * @param user текущий пользователь
     * @return объект JsonResponse
     */
    public JsonResponse checkAllLimits(User user) {
        logger.debug(LogUtil.getMethodName());

        JsonResponse response = new JsonResponse();
        List<BarEntity> list = new ArrayList<BarEntity>();
        LocalDate today = LocalDate.now();

        list.addAll(limitService.getLimits(user, (byte)0, LocalDate.now(), today));
        list.addAll(limitService.getLimits(user, (byte)1, DateUtil.getStartOfWeek(), today));
        list.addAll(limitService.getLimits(user, (byte)2, DateUtil.getStartOfMonth(), today));

        createMailLimit(user, list);

        return response;
    }

    public void checkLimit(User user, Object entity){
        logger.debug(LogUtil.getMethodName());

        List<BarEntity> list = new ArrayList<BarEntity>();

        if (entity instanceof Product) {
            Product product = (Product) entity;
            logger.debug("Проверяем превышение лимита по группе товаров " + product);

            list.addAll(limitService.getLimitsByProduct(user, product));
        } else if (entity instanceof Category) {
            Category category = (Category) entity;
            logger.debug("Проверяем превышение лимита по категории " + category);

            list.addAll(limitService.getLimitsByCategory(user, category));

            if (category.getParentId() != null)
                checkLimit(user, category.getParentId());
        }

        createMailLimit(user, list);
    }

    private void createMailLimit(User user, List<BarEntity> list) {
        logger.debug(LogUtil.getMethodName());

        for (BarEntity entity : list) {
            logger.debug(entity.toString());

            if (entity.getSum().compareTo(entity.getLimit()) >= 0) {
                String title = "Уведомление о достижении лимита.";
                String text = "<p class=\"lead text-justify\">Зафиксирован перерасход лимита по " +
                        (entity.getType().equalsIgnoreCase("product") ? "группе товаров " : "категории ") +
                        " <strong>" + entity.getName() + "</strong>. </p> " +
                        "<div class=\"wam-margin-top-2\">" +
                        "<p class=\"lead wam-margin-bottom-0\">Установленный лимит: " + entity.getLimit() + " руб.</p>" +
                        "<p class=\"lead\">Текущая сумма: " + entity.getSum() + " руб.</p>" +
                        "</div>" +
                        "<p class=\"lead\"><a href=\"statistic/limit-control\">Перейти к таблице лимитов</a></p>" +
                        "<h3 class=\"text-right wam-margin-right-2\"><small>С уважением Уведомлятор v 0.0.1.</small><h3>";

                logger.debug(text);

                Mail mail = new Mail("System",
                        title,
                        text,
                        user);

                JsonResponse saveResponse = utilService.saveEntity(mail);
                logger.debug(saveResponse.toString());
            }
        }
    }

    /**
     * Функция возвращает список из тем непрочтенных писем
     * @param user польователь
     * @return list
     */
    public List<String> getUnreadTitle(User user) {
        return mailRepository.getUnreadTitle(user);
    }
}
