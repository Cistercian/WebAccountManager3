package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.util.json.ResponseType;
import ru.hd.olaf.util.json.JsonResponse;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.SecurityService;

/**
 * Created by d.v.hozyashev on 20.04.2017.
 */
@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AmountService amountService;
    @Autowired
    private SecurityService securityService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/page-category", method = RequestMethod.GET)
    public ModelAndView getViewPageCategory(){
        logger.debug(String.format("Function %s", "getViewPageCategory()"));

        return getViewPageCategoryById(null);
    }

    //TODO: refactoring to abstract service?
    @RequestMapping(value = "/page-category/{id}", method = RequestMethod.GET)
    public ModelAndView getViewPageCategoryById(@PathVariable("id") Integer id){
        logger.debug(String.format("Function %s", "getViewPageCategoryById()"));

        ModelAndView modelAndView = new ModelAndView("/data/page-category");
        modelAndView.addObject("categories", categoryService.getAll());

        Category category;

        try {
            category = categoryService.getById(id);
            if (category != null){

                modelAndView.addObject("id", category.getId());
                modelAndView.addObject("name", category.getName());
                modelAndView.addObject("details", category.getDetails());

                Category parent = category.getParentId();
                if (parent != null) {
                    modelAndView.addObject("parentId", parent.getId());
                    modelAndView.addObject("parentName", parent.getName());
                } else
                    modelAndView.addObject("parentName", "Отсутствует");

                if (category.getType() == 0)
                    modelAndView.addObject("typeIncome", "true");

                logger.debug(String.format("Find category by id: %s", category));
                logger.debug(String.format("Parent: %s", parent));

            } else {
                String message = String.format("Запрошеный объект с id %d не найден", id);
                modelAndView.addObject("response", message);

                logger.debug(message);
            }
        } catch (AuthException e) {
            modelAndView.addObject("response", e.getMessage());

            logger.error(e.getMessage());
        } catch (IllegalArgumentException e) {
            String message = String.format("Запрошенный объект с id %d не найден. \n" +
                    "Error message: %s", id, e.getMessage());

            logger.debug(message);
        }

        return modelAndView;
    }

    /**
     * Функция сохранения/обновления записи Category
     * @param id
     * @param parentId
     * @param name
     * @param type
     * @param details
     * @return
     */
    @RequestMapping(value = "/page-category/save", method = RequestMethod.POST)
    public @ResponseBody JsonResponse saveCategory(@RequestParam(value = "id", required = false) Integer id,
                               @RequestParam(value = "parentId") Integer parentId,
                               @RequestParam(value = "name") String name,
                               @RequestParam(value = "type") Byte type,
                               @RequestParam(value = "details") String details){
        logger.debug(String.format("Function %s", "saveCategory()"));

        logger.debug(String.format("Params: " +
                "id: %s," +
                "parentId: %s," +
                "name: %s," +
                "type: %s," +
                "details: %s", id, parentId, name, type, details));

        //TODO: refactoring to JsonResponse
        Category category;

        try {
            category = categoryService.getById(id);
        } catch (AuthException e) {
            logger.debug(e.getMessage());

            return new JsonResponse(ResponseType.ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            //так здесь может быть операция создания
            category = null;
        }

        if (category == null){
            logger.debug(String.format("Not found Category, id: %d", id));
            category = new Category();
        } else {
            logger.debug(String.format("Category=%s", category));
        }

        category.setName(name);

        try {
            Category parent = categoryService.getById(parentId);
            if (parent != null) {
                category.setParentId(parent);
            }
        } catch (AuthException e) {
            logger.debug("Ошибка обработки родительской категории." + e.getMessage());

            return new JsonResponse(ResponseType.ERROR, "Ошибка обработки родительской категории." + e.getMessage());
        }  catch (IllegalArgumentException e) {
            logger.debug("Родительская категория = null");
        }

        category.setType(type);
        category.setDetails(details);
        category.setUserId(securityService.findLoggedUser());

        try {
            categoryService.save(category);
        } catch (CrudException e) {
            String message = String.format("Возникла ошибка при сохранении данных в БД. \n" +
                    "Error message: %s", e.getMessage());
            logger.error(message);

            return new JsonResponse(ResponseType.ERROR, message);
        }

        String message = String.format("Запись успешно сохранена в БД.");
        logger.debug(message + "\n" + category);

        return new JsonResponse(ResponseType.SUCCESS, message);
    }

    /**
     * Функция удаления записи category
     * @param id
     * @return
     */
    @RequestMapping(value = "/page-category/delete",method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse deleteCategory(@RequestParam(value = "id") Integer id) {
        logger.debug(String.format("Function %s", "deleteCategory()"));

        Category category = null;
        try {
            category = categoryService.getById(id);
        } catch (AuthException e) {
            return new JsonResponse(ResponseType.ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            return new JsonResponse(ResponseType.ERROR, String.format("Ошибка: передан пустой параметр id"));
        }

        JsonResponse response = new JsonResponse();
        if (category != null) {

            logger.debug(String.format("Delete category: %s", category));

            try {
                response = categoryService.delete(category);
            } catch (CrudException e) {
                String message = String.format("Произошла неизвестная ошибка: %s", e.getCause());
                response.setType(ResponseType.ERROR);
                response.setMessage(message);
            }

        } else {
            response.setType(ResponseType.ERROR);
            response.setMessage(String.format("Отмена операции: запись с id %d не найдена!", id));
        }

        logger.debug(String.format("Result: %s", response.getMessage()));

        return response;
    }
}
