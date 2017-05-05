package ru.hd.olaf.mvc.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.util.LogUtil;

/**
 * Created by d.v.hozyashev on 05.05.2017.
 */
@Component
public class CategoryValidator implements Validator {

    @Autowired
    private CategoryService categoryService;

    private static final Logger logger = LoggerFactory.getLogger(CategoryValidator.class);

    public boolean supports(Class<?> aClass) {
        return Category.class.equals(aClass);
    }

    public void validate(Object o, Errors errors) {
        logger.debug(LogUtil.getMethodName());

        Category category = (Category) o;

        //валидация длины наименования
        if (category.getName().length() < 5 || category.getName().length() > 20) {
            logger.debug("Validation.category.size.name");
            errors.rejectValue("name", "Validation.category.size.name");
        }

        //валидация длины описания
        if (category.getDetails().length() > 50) {
            logger.debug("Validation.category.size.details");
            errors.rejectValue("details", "Validation.category.size.details");
        }

        //Валидация соответствия типов текущей записи и родительской
        if (category.getParentId() != null && category.getType() != category.getParentId().getType()) {
            logger.debug("Validation.category.type");
            errors.rejectValue("type", "Validation.category.type");
        }
    }
}
