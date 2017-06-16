package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.repository.CategoryRepository;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.mvc.service.UtilService;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by Olaf on 13.04.2017.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UtilService utilService;

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    /**
     * Функция возвращает список category  с проверкой на текущего пользователя
     *
     * @return
     */
    public List<Category> getAll() {
        logger.debug(LogUtil.getMethodName());

        return getAllByCurrentUser();
    }

    /**
     * Функция возвращает список Category по текущему пользователю
     *
     * @return
     */
    public List<Category> getAllByCurrentUser() {
        logger.debug(LogUtil.getMethodName());

        List<Category> categories = categoryRepository.findByUserId(securityService.findLoggedUser());

        Collections.sort(categories, new Comparator<Category>() {
            public int compare(Category o1, Category o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        return categories;
    }

    /**
     * Функция возвращает список категорий, у которых установлена переданная родительская
     *
     * @param parent      Родительская категория
     * @param currentUser рассматриваемый пользователь
     * @return List
     */
    public List<Category> getByParent(Category parent, User currentUser) {
        logger.debug(LogUtil.getMethodName());

        return categoryRepository.findByParentIdAndUserId(parent, currentUser);
    }

    /**
     * Функция возвращает коллекцию служебного класса BarEntity, элемент которой - дочерняя категория, указанной родительской
     * с итоговой по ней суммой за заданный период с исключением нулевых сумм.
     *
     * @param parent
     * @param after
     * @param before
     * @return
     */
    public List<BarEntity> getBarEntityOfSubCategories(User user,
                                                       Category parent,
                                                       LocalDate after,
                                                       LocalDate before) {
        logger.debug(LogUtil.getMethodName());
        logger.debug(String.format("Dates: after: %s, before %s", after, before));

        List<BarEntity> bars;

        if (parent != null) {
            bars = categoryRepository.getBarEntityByUserIdAndSubCategory(
                    user,
                    parent,
                    DateUtil.getDate(after),
                    DateUtil.getDate(before));
        } else {
            Map<Integer, BarEntity> map = new HashMap<Integer, BarEntity>();

            List<BarEntity> child;

            child = categoryRepository.getBarEntityOfParentsByUserId(
                    user,
                    DateUtil.getDate(after),
                    DateUtil.getDate(before));


            logger.debug("Промежуточный список категорий:");
            LogUtil.logList(logger, child);

            for (BarEntity entity : child) {
                BarEntity bar = (BarEntity) getParentBar(entity, null);

                if (map.containsKey(bar.getId())) {

                    BarEntity barMap = map.get(bar.getId());
                    barMap.setSum(barMap.getSum().add(bar.getSum()));
                    map.put(bar.getId(), barMap);

                } else {
                    map.put(bar.getId(), bar);
                }
            }

            bars = Lists.newArrayList(map.values());
        }
        return bars;
    }

    private DBData getParentBar(DBData entity, Category rootCategory) {
        logger.debug(LogUtil.getMethodName());

        if (entity.getType().endsWith("Child")) {
            //TODO: refactoring!
            try {
                Category parent = getOne(entity.getId()).getParentId();

                String type = parent.getParentId() == null ?
                        parent.getType() == 0 ? "CategoryIncome" : "CategoryExpense" :
                        "CategoryChild";

                entity.setType(type);

                if (rootCategory != null && parent.getId() == rootCategory.getId())
                    return entity;

                entity.setId(parent.getId());
                entity.setName(parent.getName());

                if ("CategoryChild".equals(entity.getType()))
                    entity = getParentBar(entity, rootCategory);

            } catch (AuthException e) {
                logger.debug(String.format("Логическая ошибка БД - не найдена родительская категория дочерней с id = %d",
                        entity.getId()));
            }
        }

        return entity;
    }

    /**
     * Функция возвращает список дочерних категорий
     *
     * @param parentId
     * @return
     */
    private List<Category> getByParentId(Category parentId) {
        logger.debug(LogUtil.getMethodName());
        List<Category> categories =
                categoryRepository.findByParentIdAndUserId(parentId, securityService.findLoggedUser());

        return categories;
    }

    /**
     * Функция возвращения записи category с проверкой на текущего пользователя
     *
     * @param id
     * @return
     */
    public Category getOne(Integer id) throws AuthException, IllegalArgumentException {
        logger.debug(LogUtil.getMethodName());
        if (id == null) {
            logger.debug("Переданный id = null.");
            throw new IllegalArgumentException();
        }

        Category category = categoryRepository.findOne(id);
        if (category == null) {
            logger.debug(String.format("Запись с id = %d не найдена", id));
            return null;
        }

        if (!category.getUserId().equals(securityService.findLoggedUser())) {
            String message = String.format("Запрошенный объект с id %d Вам не принадлежит.", id);
            logger.debug(message);
            throw new AuthException(message);
        }

        return category;
    }

    /**
     * Функция возвращает объект JsonResponse, содержащий результат поиска объекта и, при возможности, сам объект
     *
     * @param id id записи
     * @return JsonResponse
     */
    public JsonResponse getById(Integer id) {
        logger.debug(LogUtil.getMethodName());
        return utilService.getById(Category.class, id);
    }

    /**
     * Функция создания/обновления записи
     *
     * @param category
     * @return
     */
    public Category save(Category category) throws CrudException {
        logger.debug(LogUtil.getMethodName());

        Category entity;

        try {
            entity = categoryRepository.save(category);
        } catch (Exception e) {
            throw new CrudException(ExceptionUtils.getRootCause(e).getMessage());
        }

        return entity;
    }

    /**
     * Функция удаления записи Category
     *
     * @param category
     * @return
     */
    public JsonResponse delete(Category category) throws CrudException {
        logger.debug(LogUtil.getMethodName());

        //Проверка существуют ли записи amount с данной категорией
        if (category.getAmounts().size() > 0) {
            String message = String.format("Удаление невозможно: к данной категории привязаны " +
                    "один или несколько оборотов ");
            logger.debug(message);

            throw new CrudException(message);
        }
        if (getByParentId(category).size() > 0) {
            String message = String.format("Удаление невозможно: данная запись числится " +
                    "родительской для других категорий");
            logger.debug(message);

            throw new CrudException(message);
        }

        try {
            categoryRepository.delete(category);
            return new JsonResponse(ResponseType.SUCCESS, "Удаление успешно завершено.");
        } catch (Exception e) {
            throw new CrudException(ExceptionUtils.getRootCause(e).getMessage());
        }
    }

    /**
     * Функция получения аналитических данных (средние расходы и доходы)
     * @param user Текущий пользователь
     * @param category Текущая рассматриваемая категория
     * @param after Дата начала периода
     * @param before Дата окночания периода
     * @return Список баров
     */
    public List<AnalyticEntity> getAnalyticData(User user,
                                        Category category,
                                        LocalDate after,
                                        LocalDate before) {
        logger.debug(LogUtil.getMethodName());

        Date beginOfEra = DateUtil.getDateOfStartOfEra();
        before = DateUtil.getStartOfMonth().minusDays(1);

        List<AnalyticEntity> list = categoryRepository.getAnalyticDataByCategory(
                user,
                category,
                beginOfEra,
                DateUtil.getDate(before),
                true,   //isNeedAvgSum
                false,  //isNeedRealSum
                false  //isNeedOneTimeSum
        );
        //если запрос по определенной категории, то необходимо отдельно собрать данные по группам
        if (category != null)
            list.addAll(categoryRepository.getAnalyticDataByProduct(
                    user,
                    category,
                    beginOfEra,
                    DateUtil.getDate(before),
                    true,
                    false,
                    false
            ));

        logger.debug("Список анализируемых данных со средними ценами:");
        LogUtil.logList(logger, list);

        logger.debug("Сбор данных по оборотам в текущем месяце.");
        Date beginOfMonth = DateUtil.getDate(DateUtil.getStartOfMonth());
        Date today = DateUtil.getDate(LocalDate.now());
        List<AnalyticEntity> listThisMonth = categoryRepository.getAnalyticDataByCategory(
                user,
                category,
                beginOfMonth,
                today,
                false,  //isNeedAvgSum
                true,   //isNeedRealSum
                false   //isNeedOneTimeSum
        );
        logger.debug("Список со ценами за текущий месяц:");
        LogUtil.logList(logger, listThisMonth);

        if (category != null)
            listThisMonth.addAll(categoryRepository.getAnalyticDataByProduct(
                    user,
                    category,
                    beginOfMonth,
                    today,
                    false,
                    true,
                    false
            ));

        for (AnalyticEntity entity : listThisMonth){
            if (list.contains(entity)){
                int index = list.indexOf(entity);
                AnalyticEntity data = list.get(index);

                data.setCurrentSum(entity.getAvgSum());
                list.set(index, data);
            } else {
                logger.debug(String.format("Найдены обороты, отсутствующие в статистике за прошлые месяцы: %s", entity.toString()));

                entity.setCurrentSum(entity.getAvgSum());
                entity.setAvgSum(new BigDecimal("0"));

                list.add(entity);
            }
        }

        logger.debug("Список данных с суммами за текущий месяц:");
        LogUtil.logList(logger, list);

        //агрегация данных по суммам, которые следует исключить из расчета среднестатистических (разовые обороты)
        listThisMonth = categoryRepository.getAnalyticDataByCategory(
                user,
                category,
                beginOfMonth,
                today,
                false,  //isNeedAvgSum
                false,  //isNeedRealSum
                true    //isNeedOneTimeSum
        );

        logger.debug("Список с единоразовыми оборотами:");
        LogUtil.logList(logger, listThisMonth);

        if (category != null)
            listThisMonth.addAll(categoryRepository.getAnalyticDataByProduct(
                    user,
                    category,
                    beginOfMonth,
                    today,
                    false,
                    false,
                    true
            ));
        for (AnalyticEntity entity : listThisMonth){
            if (list.contains(entity)){
                int index = list.indexOf(entity);
                AnalyticEntity data = list.get(index);

                data.setOneTimeSum(entity.getAvgSum());
                list.set(index, data);
            } else {
                logger.debug(String.format("Логичекая ошибка - найдены найдены единоразовые суммы, которые следует исключить из средних оборотов, но их там уже нет: %s",
                        entity.toString()));
            }
        }
        //учет обязательных (периодических) оборотов
        listThisMonth = categoryRepository.getAnalyticDataOfRegularByCategory(
                user,
                category,
                beginOfMonth,
                today
        );


        if (category != null)
            //TODO: redudant?
            listThisMonth.addAll(categoryRepository.getAnalyticDataOfRegularByProduct(
                    user,
                    category,
                    beginOfMonth,
                    today
            ));

        logger.debug("Список с обязательными оборотами:");
        LogUtil.logList(logger, listThisMonth);

        for (AnalyticEntity entity : listThisMonth){
            if (list.contains(entity)){
                int index = list.indexOf(entity);
                AnalyticEntity data = list.get(index);

                data.setRegularSum(entity.getAvgSum());
                list.set(index, data);
            } else {
                logger.debug(String.format("Найдены обязательные неисполненные обороты: %s",
                        entity.toString()));

                entity.setRegularSum(entity.getAvgSum());
                entity.setAvgSum(new BigDecimal("0"));

                list.add(entity);
            }
        }

        Map<String, AnalyticEntity> map = new HashMap<String, AnalyticEntity>();

        logger.debug("Агрегируем дочерние категории.");
        for (AnalyticEntity entity : list) {
            entity = (AnalyticEntity) getParentBar(entity, category);
            String id = (entity.getType().startsWith("Category") ? "Category" : "Product") + entity.getId();

            if (map.containsKey(id)) {

                AnalyticEntity dataMap = map.get(id);

                if (entity.getMaxDate().compareTo(dataMap.getMaxDate()) > 0)
                    dataMap.setMaxDate(entity.getMaxDate());

                if (entity.getMinDate().compareTo(dataMap.getMinDate()) > 0)
                    dataMap.setMinDate(entity.getMinDate());

                dataMap.setAvgSum(dataMap.getAvgSum().add(entity.getAvgSum()));
                dataMap.setCurrentSum(dataMap.getCurrentSum().add(entity.getCurrentSum()));
                dataMap.setOneTimeSum(dataMap.getOneTimeSum().add(entity.getOneTimeSum()));
                dataMap.setRegularSum(dataMap.getRegularSum().add(entity.getRegularSum()));

                map.put(id, dataMap);
            } else {
                map.put(id, entity);
            }
        }

        logger.debug("Формируем данные для отправки на страницу.");
        list = new ArrayList<AnalyticEntity>();
        for (AnalyticEntity data : map.values()){
            long distance = DateUtil.getParsedDate(data.getMinDate().toString()).until(
                    DateUtil.getParsedDate(data.getMaxDate().toString()), ChronoUnit.MONTHS
            ) + 1;

            BigDecimal avgSum = data.getAvgSum()
                    .divide(new BigDecimal(distance), 2, BigDecimal.ROUND_HALF_UP);

            data.setAvgSum(avgSum);
            list.add(data);

            logger.debug(String.format("Расчет среднего значения для %s: Общая сумма %s, кол-во месяцев, на которое делим: %s, " +
                    "сумма единоразовых оборотов: %s, ИТОГО: %s", (data.getType().startsWith("Category") ? "категории " : "группы ") + data.getName(),
                    data.getAvgSum(), distance, data.getOneTimeSum(), avgSum));
        }

        list = utilService.sortListByTypeAndSum(list);

        logger.debug("Конечный список:");
        LogUtil.logList(logger, list);

        return list;
    }
}
