package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.util.json.AnswerType;
import ru.hd.olaf.util.json.JsonAnswer;
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

    @RequestMapping(value = "/page-category/{id}", method = RequestMethod.GET)
    public ModelAndView getViewPageCategoryById(@PathVariable("id") Integer id){
        logger.debug(String.format("Function %s", "getViewPageCategoryById()"));

        ModelAndView modelAndView = new ModelAndView("/data/page-category");
        modelAndView.addObject("categories", categoryService.getAll());

        Category category = categoryService.getById(id);
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

        } else
            logger.debug(String.format("Cant load category by id: %d", id));

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
    public String saveCategory(@RequestParam(value = "id", required = false) Integer id,
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

        //TODO: refactoring to JsonAnswer
        Category category = categoryService.getById(id);
        if (category == null){
            logger.debug(String.format("Not found Category, id: %d", id));
            category = new Category();
        } else {
            logger.debug(String.format("Category=%s", category));
        }

        category.setName(name);

        Category parent = categoryService.getById(parentId);
        if (parent != null) {
            category.setParentId(parent);
        }

        category.setType(type);
        category.setDetails(details);
        category.setUserId(securityService.findLoggedUser());

        categoryService.save(category);

        return "index";
    }

    /**
     * Функция удаления записи category
     * @param id
     * @return
     */
    @RequestMapping(value = "/page-category/delete",method = RequestMethod.POST)
    public @ResponseBody JsonAnswer deleteCategory(@RequestParam(value = "id") Integer id) {
        logger.debug(String.format("Function %s", "deleteCategory()"));

        Category category = categoryService.getById(id);
        JsonAnswer response = null;
        if (category != null) {
            response = categoryService.delete(category);

            logger.debug(String.format("Result: %s", response.getMessage()));
        } else {
            logger.debug(String.format("Not found Category, id: %d", id));

            response.setType(AnswerType.ERROR);
            response.setMessage(String.format("Not found Category, id: %d", id));
        }

        return response;
    }
}
