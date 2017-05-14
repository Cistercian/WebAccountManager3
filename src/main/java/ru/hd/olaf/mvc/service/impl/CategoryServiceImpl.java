package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.mvc.repository.CategoryRepository;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.mvc.service.UtilService;
import ru.hd.olaf.util.DateUtil;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.ResponseType;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.JsonResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private AmountService amountService;
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
     * Функция возвращает коллекцию служебного класса BarEntity, элемент которой - дочерняя категория, указанной родительской
     * с итоговой по ней суммой за заданный период с исключением нулевых сумм.
     *
     * @param parent
     * @param after
     * @param before
     * @return
     */
    public List<BarEntity> getBarEntityOfSubCategories(Category parent, LocalDate after, LocalDate before) {
        logger.debug(LogUtil.getMethodName());
        logger.debug(String.format("Dates: after: %s, before %s", after, before));

        User user = securityService.findLoggedUser();
        List<BarEntity> bars = new ArrayList<BarEntity>();

        if (parent != null)
            bars = categoryRepository.getBarEntityByUserIdAndSubCategory(user,
                    parent,
                    DateUtil.getDate(after),
                    DateUtil.getDate(before));
        else {
            Map<Integer, BarEntity> map = new HashMap<Integer, BarEntity>();

            List<BarEntity> child = categoryRepository.getBarEntityOfParentsByUserId(user,
                    DateUtil.getDate(after),
                    DateUtil.getDate(before));

            logger.debug("Промежуточный список категорий:");
            LogUtil.logList(logger, child);

            for (BarEntity entity : child) {
                BarEntity bar = getParentBar(entity);

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

    private BarEntity getParentBar(BarEntity entity) {
        logger.debug(LogUtil.getMethodName());

        if ("child".equals(entity.getType())){
            //TODO: refactoring!
            try {
                Category parent = getOne(entity.getId()).getParentId();

                entity.setId(parent.getId());
                entity.setName(parent.getName());
                String type = parent.getParentId() == null ?
                        parent.getType() == 0 ? "CategoryIncome" : "CategoryExpense" :
                        "child";
                entity.setType(type);

                if ("child".equals(entity.getType()))
                    entity = getParentBar(entity);

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
                    "один или несколько записей таблицы amounts ");
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
}
