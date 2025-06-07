package me.zuif.doordeluxe.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.zuif.doordeluxe.dto.OrderDTO;
import me.zuif.doordeluxe.model.order.Order;
import me.zuif.doordeluxe.model.order.OrderStatus;
import me.zuif.doordeluxe.model.user.Role;
import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.service.IOrderService;
import me.zuif.doordeluxe.service.IUserService;
import me.zuif.doordeluxe.utils.PageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final IUserService userService;
    private final IOrderService orderService;

    @Autowired
    public OrderController(@Qualifier("userServiceImpl") IUserService userService, IOrderService orderService) {


        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/order/status/{id}")
    public String changeOrderStatus(@PathVariable("id") String id, @RequestParam("status") OrderStatus status, Model model) {
        Optional<Order> orderOptional = orderService.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(status);
            orderService.save(order);
            return "redirect:/order/list";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/analytics/financial")
    public String financialAnalytics(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Model model) {

        Map<LocalDate, BigDecimal> totals = orderService.getTotalRevenueByDate(start, end);

        List<String> totalLabels = totals.keySet().stream()
                .sorted()
                .map(date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .toList();

        List<BigDecimal> totalValues = totals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();

        model.addAttribute("totalLabels", totalLabels);
        model.addAttribute("totalValues", totalValues);

        return "analytics-financial";
    }

    @GetMapping("/analytics/orders")
    public String orderCountAnalytics(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Model model) {

        Map<LocalDate, Long> orderCounts = orderService.getOrderCountByDate(start, end);

        List<String> orderLabels = orderCounts.keySet().stream()
                .sorted()
                .map(date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .toList();

        List<Long> orderValues = orderCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();


        model.addAttribute("orderLabels", orderLabels);
        model.addAttribute("orderValues", orderValues);

        return "analytics-orders";
    }

    @GetMapping("/order/about/{id}")
    public String aboutOrder(@PathVariable("id") String id, Model model) {
        Optional<OrderDTO> dtoOptional = orderService.getDTO(id);
        if (dtoOptional.isPresent()) {
            model.addAttribute("order", dtoOptional.get());
            model.addAttribute("doors", dtoOptional.get().getOrder().getDoors());
            model.addAttribute("formatter", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return "order-about";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/analytics")
    public String about() {

        return "analytic";
    }

    @GetMapping("/order/list")
    public String orderList(Principal principal, HttpServletRequest request, Model model) {
        PageOptions pageOptions = PageOptions.retrieveFromRequest(request);
        String userName = principal.getName();
        User user = userService.findByUsername(userName);
        Page<Order> orders;
        if (user.getRole() == Role.DIRECTOR || user.getRole() == Role.MANAGER) {
            orders = orderService.findAll(PageRequest.of(pageOptions.getPage(), pageOptions.getSize()));
        } else {
            orders = orderService.findAllByUserId(user.getId(), PageRequest.of(pageOptions.getPage(), pageOptions.getSize()));
        }

        model.addAttribute("user", user);

        model.addAttribute("page", orders);
        model.addAttribute("formatter", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "order-list";
    }

    @PostMapping("/order/delete/{id}")
    public String deleteOrder(@PathVariable("id") String id) {
        Optional<Order> orderOptional = orderService.findById(id);
        if (orderOptional.isPresent()) {
            orderService.delete(id);
            logger.debug(String.format("Order with id: %s successfully deleted.", id));
            return "redirect:/order/list";
        } else {
            return "error/404";
        }
    }
}
