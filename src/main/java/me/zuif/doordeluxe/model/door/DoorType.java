package me.zuif.doordeluxe.model.door;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DoorType {
    ENTRANCE("Entrance"),
    INTERIOR("Interior");
    private String name;

}
