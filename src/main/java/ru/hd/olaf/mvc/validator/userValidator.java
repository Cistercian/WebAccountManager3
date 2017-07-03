package ru.hd.olaf.mvc.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.service.UserService;
import ru.hd.olaf.util.LogUtil;

/**
 * Created by d.v.hozyashev on 18.04.2017.
 */
@Component
public class UserValidator implements Validator {
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserValidator.class);

    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    public void validate(Object o, Errors errors) {
        logger.debug(LogUtil.getMethodName());

        User user = (User) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.getUsername().length() < 2 || user.getUsername().length() > 32) {
            logger.debug("Size.userForm.username");
            errors.rejectValue("username", "Size.userForm.username");
        }

        //игнорируем проверку на совпадение имен пользователей, если был передан passwordOld
        if (user.getPasswordOld() == null && userService.findByUsername(user.getUsername()) != null) {
            logger.debug("Duplicate.userForm.username");
            errors.rejectValue("username", "Duplicate.userForm.username");
        }

        //загрушка - при смене пароля не проверяем его длину
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.getPasswordOld() == null && user.getPassword().length() < 6 || user.getPassword().length() > 32) {
            logger.debug("Size.userForm.password");
            errors.rejectValue("password", "Size.userForm.password");
        }

        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            logger.debug("Diff.userForm.passwordConfirm");
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }

        if (!(user.getPasswordOld() == null) &&
                !userService.isPasswordMatches(user.getPasswordOld(), userService.findByUsername(user.getUsername()))) {
            logger.debug("Wrong.userForm.passwordOld");
            errors.rejectValue("passwordOld", "Wrong.userForm.passwordOld");
        }
    }
}
