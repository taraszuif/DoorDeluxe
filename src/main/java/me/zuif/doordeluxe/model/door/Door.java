package me.zuif.doordeluxe.model.door;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.zuif.doordeluxe.model.Discount;
import me.zuif.doordeluxe.model.Rating;
import me.zuif.doordeluxe.model.order.Order;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Door implements Cloneable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    @NotBlank
    private String manufacturer;
    @NotNull
    private DoorType doorType;
    @NotBlank
    private String name;
    @NotBlank
    private String imageUrl;
    @NotBlank
    private String description;
    @NotNull
    private BigDecimal price;
    @NotNull
    private int count;
    @NotNull
    private LocalDateTime addTime;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "door")
    private List<Rating> ratings;
    @OneToOne(mappedBy = "door", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Discount discount;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "doors")
    private List<Order> orders;
    @Column(name = "discounted_price")
    private BigDecimal discountedPrice;


    public BigDecimal getPriceWithDiscountRaw() {
        if (discount != null && discount.getPercentage() != null) {
            BigDecimal discountPercentage = discount.getPercentage();
            BigDecimal multiplier = BigDecimal.valueOf(1).subtract(discountPercentage.divide(BigDecimal.valueOf(100)));
            BigDecimal discountedPrice = price.multiply(multiplier);
            return discountedPrice.setScale(2, RoundingMode.HALF_UP);
        } else {
            return price.setScale(2, RoundingMode.HALF_UP);
        }
    }


    public void updateDiscountedPrice(boolean onDelete) {
        if (discount != null && discount.getPercentage() != null && !onDelete) {
            BigDecimal discountPercentage = discount.getPercentage();
            BigDecimal multiplier = BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100)));
            discountedPrice = price.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        } else {
            discountedPrice = price.setScale(2, RoundingMode.HALF_UP);
        }
    }

    public String getShortName() {
        if (name == null) {
            return "";
        }
        return name.length() > 30 ? name.substring(0, 27) + "..." : name;
    }

    public String getPriceWithDiscount() {
        if (discount != null && discount.getPercentage() != null) {
            BigDecimal discountPercentage = discount.getPercentage();
            BigDecimal multiplier = BigDecimal.valueOf(1).subtract(discountPercentage.divide(BigDecimal.valueOf(100)));
            BigDecimal discountedPrice = price.multiply(multiplier);
            return discountedPrice.setScale(2, RoundingMode.HALF_UP).toString();
        } else {
            return price.setScale(2, RoundingMode.HALF_UP).toString();
        }
    }

    @Override
    public String toString() {
        return "Door{" +
                "id='" + id + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", doorType=" + doorType +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", count=" + count +
                ", addTime=" + addTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Door door = (Door) o;
        return Objects.equals(id, door.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
