package me.zuif.doordeluxe.controller;


import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.order.Order;
import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.service.ICartService;
import me.zuif.doordeluxe.service.IDoorService;
import me.zuif.doordeluxe.service.IOrderService;
import me.zuif.doordeluxe.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final ICartService cartService;
    private final IDoorService doorService;
    private final IOrderService orderService;
    private final IUserService userService;

    public CartController(ICartService cartService, IDoorService doorService, IOrderService orderService, @Qualifier("userServiceImpl") IUserService userService) {
        this.cartService = cartService;
        this.doorService = doorService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("doors", cartService.getCart());
        model.addAttribute("totalPrice", cartService.totalPrice());
        return "cart";
    }

    @GetMapping("/cart/add/{id}")
    public String addDoorToCart(@PathVariable("id") String id) {
        Door door = doorService.findById(id);
        if (door != null) {
            cartService.addDoor(id);
            logger.debug(String.format("Door with id: %s added to shopping cart.", id));
        }
        return "redirect:/home";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeDoorFromCart(@PathVariable("id") String id) {
        Door door = doorService.findById(id);
        if (door != null) {
            cartService.removeDoor(id);
            logger.debug(String.format("Door with id: %s removed from shopping cart.", id));
        } else {

        }
        return "redirect:/cart";
    }

    @GetMapping("/cart/clear")
    public String clearDoorsInCart() {
        cartService.clear();
        return "redirect:/cart";
    }

    @GetMapping("/cart/checkout")
    public String cartCheckout(Principal principal, Model model) {
        Optional<Order> orderOptional;

        orderOptional = cartService.checkout();

        if (orderOptional.isEmpty()) {
            model.addAttribute("noDoors", true);
            model.addAttribute("doors", cartService.getCart());
            model.addAttribute("totalPrice", cartService.totalPrice());
            return "cart";
        }
        Order order = orderOptional.get();

        order.setAddTime(LocalDateTime.now());
        String userName = principal.getName();
        User user = userService.findByUsername(userName);
        order.setUser(user);
        orderService.save(order);
        return "redirect:/cart";
    }
}
