package ru.hd.olaf.mvc.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.hd.olaf.entities.Limit;

import java.math.BigDecimal;

/**
 * Created by Olaf on 09.05.2017.
 */
@Component
public class LimitValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(LimitValidator.class);

    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(Limit.class);
    }

    public void validate(Object o, Errors errors) {
        Limit limit = (Limit) o;

        if (limit.getType() == null ||
                (!"category".equalsIgnoreCase(limit.getType()) && !"product".equalsIgnoreCase(limit.getType()))) {
            logger.debug("Validation.limit.type");
            errors.rejectValue("type", "Validation.limit.type");
        }

        if (limit.getEntityId() == null ||
                (limit.getEntityId() != null && limit.getEntityId() <= 0)) {
            logger.debug("Validation.limit.entityId");
            errors.rejectValue("entityId", "Validation.limit.entityId");
        }

        if (limit.getSum() == null ||
                (limit.getSum() != null && limit.getSum().compareTo(new BigDecimal("0")) < 0)) {
            logger.debug("Validation.limit.sum");
            errors.rejectValue("entityId", "Validation.limit.sum");
        }

        if (limit.getPeriod() == null ||
                (limit.getPeriod() != null && (limit.getPeriod() < 0 || limit.getPeriod() > 2))){
            logger.debug("Validation.limit.period");
            errors.rejectValue("entityId", "Validation.limit.period");
        }
    }
}
