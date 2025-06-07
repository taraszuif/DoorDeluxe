package me.zuif.doordeluxe.controller;

import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.door.DoorType;
import me.zuif.doordeluxe.service.IDoorService;
import me.zuif.doordeluxe.service.IRatingService;
import me.zuif.doordeluxe.validator.DoorValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class DoorController {
    private static final Logger logger = LoggerFactory.getLogger(DoorController.class);
    private final IDoorService doorService;
    private final IRatingService ratingService;
    private final DoorValidator doorValidator;

    @Autowired
    public DoorController(IDoorService doorService, IRatingService ratingService, DoorValidator doorValidator) {
        this.doorService = doorService;
        this.ratingService = ratingService;
        this.doorValidator = doorValidator;
    }

    @GetMapping("/door/new")
    public String newDoor(Model model) {
        model.addAttribute("doorForm", new Door());
        model.addAttribute("method", "new");
        model.addAttribute("types", DoorType.values());
        return "door";
    }

    @PostMapping("/door/new")
    public String newDoor(@ModelAttribute("doorForm") Door doorForm, BindingResult bindingResult, Model model) {
        doorValidator.validate(doorForm, bindingResult);

        if (bindingResult.hasErrors()) {
            logger.error(String.valueOf(bindingResult.getFieldError()));
            model.addAttribute("method", "new");
            return "door";
        }

        doorForm.setAddTime(LocalDateTime.now());
        doorService.save(doorForm);
        logger.debug(String.format("Door with id: %s created.", doorForm.getId()));

        return "redirect:/home";
    }

    @GetMapping("/door/edit/{id}")
    public String editDoor(@PathVariable("id") String id, Model model) {
        Door door = doorService.findById(id);
        if (door != null) {
            model.addAttribute("doorForm", door);
            model.addAttribute("types", DoorType.values());
            model.addAttribute("method", "edit");
            return "door";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/door/about/{id}")
    public String aboutDoor(@PathVariable("id") String id, Model model) {
        Door door = doorService.findById(id);
        if (door != null) {
            model.addAttribute("door", door);
            model.addAttribute("rating", ratingService.getDTO(door));
            model.addAttribute("formatter", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return "door-about";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/door/edit/{id}")
    public String editDoor(@PathVariable("id") String id, @ModelAttribute("doorForm") Door doorForm, BindingResult bindingResult, Model model) {
        doorValidator.validate(doorForm, bindingResult);

        if (bindingResult.hasErrors()) {
            logger.error(String.valueOf(bindingResult.getFieldError()));
            model.addAttribute("method", "edit");
            return "door";
        }
        Door door = doorService.findById(id);
        if (door != null) {
            doorForm.setAddTime(door.getAddTime());
        } else {
            doorForm.setAddTime(LocalDateTime.now());
        }
        doorService.update(id, doorForm);
        logger.debug(String.format("Door with id: %s has been successfully edited.", id));

        return "redirect:/home";
    }


    @PostMapping("/door/delete/{id}")
    public String deleteDoor(@PathVariable("id") String id) {
        Door door = doorService.findById(id);
        if (door != null) {

            doorService.delete(id);
            logger.debug(String.format("Door with id: %s successfully deleted.", door.getId()));
            return "redirect:/home";
        } else {
            return "error/404";
        }
    }
}
