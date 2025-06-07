package me.zuif.doordeluxe.service;

import me.zuif.doordeluxe.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    void save(User user);

    void login(String username, String password);

    User findByUsername(String username);

    User findByEmail(String email);

    User findById(String id);

    Page<User> findAll(Pageable pageable);

    void delete(String id);

    void update(String id, User user);
}
