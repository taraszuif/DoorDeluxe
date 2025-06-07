package me.zuif.doordeluxe.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.zuif.doordeluxe.model.door.Door;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "door_id")
    private Door door;
    @Min(0)
    @Max(100)
    private BigDecimal percentage;


    @NotNull
    private LocalDateTime date;

    public Discount(Door door, BigDecimal percentage, LocalDateTime date) {
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        this.door = door;
        this.percentage = percentage;
        this.date = date;
    }

    public int getIntPercentage() {
        return percentage.intValue();
    }

    @Override
    public String toString() {
        return "Discount{" +
                "id=" + id +
                ", productId=" + door.getId() +
                ", percentage=" + percentage +
                ", date=" + date +
                '}';
    }
}