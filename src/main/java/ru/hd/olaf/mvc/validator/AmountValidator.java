package ru.hd.olaf.mvc.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.util.LogUtil;

import java.math.BigDecimal;

/**
 * Created by Olaf on 07.05.2017.
 */
@Component
public class AmountValidator implements Validator {

    @Autowired
    private AmountService amountService;
    @Autowired
    private CategoryService categoryService;

    private static final Logger logger = LoggerFactory.getLogger(AmountValidator.class);

    public boolean supports(Class<?> aClass) {
        return Amount.class.isAssignableFrom(aClass);
    }

    public void validate(Object o, Errors errors) {
        logger.debug(LogUtil.getMethodName());

        Amount amount = (Amount) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "date", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "productId", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "categoryId", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "NotEmpty");

        //валидация длины наименования
        if (amount.getName().length() < 3 || amount.getName().length() > 40) {
            logger.debug("Validation.size.name");
            errors.rejectValue("name", "Validation.size.name");
        }

        //валидация длины описания
        if (amount.getDetails().length() > 255) {
            logger.debug("Validation.size.details");
            errors.rejectValue("details", "Validation.size.details");
        }

        //валидация суммы
        if (amount.getPrice() != null && amount.getPrice().compareTo(new BigDecimal("0")) < 0) {
            logger.debug("Validation.amount.price");
            errors.rejectValue("price", "Validation.amount.price");
        }

        //валидация привязанного обязательного оборота
        if (amount.getRegularId() != null && amount.getCategoryId() != null &&
                amount.getCategoryId().getType() != amount.getRegularId().getCategoryId().getType()) {
            logger.debug("Validation.amount.periodical.categoryType");
            errors.rejectValue("regularId", "Validation.amount.periodical.categoryType");
        }
    }
}
