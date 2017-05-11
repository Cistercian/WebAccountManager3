package ru.hd.olaf.mvc.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.hd.olaf.entities.Limit;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.mvc.service.CategoryService;
import ru.hd.olaf.mvc.service.LimitService;
import ru.hd.olaf.mvc.service.ProductService;
import ru.hd.olaf.util.json.JsonResponse;

import java.math.BigDecimal;

/**
 * Created by Olaf on 09.05.2017.
 */
@Component
public class LimitValidator implements Validator {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private LimitService limitService;

    private static final Logger logger = LoggerFactory.getLogger(LimitValidator.class);

    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(Limit.class);
    }

    public void validate(Object o, Errors errors) {
        Limit limit = (Limit) o;

        if (limit.getType() == null)
            setError("type", "Validation.limit.type", errors);
        else {
            JsonResponse response = new JsonResponse();
            if ("category".equalsIgnoreCase(limit.getType())) {
                try {
                    response = categoryService.getById(limit.getEntityId());
                } finally {
                    if (response.getEntity() == null)
                        setError("entityId", "Validation.limit.entityId", errors);
                }
            } else if ("product".equalsIgnoreCase(limit.getType())) {
                try {
                    response = productService.getById(limit.getEntityId());
                } finally {
                    if (response.getEntity() == null)
                        setError("entityId", "Validation.limit.entityId", errors);
                }
            }
        }

        if (limit.getSum() == null)
            setError("sum", "Validation.limit.sum", errors);
        else if (limit.getSum().compareTo(new BigDecimal("0")) < 0)
            setError("sum", "Validation.limit.sum", errors);

        if (limit.getPeriod() == null)
            setError("period", "Validation.limit.period", errors);
        else if (limit.getPeriod() < 0 || limit.getPeriod() > 2){
            setError("period", "Validation.limit.period", errors);
        }

        //проверка на дубликат по полям period и categoryId/productId
        Limit duplicate = null;
        if (limit.getCategoryId() != null)
            duplicate = limitService.getByPeriodAndEntity(limit.getPeriod(), limit.getCategoryId());
        else if (limit.getProductId() != null)
            duplicate = limitService.getByPeriodAndEntity(limit.getPeriod(), limit.getProductId());

        if (duplicate != null && duplicate.getId() != limit.getId())
            setError("entityId", "Validation.limit.duplicate", errors);

    }

    private void setError(String elem, String message, Errors errors) {
        logger.debug(message);
        errors.rejectValue(elem, message);
    }
}
