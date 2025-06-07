package me.zuif.doordeluxe.validation;

import me.zuif.doordeluxe.model.Rating;
import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.door.DoorType;
import me.zuif.doordeluxe.model.order.Order;
import me.zuif.doordeluxe.model.user.Role;
import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.service.IDoorService;
import me.zuif.doordeluxe.service.IRatingService;
import me.zuif.doordeluxe.service.impl.CartServiceImpl;
import me.zuif.doordeluxe.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ValidationTests {
    private final static String NORMAL_IMAGE_URL = "https://images.ctfassets.net/hrltx12pl8hq/28ECAQiPJZ78hxatLTa7Ts/2f695d869736ae3b0de3e56ceaca3958/free-nature-images.jpg?fit=fill&w=1200&h=630";
    @MockitoBean
    private IDoorService doorService;

    @MockitoBean
    private IRatingService ratingService;
    @MockitoBean
    private UserServiceImpl userService;
    @MockitoBean
    private CartServiceImpl cartService;
    private CartServiceImpl cartServiceImpl;
    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithAnonymousUser
    void testTS01_1() {
        Door door = new Door();
        door.setId("1");
        door.setName("Sample Door");
        door.setPrice(BigDecimal.valueOf(100.0));
        door.setAddTime(LocalDateTime.now());
        door.setDoorType(DoorType.INTERIOR);
        door.setImageUrl(NORMAL_IMAGE_URL);
        when(doorService.findById(door.getId())).thenReturn(door);
        cartServiceImpl.addDoor(door.getId());

        assertTrue(cartServiceImpl.getCart().containsKey(door));
    }

    @BeforeEach
    void setUp() throws Exception {
        cartServiceImpl = new CartServiceImpl(doorService);
    }

    @Test
    @WithMockUser
    void testTS01_2() {
        Door door = new Door();
        door.setId("1");
        door.setName("Sample Door");
        door.setPrice(BigDecimal.valueOf(100.0));
        door.setAddTime(LocalDateTime.now());
        door.setDoorType(DoorType.INTERIOR);
        door.setImageUrl(NORMAL_IMAGE_URL);
        when(doorService.findById(door.getId())).thenReturn(door);
        cartServiceImpl.addDoor(door.getId());

        assertTrue(cartServiceImpl.getCart().containsKey(door));
    }

    @Test
    @WithAnonymousUser
    void testTS02_1() throws Exception {
        mockMvc.perform(post("/register")
                        .param("userName", "user2")
                        .param("email", "")
                        .param("password", "pass")
                        .param("firstName", "Anna")
                        .param("lastName", "Smith")
                        .param("city", "Lviv")
                        .param("age", "30")
                        .param("role", "USER")
                        .param("addTime", LocalDateTime.now().toString())
                        .param("imageUrl", "default.jpg")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"));
    }

    @Test
    @WithAnonymousUser
    void testTS02_2() throws Exception {
        mockMvc.perform(post("/register")
                        .param("userName", "user2")
                        .param("email", "test")
                        .param("password", "pass")
                        .param("firstName", "Anna")
                        .param("lastName", "Smith")
                        .param("city", "Lviv")
                        .param("age", "30")
                        .param("role", "USER")
                        .param("addTime", LocalDateTime.now().toString())
                        .param("imageUrl", "default.jpg")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"));
    }

    @Test
    @WithAnonymousUser
    void testTS02_3() throws Exception {
        when(userService.findByEmail("alreadyemail@mail.com")).thenReturn(new User());

        mockMvc.perform(post("/register")
                        .param("userName", "user2")
                        .param("email", "alreadyemail@mail.com")
                        .param("password", "pass123")
                        .param("firstName", "Anna")
                        .param("lastName", "Smith")
                        .param("city", "Lviv")
                        .param("age", "30")
                        .param("role", "USER")
                        .param("addTime", LocalDateTime.now().toString())
                        .param("imageUrl", "default.jpg")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"));
    }

    @Test
    @WithAnonymousUser
    void testTS02_4() throws Exception {
        mockMvc.perform(post("/register")
                        .param("userName", "user2")
                        .param("email", "mail@mail.com")
                        .param("password", "pass1234")
                        .param("firstName", "Anna")
                        .param("lastName", "Smith")
                        .param("city", "Lviv")
                        .param("age", "30")
                        .param("role", "USER")
                        .param("addTime", LocalDateTime.now().toString())
                        .param("imageUrl", "default.jpg")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }

    @Test
    @WithAnonymousUser
    void testTS03_1() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", "test")
                        .param("password", "pass")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login?error"));
    }

    @Test
    @WithAnonymousUser
    void testTS03_2() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", "test@gmail.com")
                        .param("password", "wrongpass")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login?error"));
    }

    @Test
    @WithAnonymousUser
    void testTS03_3() throws Exception {
        mockMvc.perform(post("/register")
                        .param("userName", "user23")
                        .param("email", "test@gmail.com")
                        .param("password", "passpass")
                        .param("firstName", "Anna")
                        .param("lastName", "Smith")
                        .param("city", "Lviv")
                        .param("age", "30")
                        .param("role", "USER")
                        .param("addTime", LocalDateTime.now().toString())
                        .param("imageUrl", "default.jpg")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login"));


        mockMvc.perform(get("/login")

                        .param("userName", "user23")
                        .param("password", "passpass")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testTS04_1() throws Exception {
        mockMvc.perform(get("/cart/checkout"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testTS04_2() throws Exception {
        Door door = new Door();
        door.setId("2");
        door.setName("Test Door");
        door.setPrice(BigDecimal.valueOf(150));
        door.setAddTime(LocalDateTime.now());
        door.setDoorType(DoorType.ENTRANCE);
        door.setImageUrl(NORMAL_IMAGE_URL);
        when(doorService.findById("2")).thenReturn(door);

        cartService.addDoor("2");

        mockMvc.perform(get("/cart/checkout"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void testTS05_1() throws Exception {
        mockMvc.perform(post("/door/new")
                        .param("name", "Invalid Door")
                        .param("price", "-100")
                        .param("imageUrl", NORMAL_IMAGE_URL)
                        .param("doorType", "INTERIOR")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("doorForm", "price"))
                .andExpect(view().name("door"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void testTS05_2() throws Exception {
        mockMvc.perform(post("/door/new")
                        .param("name", "Valid Door")
                        .param("price", "200")
                        .param("description", "Descggggggription description".repeat(10))
                        .param("manufacturer", "Manufacturer")
                        .param("imageUrl", NORMAL_IMAGE_URL)
                        .param("doorType", "INTERIOR")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void testTS06_1() throws Exception {
        String doorId = "1";
        Door door = new Door();
        door.setId(doorId);
        Mockito.when(doorService.findById(doorId)).thenReturn(door);

        mockMvc.perform(post("/door/delete/{id}", doorId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void testTS07_1() throws Exception {
        String doorId = "1";
        Door door = new Door();
        door.setId(doorId);
        door.setAddTime(LocalDateTime.now());

        Mockito.when(doorService.findById(doorId)).thenReturn(door);

        mockMvc.perform(post("/door/edit/{id}", doorId)
                        .param("name", "Updated Door")
                        .param("price", "-10")
                        .param("imageUrl", NORMAL_IMAGE_URL)
                        .param("manufacturer", "Manufacturer")
                        .param("description", "Description description")
                        .param("doorType", "INTERIOR")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("doorForm", "price"))
                .andExpect(view().name("door"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void testTS07_2() throws Exception {
        String doorId = "1";
        Door door = new Door();
        door.setId(doorId);
        door.setAddTime(LocalDateTime.now());

        Mockito.when(doorService.findById(doorId)).thenReturn(door);

        mockMvc.perform(post("/door/edit/{id}", doorId)
                        .param("name", "Updated Door")
                        .param("price", "250")
                        .param("description", "Description description".repeat(10))
                        .param("manufacturer", "Manufacturer")
                        .param("imageUrl", NORMAL_IMAGE_URL)
                        .param("doorType", "INTERIOR")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockUser(username = "notAuthor", roles = {"USER"})
    void testTS08_1_DeleteByNonAuthor() throws Exception {
        Rating rating = new Rating();
        rating.setId("5");
        rating.setUser(new User());
        rating.getUser().setUserName("realAuthor");
        rating.setDoor(new Door());
        rating.getDoor().setId("door123");

        when(ratingService.findById("5")).thenReturn(Optional.of(rating));

        mockMvc.perform(post("/rating/delete/5").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/door/about/door123"));

        verify(ratingService, never()).delete(any());
    }

    @Test
    @WithMockUser(username = "realAuthor", roles = {"USER"})
    void testTS08_2_DeleteByAuthor() throws Exception {
        Rating rating = new Rating();
        rating.setId("5");
        rating.setUser(new User());
        rating.getUser().setUserName("realAuthor");
        rating.setDoor(new Door());
        rating.getDoor().setId("door123");

        when(ratingService.findById("5")).thenReturn(Optional.of(rating));

        mockMvc.perform(post("/rating/delete/5").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/door/about/door123"));

        verify(ratingService, times(1)).delete("5");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"DIRECTOR"})
    void testAddUserWithExistingEmail() throws Exception {
        User existing = new User();
        existing.setEmail("existing@mail.com");
        existing.setUserName("oldUser");
        existing.setPassword("encoded");
        existing.setLastName("LastName");
        existing.setFirstName("FirstName");
        existing.setCity("City");
        existing.setImageUrl("default.jpg");
        existing.setAddTime(LocalDateTime.now());
        existing.setRole(Role.MANAGER);

        Mockito.when(userService.findByEmail("existing@mail.com")).thenReturn(existing);

        mockMvc.perform(post("/user/add")
                        .param("email", "existing@mail.com")
                        .param("userName", "newUser")
                        .param("password", "12345678")
                        .param("role", "MANAGER")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"));

    }

    @Test
    @WithMockUser(username = "admin", roles = {"DIRECTOR"})
    void testAddUserWithNewEmail() throws Exception {
        mockMvc.perform(post("/user/add")
                        .param("email", "new@mail.com")
                        .param("userName", "newUser")
                        .param("password", "12345678")
                        .param("city", "City")
                        .param("imageUrl", "default.jpg")
                        .param("lastName", "LastName")
                        .param("firstName", "FirstName")
                        .param("addTime", LocalDateTime.now().toString())
                        .param("role", "MANAGER")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"DIRECTOR"})
    void testDeleteUserSuccessfully() throws Exception {
        User user = new User();
        user.setId("test-id");
        user.setUserName("toDelete");
        user.setEmail("todelete@mail.com");
        user.setPassword("12345678");
        user.setRole(Role.MANAGER);
        Mockito.when(userService.findById("test-id")).thenReturn(user);

        mockMvc.perform(post("/user/delete/test-id")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));
    }

    @Test
    @WithMockUser(username = "customer", roles = {"USER"})
    void testTSN01_1() throws Exception {
        Order order = new Order();
        when(cartService.checkout()).thenReturn(Optional.of(order));
        when(userService.findByUsername("customer")).thenReturn(new User());

        long start = System.currentTimeMillis();

        mockMvc.perform(get("/cart/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        long duration = System.currentTimeMillis() - start;
        assertTrue(duration < 2000, "Оформлення замовлення має займати менше 2 секунд");
    }

    @Test
    void testTSN02_1() throws Exception {
        mockMvc.perform(get("/home?lang=en"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/home?lang=ua"))
                .andExpect(status().isOk());


    }

    @Test
    void testTSN03_1() throws Exception {
        mockMvc.perform(get("/")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                                + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"))
                .andExpect(status().isOk());
    }

}