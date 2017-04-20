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
    public ModelAndView getPageCategory(){
        logger.debug(String.format("Function %s", "getPageCategory()"));

        ModelAndView modelAndView = new ModelAndView("/data/page-category");
        modelAndView.addObject("categories", categoryService.getIdAndNameByCurrentUser());

        return modelAndView;
    }

    @RequestMapping(value = "/page-category/{id}", method = RequestMethod.GET)
    public ModelAndView getPageCategoryById(@PathVariable("id") Integer id){
        logger.debug(String.format("Function %s", "getPageCategoryById()"));

        ModelAndView modelAndView = new ModelAndView("/data/page-category");

        modelAndView.addObject("categories", categoryService.getIdAndNameByCurrentUser());

        try {
            Category category = categoryService.getById(id);

            modelAndView.addObject("parentId", category.getParentId());
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

        } catch (Exception e) {
            logger.debug(String.format("Cant load category by id. Reason: %s", e.getMessage()));
        }

        return modelAndView;
    }

    @RequestMapping(value = "/page-category/save", method = RequestMethod.POST)
    public String saveCategory(@RequestParam(value = "id", required = false) Integer id,
                               @RequestParam(value = "parentId") Integer parentId,
                               @RequestParam(value = "name") String name,
                               @RequestParam(value = "type") Byte type,
                               @RequestParam(value = "details") String details){
        logger.debug(String.format("Function %s", "saveCategory()"));
        parentId = parentId == null ? 0 : parentId;
        logger.debug(String.format("Params: " +
                "id: %s," +
                "parentId: %s," +
                "name: %s," +
                "type: %s," +
                "details: %s", id.toString(), parentId.toString(), name, type.toString(), details));

        Category category;
        if (id == null){
            logger.debug(String.format("RequestParam 'id' is not found, creating new Category"));
            category = new Category();
        } else {
            logger.debug(String.format("RequestParam 'id'=%d", id));
            category = categoryService.getById(id);
            logger.debug(String.format("Category=%s", category));
            //TODO: security and NPE;
        }

        category.setName(name);
        category.setParentId(categoryService.getById(parentId));
        category.setType(type);
        category.setDetails(details);
        category.setUserId(securityService.findLoggedUser());

        categoryService.add(category);

        return "index";
    }

    @RequestMapping(value = "/page-category/delete",method = RequestMethod.POST)
    public @ResponseBody JsonAnswer deleteCategory(@RequestParam(value = "id") Integer id) {
        logger.debug(String.format("Function %s", "deleteCategory()"));

        JsonAnswer jsonAnswer = new JsonAnswer();

        //TODO: check available Amount
        if (amountService.getByCategory(categoryService.getById(id)).size() > 0) {
            logger.debug(String.format("Deleting is aborted. Found available Amounts!"));

            jsonAnswer.setType(AnswerType.ERROR);
            jsonAnswer.setMessage("Deleting is aborted. Found available Amounts!");

            return jsonAnswer;
        }

        logger.debug(String.format("Category for deleting: %s", categoryService.getById(id)));

        String result = categoryService.delete(id);
        jsonAnswer.setType(AnswerType.SUCCESS);
        jsonAnswer.setMessage(result);

        return jsonAnswer;
    }
}
