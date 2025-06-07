package me.zuif.doordeluxe.controller;

import jakarta.validation.Valid;
import me.zuif.doordeluxe.model.Discount;
import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.service.IDiscountService;
import me.zuif.doordeluxe.service.IDoorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class DiscountController {

    private static final Logger logger = LoggerFactory.getLogger(DiscountController.class);
    private final IDiscountService IDiscountService;
    private final IDoorService doorService;

    @Autowired
    public DiscountController(IDiscountService IDiscountService, IDoorService doorService) {
        this.IDiscountService = IDiscountService;
        this.doorService = doorService;
    }

    @GetMapping("/discount/add")
    public String newDiscountForm(@RequestParam("doorId") String doorId, Model model) {
        Discount discount = new Discount();
        Door door = doorService.findById(doorId);
        discount.setDoor(door);
        model.addAttribute("discount", discount);
        return "discount-form";
    }

    @PostMapping("/discount/add")
    public String createDiscount(@ModelAttribute @Valid Discount discount, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.warn("Discount form has validation errors: {}", bindingResult.getAllErrors());
            return "discount-form";
        }

        try {
            logger.info("Creating discount");
            discount.setDate(LocalDateTime.now());
            IDiscountService.createDiscount(discount);
            if (discount.getDoor() != null) {
                Door door = doorService.findById(discount.getDoor().getId());
                if (door != null) {
                    door.updateDiscountedPrice(false);
                    doorService.save(door);
                }
            }
            logger.info("Discount successfully created");
            return "redirect:/home";
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create discount: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "discount-form";
        }
    }

    @GetMapping("/discount/edit")
    public String editDiscountForm(@RequestParam("discountId") Long id, Model model) {
        Optional<Discount> discount = IDiscountService.findById(id);

        if (discount.isPresent()) {
            model.addAttribute("discount", discount.get());
            return "discount-form";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/discount/edit")
    public String editDiscount(@RequestParam("discountId") Long id,
                               @ModelAttribute @Valid Discount discount,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "discount-form";
        }

        Optional<Discount> existingDiscount = IDiscountService.findById(id);
        if (existingDiscount.isPresent()) {
            Discount updatedDiscount = existingDiscount.get();
            updatedDiscount.setPercentage(discount.getPercentage());
            updatedDiscount.setDate(LocalDateTime.now());
            IDiscountService.createDiscount(updatedDiscount);
            if (discount.getDoor() != null) {
                Door door = doorService.findById(discount.getDoor().getId());
                if (door != null) {
                    door.updateDiscountedPrice(false);
                    doorService.save(door);
                }
            }
            return "redirect:/home";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/discount/delete")
    public String deleteDiscount(@RequestParam("discountId") Long id) {
        Optional<Discount> discount = IDiscountService.findById(id);
        if (discount.isPresent()) {
            Discount discount1 = discount.get();
            Door door = doorService.findById(discount1.getDoor().getId());
            if (door != null) {
                door.updateDiscountedPrice(false);
                doorService.save(door);
            }
            IDiscountService.deleteDiscount(id);

            return "redirect:/home";
        } else {
            return "error/404";
        }
    }
}