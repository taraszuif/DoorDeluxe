package me.zuif.doordeluxe.repository;

import me.zuif.doordeluxe.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUserName(String userName);

    User findByUserNameOrEmail(String username, String email);

    Optional<User> findByEmail(String email);

    Optional<User> findById(String id);

    Page<User> findAll(Pageable pageable);
}
