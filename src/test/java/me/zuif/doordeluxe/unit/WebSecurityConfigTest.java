package me.zuif.doordeluxe.unit;

import me.zuif.doordeluxe.config.WebSecurityConfig;
import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class WebSecurityConfigTest {
    private WebSecurityConfig securityConfig;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
        securityConfig = new WebSecurityConfig(userRepository);
    }


    @Test
    void testEncryptPasswordMd5() throws Exception {
        String password = "test";
        String encryptedPassword = securityConfig.encryptPassword(password);
        assertNotNull(encryptedPassword);
        assertNotEquals("098f6bcd4621d373cade4e832627b4f6", encryptedPassword);
    }

    @Test
    void testComparePasswords() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode("test");
        assertTrue(securityConfig.comparePasswords("test", hashedPassword));
        assertFalse(securityConfig.comparePasswords("wrong", hashedPassword));
    }

    @Test
    void testCheckAlreadyEmailExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
        assertTrue(securityConfig.checkAlreadyEmail("test@example.com"));
    }

    @Test
    void testCheckAlreadyEmailNotExists() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        assertFalse(securityConfig.checkAlreadyEmail("new@example.com"));
    }
}