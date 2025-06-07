package me.zuif.doordeluxe.validator;

import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    private final UserServiceImpl userService;

    @Autowired
    public UserValidator(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "age", "error.not_empty");
        if (user.getUserName().length() < 3) {
            errors.rejectValue("username", "register.error.username.less_3");
        }
        if (user.getUserName().length() > 24) {
            errors.rejectValue("username", "register.error.username.over_24");
        }
        User userWithSameUsername = userService.findByUsername(user.getUserName());
        if (userWithSameUsername != null && (user.getId() == null || !userWithSameUsername.getId().equals(user.getId()))) {
            errors.rejectValue("userName", "register.error.duplicated.username");
        }

        User userWithSameEmail = userService.findByEmail(user.getEmail());
        if (userWithSameEmail != null && (user.getId() == null || !userWithSameEmail.getId().equals(user.getId()))) {
            errors.rejectValue("email", "register.error.duplicated.email");
        }
        if (!user.getPassword().equals("N/A") && user.getPassword().length() < 8) {
            errors.rejectValue("password", "register.error.password.less_8");
        }
        if (user.getPassword().length() >= 256) {
            errors.rejectValue("password", "register.error.password.over_32");
        }

        if (!InputValidator.validateEmail(user.getEmail())) {
            errors.rejectValue("email", "register.error.email.invalid");
        }
    }
}
