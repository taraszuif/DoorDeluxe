package me.zuif.doordeluxe.controller;

import me.zuif.doordeluxe.model.user.Role;
import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.service.IUserService;
import me.zuif.doordeluxe.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final IUserService userService;
    private final UserValidator userValidator;

    @Autowired
    public RegisterController(@Qualifier("userServiceImpl") IUserService userService, UserValidator userValidator) {
        this.userService = userService;

        this.userValidator = userValidator;
    }

    @GetMapping("/register")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            logger.error(String.valueOf(bindingResult.getFieldError()));
            return "register";
        }
        userForm.setRole(Role.USER);
        userForm.setAddTime(LocalDateTime.now());
        userForm.setImageUrl("/images/user/profile.jpg");
        userService.save(userForm);
        return "redirect:/login";
    }
}
