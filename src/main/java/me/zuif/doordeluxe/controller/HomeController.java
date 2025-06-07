package me.zuif.doordeluxe.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.door.DoorType;
import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.service.IDoorService;
import me.zuif.doordeluxe.service.IUserService;
import me.zuif.doordeluxe.service.impl.UserServiceImpl;
import me.zuif.doordeluxe.utils.FindOptions;
import me.zuif.doordeluxe.utils.PageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    private final IDoorService doorService;
    private final IUserService userService;

    @Autowired
    public HomeController(IDoorService doorService, UserServiceImpl userService) {
        this.doorService = doorService;
        this.userService = userService;
    }

    @GetMapping("/fragment/doors")
    public String doorListFragment(Principal principal, HttpServletRequest request, Model model) {
        fillHomeModel(model, principal, request);
        return "fragments/door-cards";
    }

    @GetMapping(value = {"/", "/index", "/home"})
    public String home(Principal principal, HttpServletRequest request, Model model) {
        fillHomeModel(model, principal, request);
        return "home";
    }

    private void fillHomeModel(Model model, Principal principal, HttpServletRequest request) {
        PageOptions pageOptions = PageOptions.retrieveFromRequest(request);
        FindOptions findOptions = FindOptions.retrieveFromRequest(request);

        PageRequest pageRequest;
        if (findOptions.isSort()) {
            Sort.Direction direction = findOptions.isSortDescending() ? Sort.Direction.DESC : Sort.Direction.ASC;
            pageRequest = PageRequest.of(pageOptions.getPage(), pageOptions.getSize(),
                    Sort.by(direction, findOptions.getSortField()));
        } else {
            pageRequest = PageRequest.of(pageOptions.getPage(), pageOptions.getSize());
        }

        Page<Door> doors = doorService.findFilteredDoors(
                findOptions.getSearch(),
                findOptions.getDoorTypes(),
                findOptions.getManufacturers(),
                findOptions.getPriceMin(),
                findOptions.getPriceMax(),
                pageRequest
        );

        boolean searchDetail = false;

        if (findOptions.getSearch() != null && doors.getTotalElements() == 0) {
            searchDetail = true;
            doors = doorService.findAllByDescriptionLikeOrManufacturerLike(
                    findOptions.getSearch(), findOptions.getSearch(), pageRequest
            );
        }

        if (doors == null) {
            doors = new PageImpl<>(new ArrayList<>());
        }

        User user = null;
        if (principal != null) {
            user = userService.findByUsername(principal.getName());
        }

        model.addAttribute("user", user);
        model.addAttribute("page", doors);
        model.addAttribute("doorsCount", doors.getTotalElements());
        model.addAttribute("searchDetail", searchDetail);
        model.addAttribute("types", DoorType.values());
        Set<String> manufacturers = doors.getContent().stream()
                .map(Door::getManufacturer)

                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toCollection(TreeSet::new));
        model.addAttribute("manufacturers", manufacturers);
        model.addAttribute("doorTypes", Arrays.stream(DoorType.values()).map(doorType -> "type." + doorType.getName().toLowerCase()).toList());
    }

    @GetMapping("/about")
    public String about() {

        return "about";
    }

    @PostMapping("/change")
    public ResponseEntity<Void> changeLocale(@RequestParam("lang") String lang, HttpServletResponse response) {
        Cookie cookie = new Cookie("lang", lang);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/contact")
    public String contact() {

        return "contact";
    }


}
