package ru.hd.olaf.mvc.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.util.LogUtil;

/**
 * Created by Olaf on 07.05.2017.
 */
@Component
public class ProductValidator implements Validator {

    @Autowired
    private ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(CategoryValidator.class);

    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(Product.class);
    }

    public void validate(Object o, Errors errors) {
        logger.debug(LogUtil.getMethodName());

        Product product = (Product) o;

        //валидация длины наименования
        if (product.getName().length() < 3 || product.getName().length() > 40) {
            logger.debug("Validation.size.name");
            errors.rejectValue("name", "Validation.size.name");
        }
    }
}
