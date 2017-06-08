package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
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

        return categoryRepository.findByUserId(securityService.findLoggedUser());
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

    private DbData getParentBar(DbData entity, Category rootCategory) {
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
    public List<BarEntity> getAnalyticData(User user,
                                           Category category,
                                           LocalDate after,
                                           LocalDate before) {
        logger.debug(LogUtil.getMethodName());

        after = DateUtil.getStartOfEra();
        before = DateUtil.getStartOfMonth().minusDays(1);

        List<AnalyticData> list = categoryRepository.getAnalyticDataByCategory(
                user,
                category,
                DateUtil.getDate(after),
                DateUtil.getDate(before)
        );
        //если запрос по определенной категории, то необходимо отдельно собрать данные по группам
        if (category != null)
            list.addAll(categoryRepository.getAnalyticDataByProduct(
                    user,
                    category,
                    DateUtil.getDate(after),
                    DateUtil.getDate(before)
            ));

        logger.debug("Список анализируемых данных:");
        LogUtil.logList(logger, list);

        logger.debug("Сбор данных по оборотам в текущем месяце.");
        //TODO: возможна ситуация, когда оборота в средних данных нет, а в текущем месяце информация есть.
        for (AnalyticData entity : list) {
            if (entity.getType().startsWith("Category")) {
                JsonResponse response = utilService.getById(Category.class, entity.getId());
                if (response.getType() == ResponseType.SUCCESS) {
                    Category category1 = (Category) response.getEntity();
                    entity.setCurrentSum(categoryRepository.getCategorySum(
                            user,
                            category1,
                            DateUtil.getDate(DateUtil.getStartOfMonth()),
                            DateUtil.getDate(LocalDate.now())
                    ));
                }
            } else {
                JsonResponse response = utilService.getById(Product.class, entity.getId());
                if (response.getType() == ResponseType.SUCCESS) {
                    Product product = (Product) response.getEntity();
                    entity.setCurrentSum(categoryRepository.getProductSum(
                            user,
                            category,
                            product,
                            DateUtil.getDate(DateUtil.getStartOfMonth()),
                            DateUtil.getDate(LocalDate.now())
                    ));
                }
            }
        }
        logger.debug("Список данных с суммами за текущий месяц:");
        LogUtil.logList(logger, list);

        Map<String, AnalyticData> map = new HashMap<String, AnalyticData>();

        logger.debug("Агрегируем дочерние категории.");
        for (AnalyticData entity : list) {
            entity = (AnalyticData) getParentBar(entity, category);
            String id = (entity.getType().startsWith("Category") ? "Category" : "Product") + entity.getId();

            if (map.containsKey(id)) {

                AnalyticData dataMap = map.get(id);

                if (entity.getMaxDate().compareTo(dataMap.getMaxDate()) > 0)
                    dataMap.setMaxDate(entity.getMaxDate());

                if (entity.getMinDate().compareTo(dataMap.getMinDate()) > 0)
                    dataMap.setMinDate(entity.getMinDate());

                dataMap.setAvgSum(dataMap.getAvgSum().add(entity.getAvgSum()));
                dataMap.setCurrentSum(dataMap.getCurrentSum().add(entity.getCurrentSum()));

                map.put(id, dataMap);
            } else {
                map.put(id, entity);
            }
        }

        List<BarEntity> bars = new ArrayList<BarEntity>();
        logger.debug("Формируем данные для отправки на страницу.");
        for (AnalyticData data : map.values()){
            long distance = DateUtil.getParsedDate(data.getMinDate().toString()).until(
                    DateUtil.getParsedDate(data.getMaxDate().toString()), ChronoUnit.MONTHS
            );
            distance = distance == 0 ? 1 : distance;

            BigDecimal avgSum = data.getAvgSum().divide(new BigDecimal(distance), 2, BigDecimal.ROUND_HALF_UP);

            bars.add(new BarEntity(data.getType(), data.getId(), data.getCurrentSum(), data.getName(), avgSum));
        }

        bars = utilService.sortListByTypeAndSum(bars);

        logger.debug("Конечный список:");
        LogUtil.logList(logger, bars);

        return bars;
    }
}
