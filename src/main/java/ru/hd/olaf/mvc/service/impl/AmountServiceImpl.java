package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.repository.AmountRepository;
import ru.hd.olaf.mvc.service.*;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.CalendarEntity;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.util.json.ResponseType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by Olaf on 13.04.2017.
 */
@Service
public class AmountServiceImpl implements AmountService {

    @Autowired
    private AmountRepository amountRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(AmountServiceImpl.class);

    /**
     * Функция возвращает список amount с проверкой на текущего пользователя
     *
     * @return List<Amount>
     */
    public List<Amount> getAll() {
        logger.debug(LogUtil.getMethodName());
        return Lists.newArrayList(amountRepository.findByUserId(securityService.findLoggedUser()));
    }

    /**
     * Функция возвращает список amount по категории и текущему пользователю
     *
     * @param category рассматриваемая категория
     * @return List<Amount>
     */
    public List<Amount> getByCategory(Category category) {
        logger.debug(LogUtil.getMethodName());
        List<Amount> amounts = amountRepository.findByCategoryIdAndUserId(category, securityService.findLoggedUser());

        Collections.sort(amounts, new Comparator<Amount>() {
            public int compare(Amount o1, Amount o2) {
                return o2.getPrice().compareTo(o1.getPrice());
            }
        });

        return amounts;
    }

    /**
     * Функция возвращает список оборотов пользователя по переданной группе товаров и попадающих в заданный интервал
     *
     * @param user    рассматриваемый пользователь системы
     * @param product группа товаров
     * @param after   начальная дата периода
     * @param before  конечная дата периода
     * @return List<Amount>
     */
    public List<Amount> getByProductAndDate(User user, Product product, LocalDate after, LocalDate before) {
        logger.debug(LogUtil.getMethodName());

        after = after == null ? LocalDate.of(1900, 1, 1) : after;
        before = before == null ? LocalDate.now() : before;

        logger.debug(String.format("Dates interval: %s - %s", after.toString(), before.toString()));

        Date begin = DateUtil.getDate(after);
        Date end = DateUtil.getDate(before);

        List<Amount> amounts = amountRepository.findByProductIdAndUserIdAndDateBetween(
                product,
                user,
                begin,
                end
        );

        return amounts;
    }

    /**
     * Функция возвращает список оборотов по пользователю, группе товаров, категории и интервал удат
     *
     * @param user     пользователь
     * @param product  группа товаров
     * @param category категория
     * @param after    начальная дата
     * @param before   конечная дата
     * @return List<Amount>
     */
    public List<Amount> getByProductAndCategoryAndDate(User user,
                                                       Product product,
                                                       Category category,
                                                       LocalDate after,
                                                       LocalDate before) {
        logger.debug(LogUtil.getMethodName());

        after = after == null ? LocalDate.of(1900, 1, 1) : after;
        before = before == null ? LocalDate.now() : before;

        logger.debug(String.format("Dates interval: %s - %s", after.toString(), before.toString()));

        Date begin = DateUtil.getDate(after);
        Date end = DateUtil.getDate(before);

        List<Amount> amounts = Lists.newArrayList(amountRepository.findByProductIdAndCategoryIdAndUserIdAndDateBetween(
                product,
                category,
                user,
                begin,
                end
        ));

        return amounts;
    }

    /**
     * Функция поиска списка оборотов пользователя за заданный период
     *
     * @param user   рассматриваемый пользователь
     * @param after  начальная дата периода
     * @param before конечный период
     * @return List<Amount>
     */
    public List<Amount> getByDate(User user, LocalDate after, LocalDate before) {
        logger.debug(LogUtil.getMethodName() + String.format(". Интервал: %s - %s", after, before));

        Date begin = DateUtil.getDate(after);
        Date end = DateUtil.getDate(before);

        return Lists.newArrayList(amountRepository.findByUserIdAndDateBetween(user,
                begin,
                end));
    }

    /**
     * Функция возвращает список оборотов пользователя совпадающих с переданной строкой и за заданный период
     *
     * @param user   пользователь
     * @param query  искомая строка
     * @param after  начальная дата
     * @param before конечная дата
     * @return List<Amount>
     */
    public List<Amount> getByMatchingName(User user, String query, LocalDate after, LocalDate before) {
        logger.debug(LogUtil.getMethodName() + String.format(". Интервал: %s - %s", after, before));

        Date begin = DateUtil.getDate(after);
        Date end = DateUtil.getDate(before);

        return amountRepository.getByUserIdAndMatchingNameAndDateBetween(user, "%" + query.toUpperCase() + "%", begin, end);
    }

