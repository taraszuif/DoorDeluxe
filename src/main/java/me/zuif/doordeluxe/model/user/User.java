package me.zuif.doordeluxe.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.zuif.doordeluxe.model.Rating;
import me.zuif.doordeluxe.model.order.Order;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "Users")
@Entity
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    @NotBlank
    private String userName;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String password;
    @NotNull
    private int age;
    @NotNull
    private String city;
    @NotNull
    private Role role;
    @NotNull
    private LocalDateTime addTime;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String imageUrl;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Rating> ratings;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Order> orders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }


    @PreRemove
    public void dismissRatingAndOrder() {
        this.ratings.forEach(Rating::dismissUser);
        this.ratings.clear();
        this.orders.forEach(Order::dismissUser);
        this.ratings.clear();
    }


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
