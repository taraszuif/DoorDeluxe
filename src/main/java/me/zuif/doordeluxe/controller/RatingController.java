package me.zuif.doordeluxe.controller;

import me.zuif.doordeluxe.model.Rating;
import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.service.IDoorService;
import me.zuif.doordeluxe.service.IRatingService;
import me.zuif.doordeluxe.service.IUserService;
import me.zuif.doordeluxe.validator.RatingValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class RatingController {
    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);
    private final IRatingService ratingService;
    private final IUserService userService;
    private final IDoorService doorService;
    private final RatingValidator ratingValidator;

    @Autowired
    public RatingController(IRatingService ratingService, @Qualifier("userServiceImpl") IUserService userService, IDoorService doorService, RatingValidator ratingValidator) {
        this.ratingService = ratingService;
        this.userService = userService;
        this.doorService = doorService;
        this.ratingValidator = ratingValidator;
    }

    @GetMapping("/rating/new/{id}")
    public String newRating(Model model) {
        model.addAttribute("ratingForm", new Rating());
        model.addAttribute("method", "new");
        return "rating";
    }

    @PostMapping("/rating/new/{id}")
    public String newRating(Principal principal, @PathVariable("id") String id, @ModelAttribute("ratingForm") Rating ratingForm, BindingResult bindingResult, Model model) {
        ratingValidator.validate(ratingForm, bindingResult);

        if (bindingResult.hasErrors()) {
            logger.error(String.valueOf(bindingResult.getFieldError()));
            model.addAttribute("method", "new");
            return "rating";
        }

        ratingForm.setId(null);
        ratingForm.setAddTime(LocalDateTime.now());
        Door door = doorService.findById(id);
        if (door == null) {
            return "redirect:/home";
        }
        ratingForm.setDoor(door);

        String userName = principal.getName();

        User user = userService.findByUsername(userName);
        ratingForm.setUser(user);
        ratingService.save(ratingForm);
        logger.debug(String.format("Rating with id: %s created.", ratingForm.getId()));

        return "redirect:/door/about/" + door.getId();
    }

    @GetMapping("/rating/edit/{id}")
    public String editRating(@PathVariable("id") String id, Model model) {
        Optional<Rating> rating = ratingService.findById(id);
        if (rating.isPresent()) {
            model.addAttribute("ratingForm", rating.get());
            model.addAttribute("method", "edit");
            return "rating";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/rating/edit/{id}")
    public String editRating(Principal principal, @PathVariable("id") String id, @ModelAttribute("ratingForm") Rating ratingForm, BindingResult bindingResult, Model model) {
        ratingValidator.validate(ratingForm, bindingResult);

        if (bindingResult.hasErrors()) {
            logger.error(String.valueOf(bindingResult.getFieldError()));
            model.addAttribute("method", "edit");
            return "rating";
        }
        String userName = principal.getName();
        Rating rating = ratingService.findById(id).get();
        if (!userName.equals(rating.getUser().getUserName())) {
            logger.error("Security error: " + userName + " trying to edit foreign rating");
            model.addAttribute("method", "edit");
            return "rating";
        }
        ratingForm.setAddTime(rating.getAddTime());
        ratingForm.setUser(rating.getUser());
        ratingForm.setDoor(rating.getDoor());
        ratingService.update(id, ratingForm);
        logger.debug(String.format("Rating with id: %s has been successfully edited.", id));

        return "redirect:/door/about/" + rating.getDoor().getId();
    }


    @PostMapping("/rating/delete/{id}")
    public String deleteRating(Principal principal, @PathVariable("id") String id) {
        Optional<Rating> ratingOptional = ratingService.findById(id);
        if (ratingOptional.isPresent()) {
            String userName = principal.getName();
            Rating rating = ratingOptional.get();
            if (!userName.equals(rating.getUser().getUserName())) {
                logger.error("Security error: " + userName + " trying to delete foreign rating");
                return "redirect:/door/about/" + rating.getDoor().getId();
            }
            ratingService.delete(id);
            logger.debug(String.format("Rating with id: %s successfully deleted.", rating.getId()));
            return "redirect:/door/about/" + rating.getDoor().getId();
        } else {
            return "error/404";
        }
    }
}