    /**
     * Функция возвращает среднюю цену оборотов по пользователю, искомой строке и за заданный период
     *
     * @param user   пользователь
     * @param query  строка для фильтрации оборотов
     * @param after  начальная дата периода
     * @param before конечная дата
     * @return средняя цена
     */
    public BigDecimal getCompareAvgPrice(User user, String query, LocalDate after, LocalDate before) {
        logger.debug(LogUtil.getMethodName() + String.format(". Интервал: %s - %s", after, before));

        Date begin = DateUtil.getDate(after);
        Date end = DateUtil.getDate(before);

        BigDecimal result = amountRepository.getAvgPriceByUserIdAndMatchingNameAndDateBetween(user, "%" + query.toUpperCase() + "%", begin, end);
        result = result != null ? result.setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal("0");

        return result;
    }

    /**
     * Функция возвращает список BarEntity, соответствующий набору amount по заданной категории при совпадении
     * amounts.date в заданный период с группировкой по amounts.product (группа товаров)
     *
     * @param category категория
     * @param after    начальная дата периода
     * @param before   конечная дата
     * @return List<BarEntity>
     */
    public List<BarEntity> getBarEntitiesByCategory(User user,
                                                    Category category,
                                                    LocalDate after,
                                                    LocalDate before) {
        logger.debug(LogUtil.getMethodName());

        List<BarEntity> barEntities;

        barEntities = amountRepository.getBarEntityByUserIdAndCategoryIdAndDateGroupByProductId(user,
                category,
                DateUtil.getDate(after),
                DateUtil.getDate(before));

        return barEntities;
    }

    /**
     * Функция вовзвращает список оборотов по пользователю, категории, типу и за заданный период
     *
     * @param user     пользователь
     * @param category категория
     * @param after    начальная дата
     * @param before   конечная дата
     * @param type     тип оборотов
     * @return List<Amount>
     */
    public List<Amount> getByType(User user,
                                  Category category,
                                  LocalDate after,
                                  LocalDate before,
                                  Integer type) {
        logger.debug(LogUtil.getMethodName());

        List<Amount> amounts = amountRepository.getByType(
                user,
                category,
                DateUtil.getDate(after),
                DateUtil.getDate(before),
                type);

        return amounts;
    }

    /**
     * Поиск списка оборотов по пользователю, категории, датам и типами, не совпадающими с переданным значением.
     *
     * @param user     пользователь
     * @param category категория
     * @param after    начальная дата
     * @param before   конечная дата
     * @param type     тип для исключения. Т.е. при передаче значения 1 будут выведены обороты с типами != 1.
     * @return List<Amount>
     */
    public List<Amount> getAmountsForBindingByType(User user,
                                                   Category category,
                                                   LocalDate after,
                                                   LocalDate before,
                                                   Byte type) {
        logger.debug(LogUtil.getMethodName());

        List<Amount> amounts = amountRepository.getAmountsForBinding(
                user,
                category,
                DateUtil.getDate(after),
                DateUtil.getDate(before),
                type);

        return amounts;
    }

    /**
     * Функция возвращает общую сумму по всем amount, относящимся к данной категории, продукту и попадающим
     * в указанный временной интервал
     *
     * @param category категория
     * @param product  группа товаров
     * @param after    начальная дата
     * @param before   конечная дата
     * @return сумма
     */
    public BigDecimal getSumByCategoryAndProduct(Category category,
                                                 Product product,
                                                 LocalDate after,
                                                 LocalDate before) {
        logger.debug(LogUtil.getMethodName());
        List<Amount> amounts = getByCategoryAndProduct(category, product);

        BigDecimal sumAmounts = new BigDecimal("0");
        for (Amount amount : amounts) {

            LocalDate amountDate = amount.getLocalDate();
            if (amountDate.isAfter(after) && amountDate.isBefore(before)) {
                sumAmounts = sumAmounts.add(amount.getPrice());
            }
        }

        return sumAmounts;
    }

    /**
     * Функция возвращает список amount по категории и продукту с контролем пользователя
     *
     * @param categoryId категория
     * @param productId  группа товаров
     * @return List<Amount>
     */
    public List<Amount> getByCategoryAndProduct(Category categoryId, Product productId) {
        logger.debug(LogUtil.getMethodName());
        return amountRepository.findByCategoryIdAndProductIdAndUserId(categoryId,
                productId,
                securityService.findLoggedUser());
    }

    /**
     * Функция возвращает запись amount  с проверкой на соответствие текущему пользователю
     *
     * @param id id рассматриваемой сущности
     * @return Сущность БД
     */
    public Amount getOne(Integer id) throws AuthException, IllegalArgumentException {
        logger.debug(LogUtil.getMethodName());
        if (id == null) throw new IllegalArgumentException();

        Amount amount = amountRepository.findOne(id);
        if (amount == null) return null;

        if (!amount.getUserId().equals(securityService.findLoggedUser()))
            throw new AuthException(String.format("Запрошенный объект с id %d Вам не принадлежит.", id));

        return amount;
    }

    /**
     * Функция возвращает объект JsonResponse, содержащий результат поиска объекта и, при возможности, сам объект
     *
     * @param id id записи
     * @return JsonResponse
     */
    public JsonResponse getById(Integer id) {
        logger.debug(LogUtil.getMethodName());
        return utilService.getById(Amount.class, id);
    }

