package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Limit;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.repository.LimitRepository;
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
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Created by Olaf on 08.05.2017.
 */
@Service
public class LimitServiceImpl implements LimitService {

    @Autowired
    private LimitRepository limitRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(LimitServiceImpl.class);

    public Limit getOne(Integer id) throws AuthException, IllegalArgumentException {
        logger.debug(LogUtil.getMethodName());

        if (id == null) throw new IllegalArgumentException();

        Limit limit = limitRepository.findOne(id);
        if (limit == null) return null;

        if (!limit.getUserId().equals(securityService.findLoggedUser()))
            throw new AuthException(String.format("Запрошенный объект с id %d Вам не принадлежит.", id));

        return limit;
    }

    public JsonResponse getById(Integer id) {
        logger.debug(LogUtil.getMethodName());
        return utilService.getById(Limit.class, id);
    }

    public List<Limit> getAll() {
        logger.debug(LogUtil.getMethodName());
        User user = securityService.findLoggedUser();

        return limitRepository.findByUserId(user);
    }

    public Limit save(Limit limit) throws CrudException {
        logger.debug(LogUtil.getMethodName());

        Limit entity;

        try {
            entity = limitRepository.save(limit);

            //дополнительно после сохранения проверяем выполнение текущего лимита
            User currentUser = securityService.findLoggedUser();
            if (limit.getCategoryId() != null)
                mailService.checkLimit(currentUser, limit.getCategoryId());
            else if (limit.getProductId() != null)
                mailService.checkLimit(currentUser, limit.getProductId());
        } catch (Exception e) {
            logger.debug(String.format("Exception handled: %s", e.getMessage()));
            throw new CrudException(ExceptionUtils.getRootCause(e).getMessage());
        }

        return entity;
    }

    public JsonResponse delete(Limit limit) throws CrudException {
        logger.debug(LogUtil.getMethodName());

        try {
            limitRepository.delete(limit.getId());
            return new JsonResponse(ResponseType.SUCCESS, "Удаление успешно завершено.");
        } catch (Exception e) {
            throw new CrudException(ExceptionUtils.getRootCause(e).getMessage());
        }
    }

    /**
     * Функция возвращает BarEntity для прорисовки прогресс баров по таблице лимитов
     * @param user пользователь
     * @param period период (0 - день, 1 - неделя, 2 - месяц)
     * @param after начальная дата отсечки
     * @param before конечная дата отсечки
     * @return список BarEntity
     */
    public List<BarEntity> getLimits(User user, Byte period, LocalDate after, LocalDate before) {
        logger.debug(LogUtil.getMethodName());

        Date begin = Date.from(after.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(before.atStartOfDay(ZoneId.systemDefault()).toInstant());

        logger.debug(String.format("Интервал: %s - %s", after, before));

        //TODO: union?
        List<BarEntity> bars = limitRepository.findLimitAndSumAmountsGroupByProduct(user, period, begin, end);
        bars.addAll(limitRepository.findLimitAndSumAmountsGroupByCategory(user, period, begin, end));

        return bars;
    }

    /**
     * Функция возвращает запись таблицы limit по периоду и подчиненной сущности (используется валидаторов)
     * @param period Byte period
     * @param entity Object
     * @return Limit or null
     */
    public Limit getByPeriodAndEntity(Byte period, Object entity) {
        logger.debug(LogUtil.getMethodName());

        User user = securityService.findLoggedUser();
        Limit limit = null;

        if (entity instanceof Category)
            limit = limitRepository.findByUserIdAndPeriodAndCategoryId(user, period, (Category) entity);
        else if (entity instanceof Product)
            limit = limitRepository.findByUserIdAndPeriodAndProductId(user, period, (Product) entity);

        return limit;
    }

    /**
     * Функция возвращает список объектов BarEntity с информацией по текущему расходу лимитов по отдельно взятой группе
     * @param user Пользователь
     * @param product Рассматриваемая товарная группа
     * @return Список
     */
    public List<BarEntity> getLimitsByProduct(User user, Product product) {
        return limitRepository.findLimitAndSumAmountsByProduct(user,
                product,
                DateUtil.getDate(LocalDate.now()),
                DateUtil.getDate(DateUtil.getStartOfWeek()),
                DateUtil.getDate(DateUtil.getStartOfMonth()),
                DateUtil.getDate(LocalDate.now()));
    }

    /**
     * Функция возвращает список объектов BarEntity с информацией по текущему расходу лимитов по отдельно взятой
     * категории
     * @param user Пользователь
     * @param category Рассматриваемая категория
     * @return Список
     */
    public List<BarEntity> getLimitsByCategory(User user, Category category) {
        return limitRepository.findLimitAndSumAmountsByCategory(user,
                category,
                DateUtil.getDate(LocalDate.now()),
                DateUtil.getDate(DateUtil.getStartOfWeek()),
                DateUtil.getDate(DateUtil.getStartOfMonth()),
                DateUtil.getDate(LocalDate.now()));
    }
}
