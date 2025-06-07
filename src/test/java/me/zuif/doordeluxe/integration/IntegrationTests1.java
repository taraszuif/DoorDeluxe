package me.zuif.doordeluxe.integration;

import me.zuif.doordeluxe.model.user.Role;
import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationTests1 {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setUserName("user1");
        user.setEmail("test@gmail.com");
        user.setPassword("passpass");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCity("Kyiv");
        user.setAge(25);
        user.setRole(Role.USER);
        user.setAddTime(LocalDateTime.now());
        user.setImageUrl("default.jpg");
        userRepository.save(user);
    }

    @Test
    void TC01_01_EmailAlreadyUsed() throws Exception {
        mockMvc.perform(post("/register")
                        .param("userName", "user2")
                        .param("email", "test@gmail.com")
                        .param("password", "passpass")
                        .param("firstName", "Anna")
                        .param("lastName", "Smith")
                        .param("city", "Lviv")
                        .param("age", "30")
                        .param("role", "USER")
                        .param("addTime", LocalDateTime.now().toString())
                        .param("imageUrl", "default.jpg")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"));
    }

    @Test
    void TC01_02_InvalidEmailFormat() throws Exception {
        mockMvc.perform(post("/register")
                        .param("userName", "user2")
                        .param("email", "invalid-email")
                        .param("password", "passpass")
                        .param("firstName", "Anna")
                        .param("lastName", "Smith")
                        .param("city", "Lviv")
                        .param("age", "30")
                        .param("role", "USER")
                        .param("addTime", LocalDateTime.now().toString())
                        .param("imageUrl", "default.jpg")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is(200));
    }

    @Test
    void TC01_03_ShortPassword() throws Exception {
        mockMvc.perform(post("/register")
                        .param("userName", "user2")
                        .param("email", "newemail@gmail.com")
                        .param("password", "123")
                        .param("firstName", "Anna")
                        .param("lastName", "Smith")
                        .param("city", "Lviv")
                        .param("age", "30")
                        .param("role", "USER")
                        .param("addTime", LocalDateTime.now().toString())
                        .param("imageUrl", "default.jpg")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "password"));
    }

    @Test
    void TC01_04_LongPassword() throws Exception {
        String longPassword = "a".repeat(256);
        mockMvc.perform(post("/register")
                        .param("userName", "user2")
                        .param("email", "newemail@gmail.com")
                        .param("password", longPassword)
                        .param("firstName", "Anna")
                        .param("lastName", "Smith")
                        .param("city", "Lviv")
                        .param("age", "30")
                        .param("role", "USER")
                        .param("addTime", LocalDateTime.now().toString())
                        .param("imageUrl", "default.jpg")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "password"));
    }

    @Test
    void TC01_05_SuccessfulRegistration() throws Exception {
        mockMvc.perform(post("/register")
                        .param("userName", "user3")
                        .param("email", "valid@gmail.com")
                        .param("password", "passpass")
                        .param("firstName", "Anna")
                        .param("lastName", "Smith")
                        .param("city", "Lviv")
                        .param("age", "30")
                        .param("role", "USER")
                        .param("addTime", LocalDateTime.now().toString())
                        .param("imageUrl", "default.jpg")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void TC02_01_LoginWithInvalidPassword() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "test@gmail.com")
                        .param("password", "wrongpass")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void TC02_02_LoginWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "nonexistent@gmail.com")
                        .param("password", "passpass")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void TC02_03_LoginWithValidCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "test@gmail.com")
                        .param("password", "passpass")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection());
    }
}