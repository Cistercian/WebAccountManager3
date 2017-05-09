package ru.hd.olaf.mvc.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.hd.olaf.entities.Notification;
import ru.hd.olaf.mvc.service.NotificationService;

import java.math.BigDecimal;

/**
 * Created by Olaf on 09.05.2017.
 */
@Component
public class NotificationValidator implements Validator {

    @Autowired
    private NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(NotificationValidator.class);

    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(Notification.class);
    }

    public void validate(Object o, Errors errors) {
        Notification notification = (Notification) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "NotEmpty");
        if (!"category".equalsIgnoreCase(notification.getType()) || !"product".equalsIgnoreCase(notification.getType())) {
            logger.debug("Validation.notification.type");
            errors.rejectValue("type", "Validation.notification.type");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "entityId", "NotEmpty");
        if (notification.getEntityId() != null && notification.getEntityId() <= 0) {
            logger.debug("Validation.notification.entityId");
            errors.rejectValue("entityId", "Validation.notification.entityId");
        }

        if (notification.getSum() != null && notification.getSum().compareTo(new BigDecimal("0")) < 0) {
            logger.debug("Validation.notification.sum");
            errors.rejectValue("entityId", "Validation.notification.sum");
        }

        if (notification.getPeriod() != null && (notification.getPeriod() < 0 || notification.getPeriod() > 2)){
            logger.debug("Validation.notification.period");
            errors.rejectValue("entityId", "Validation.notification.period");
        }
    }
}