    /**
     * Функция сохраняет(обновляет) запись amount
     *
     * @param amount Сохраняемая сущность
     * @return ссылка на сохраненный объект
     */
    public Amount save(Amount amount) throws CrudException {
        logger.debug(LogUtil.getMethodName());

        Amount entity;

        try {
            entity = amountRepository.save(amount);

            //дополнительно после сохранения изменений в таблице amount проверяем лимиты
            User currentUser = securityService.findLoggedUser();
            mailService.checkLimit(currentUser, amount.getProductId());
            mailService.checkLimit(currentUser, amount.getCategoryId());
        } catch (Exception e) {
            throw new CrudException(ExceptionUtils.getRootCause(e).getMessage());
        }

        return entity;
    }

    /**
     * Функция удаления записи из БД
     *
     * @param amount Удаляемая сущность
     * @return ответ JsonResponse
     */
    public JsonResponse delete(Amount amount) throws CrudException {
        logger.debug(LogUtil.getMethodName());

        //в том случае, если удаляемая сущность привязана к другим как постоянный оборот, то сначала необходимо снять привязку
        //TODO: transaction;
        if (amount.getType() == 3) {
            for (Amount child : amountRepository.findByRegularId(amount)) {
                child.setRegularId(null);
                amountRepository.save(child);
            }
        }

        try {
            amountRepository.delete(amount.getId());
            return new JsonResponse(ResponseType.SUCCESS, "Удаление успешно завершено.");
        } catch (Exception e) {
            throw new CrudException(ExceptionUtils.getRootCause(e).getMessage());
        }
    }

    /**
     * Функция получения итоговой суммы по типу категорий за период (суммарных доход/расход)
     *
     * @param type   Тип категории (0 - доход, 0 - расход)
     * @param user   Обрабатываемый пользоваль
     * @param after  начальная дата отсечки
     * @param before конечная дата отсечки
     * @return сумма
     */
    public BigDecimal getSumByCategoryType(Byte type, User user, LocalDate after, LocalDate before) {
        logger.debug(LogUtil.getMethodName());

        BigDecimal sum = amountRepository.getSumByTypeAndUserIdAndDate(type,
                user,
                DateUtil.getDate(after),
                DateUtil.getDate(before));

        return sum == null ? new BigDecimal("0") : sum;
    }

    /**
     * Получение списка CalendarEntity для заполнения FullCalendar
     *
     * @param user   пользователь
     * @param after  начальная дата отсечки
     * @param before конечная дата отсечки
     * @return список CalendarEntity
     */
    public List<CalendarEntity> getCalendarEntities(User user,
                                                    LocalDate after,
                                                    LocalDate before,
                                                    Category category,
                                                    Product product) {
        logger.debug(LogUtil.getMethodName());

        List<CalendarEntity> calendarEntities;

        long countOfDays = after.until(before, ChronoUnit.DAYS);

        logger.debug(String.format("Интервал календаря: %s - %s. Кол-во дней в периоде: %d",
                after, before, countOfDays));

        calendarEntities = amountRepository.getCalendarData(
                user,
                DateUtil.getDate(after),
                DateUtil.getDate(before),
                category,
                product);

        //если период просмотра неделя, то добавляем нулевые даты
        if (countOfDays < 8) {
            logger.debug("Период просмотра - неделя, значит нам необходимо добавить даты с нулевой суммой");
            while (after.compareTo(before) <= 0) {
                logger.debug(String.format("Обрабатываем дату %s", after.toString()));

                CalendarEntity empty = new CalendarEntity("0", DateUtil.getFormattedForFullCalendar(after));
                if (!calendarEntities.contains(empty)) {
                    logger.debug("Данные за дату не найдены - создаем строку с нулевой суммой.");
                    calendarEntities.add(empty);
                }

                after = after.plusDays(1);
            }
        }

        logger.debug("Список данных календаря:");
        LogUtil.logList(logger, calendarEntities);

        return calendarEntities;
    }

    /**
     * Функция возвращает список постоянных оборотов (с типом 2)
     *
     * @param user рассматриваемый пользователь
     * @return список
     */
    public List<Amount> getAllRegular(User user) {
        return amountRepository.getAllRegular(user);
    }

    public List<Amount> getAvgOfPrevMonths(User user, Product product, Category category) {
        logger.debug(LogUtil.getMethodName());

        logger.debug(String.format("Диапазон дат: %s - %s", DateUtil.getDateOfStartOfEra().toString(),
                DateUtil.getDate(DateUtil.getStartOfMonth().minusDays(1)).toString()));

        return amountRepository.getAvgOfPrevMonths(
                user,
                product,
                category,
                DateUtil.getDateOfStartOfEra(),
                DateUtil.getDate(DateUtil.getStartOfMonth().minusDays(1)));
    }
}
