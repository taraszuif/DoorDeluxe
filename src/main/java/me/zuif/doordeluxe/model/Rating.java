package me.zuif.doordeluxe.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.user.User;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@Entity
public class Rating {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    @NotNull
    private int rate;
    @NotBlank
    private String title;
    @NotBlank
    private String comment;
    @NotNull
    private LocalDateTime addTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "door_id")
    private Door door;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public void dismissUser() {
        this.user = null;
    }
}