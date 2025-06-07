package me.zuif.doordeluxe.controller;

import me.zuif.doordeluxe.service.ICartService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final ICartService cartService;

    public GlobalControllerAdvice(ICartService cartService) {
        this.cartService = cartService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("cartCount", cartService.getTotalCount());
    }
}