package me.zuif.doordeluxe.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    DIRECTOR("Director"),
    MANAGER("Manager"),
    USER("User"),
    GUEST("Guest");
    private final String name;
}
