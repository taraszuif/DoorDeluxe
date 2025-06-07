package me.zuif.doordeluxe.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import me.zuif.doordeluxe.config.WebSecurityConfig;
import me.zuif.doordeluxe.model.user.Role;
import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.service.IUserService;
import me.zuif.doordeluxe.utils.PageOptions;
import me.zuif.doordeluxe.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final IUserService userService;
    private final UserValidator userValidator;
    private final WebSecurityConfig webSecurityConfig;

    @Autowired
    public UserController(@Qualifier("userServiceImpl") IUserService userService, UserValidator userValidator, WebSecurityConfig webSecurityConfig) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.webSecurityConfig = webSecurityConfig;
    }

    @GetMapping("/user")
    public String userPanel(Principal principal, Model model) {
        try {
            User user = userService.findByUsername(principal.getName());
            if (user == null) {
                return "error/404";
            }
            model.addAttribute("user", user);
            return "user";
        } catch (Exception e) {
            logger.error("Error loading user panel", e);
            return "error/500";
        }
    }

    @GetMapping("/user/list")
    public String userList(HttpServletRequest request, Model model) {
        try {
            PageOptions pageOptions = PageOptions.retrieveFromRequest(request);
            Page<User> users = userService.findAll(PageRequest.of(pageOptions.getPage(), pageOptions.getSize()));
            model.addAttribute("page", users);
            model.addAttribute("formatter", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return "user-list";
        } catch (Exception e) {
            logger.error("Error loading user list", e);
            return "error/500";
        }
    }

    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable("id") String id, Model model) {
        try {
            User user = userService.findById(id);
            if (user == null) return "error/404";

            model.addAttribute("userForm", user);
            model.addAttribute("roles", Role.values());
            model.addAttribute("method", "edit");
            return "user-edit";
        } catch (Exception e) {
            logger.error("Error loading edit user form", e);
            return "error/500";
        }
    }

    @PostMapping("/user/edit/{id}")
    public String editUser(
            @PathVariable("id") String id,
            @ModelAttribute("userForm") User userForm,
            BindingResult bindingResult,
            @RequestParam(value = "oldPassword", required = false) String oldPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            Model model
    ) {
        User before = userService.findById(id);
        if (before == null) return "error/404";

        userForm.setPassword(before.getPassword());
        userForm.setRole(before.getRole());
        userForm.setAddTime(before.getAddTime());
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("method", "edit");

            return "user-edit";
        }

        try {

            userForm.setUserName(before.getUserName());
            userForm.setEmail(before.getEmail());
            userForm.setAddTime(before.getAddTime());

            if ("N/A".equals(before.getPassword())) {
                userForm.setPassword(before.getPassword());
            } else {
                if ((oldPassword != null && !oldPassword.isEmpty()) || (newPassword != null && !newPassword.isEmpty())) {
                    if (oldPassword == null || newPassword == null || oldPassword.isEmpty() || newPassword.isEmpty()) {
                        bindingResult.rejectValue("password", "user.password.change.required");
                        model.addAttribute("roles", Role.values());
                        model.addAttribute("method", "edit");
                        return "user-edit";
                    }

                    if (!webSecurityConfig.comparePasswords(before.getPassword(), oldPassword)) {
                        bindingResult.rejectValue("password", "user.password.change.invalidOld");
                        model.addAttribute("roles", Role.values());
                        model.addAttribute("method", "edit");
                        return "user-edit";
                    }

                    userForm.setPassword(new BCryptPasswordEncoder().encode(newPassword));
                } else {
                    userForm.setPassword(before.getPassword());
                }
            }

            if (userForm.getRole() == null) userForm.setRole(before.getRole());

            userService.update(id, userForm);
            logger.debug("User with id: {} has been successfully edited.", id);
            if (userForm.getRole() == Role.MANAGER || userForm.getRole() == Role.DIRECTOR) {
                return "redirect:/user/list";
            } else {
                return "redirect:/user";
            }
        } catch (Exception e) {
            logger.error("Error editing user", e);
            return "error/500";
        }
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") String id) {
        try {
            User user = userService.findById(id);
            if (user == null) return "error/404";

            userService.delete(id);
            logger.debug("User with id: {} successfully deleted.", id);
            return "redirect:/user/list";
        } catch (Exception e) {
            logger.error("Error deleting user", e);
            return "error/500";
        }
    }

    @GetMapping("/user/add")
    public String addUserForm(Model model) {
        model.addAttribute("userForm", new User());
        model.addAttribute("roles", Role.values());
        model.addAttribute("method", "add");
        return "user-edit";
    }

    @PostMapping("/user/add")
    public String addUser(
            @Valid @ModelAttribute("userForm") User userForm,
            BindingResult bindingResult,
            Model model
    ) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("method", "add");
            return "user-edit";
        }

        try {
            if (userService.findByEmail(userForm.getEmail()) != null) {
                model.addAttribute("message", "Акаунт за такою поштою вже існує");
                model.addAttribute("roles", Role.values());
                model.addAttribute("method", "add");
                return "user-edit";
            }
            userForm.setAddTime(LocalDateTime.now()
            );
            userService.save(userForm);
            logger.debug("User successfully added: {}", userForm.getEmail());
            return "redirect:/user/list";
        } catch (Exception e) {
            logger.error("Error adding user", e);
            return "error/500";
        }
    }
}