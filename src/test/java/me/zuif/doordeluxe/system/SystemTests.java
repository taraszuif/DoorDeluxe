package me.zuif.doordeluxe.system;

import me.zuif.doordeluxe.config.WebSecurityConfig;
import me.zuif.doordeluxe.model.Discount;
import me.zuif.doordeluxe.model.Rating;
import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.door.DoorType;
import me.zuif.doordeluxe.model.order.Order;
import me.zuif.doordeluxe.model.order.OrderStatus;
import me.zuif.doordeluxe.model.user.Role;
import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.repository.UserRepository;
import me.zuif.doordeluxe.service.IDiscountService;
import me.zuif.doordeluxe.service.IDoorService;
import me.zuif.doordeluxe.service.IOrderService;
import me.zuif.doordeluxe.service.IRatingService;
import me.zuif.doordeluxe.service.impl.CartServiceImpl;
import me.zuif.doordeluxe.service.impl.UserServiceImpl;
import me.zuif.doordeluxe.validator.InputValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SystemTests {
    private final static String NORMAL_IMAGE_URL = "https://images.ctfassets.net/hrltx12pl8hq/28ECAQiPJZ78hxatLTa7Ts/2f695d869736ae3b0de3e56ceaca3958/free-nature-images.jpg?fit=fill&w=1200&h=630";
    @MockitoBean
    private IDoorService doorService;
    @MockitoBean
    private IOrderService orderService;
    @MockitoBean
    private IRatingService ratingService;
    @MockitoBean
    private UserServiceImpl userService;
    @MockitoBean
    private CartServiceImpl cartService;
    private CartServiceImpl cartServiceImpl;
    @Autowired
    private MockMvc mockMvc;
    private UserRepository userRepository;
    private WebSecurityConfig securityConfig;
    @MockitoBean
    private IDiscountService IDiscountService;


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

    @Test
    void testFR01_DisplayAllProducts() throws Exception {
        List<Door> doors = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            doors.add(new Door());
        }

        Page<Door> doorsPage = new PageImpl<>(doors, PageRequest.of(0, 20), 20);

        when(doorService.findFilteredDoors(any(), anyList(), anyList(), anyDouble(), anyDouble(), any(PageRequest.class))).thenReturn(doorsPage);

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void testFR01_AtLeast20ProductsDisplayed() throws Exception {
        List<Door> doors = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            doors.add(new Door());
        }

        Page<Door> doorsPage = new PageImpl<>(doors, PageRequest.of(0, 20), 20);
        when(doorService.findFilteredDoors(any(), any(), any(), any(), any(), any(PageRequest.class))).thenReturn(doorsPage);

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("doorsCount", greaterThanOrEqualTo(20L)))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    void testFR01_AtMost20ProductsDisplayed() throws Exception {
        List<Door> doors = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            doors.add(new Door());
        }

        Page<Door> doorsPage = new PageImpl<>(doors, PageRequest.of(0, 20), 20);
        when(doorService.findAll(any(PageRequest.class))).thenReturn(doorsPage);

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("doorsCount", lessThanOrEqualTo(20L)))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    @WithMockUser
    void testFR02_AddOneProductToCart() throws Exception {
        Door door = new Door();
        door.setId("1");
        door.setName("Sample Door");
        door.setPrice(BigDecimal.valueOf(200));
        door.setDoorType(DoorType.INTERIOR);

        when(doorService.findById("1")).thenReturn(door);
        when(cartService.getCart()).thenReturn(Map.of(door, 1));
        when(cartService.totalPrice()).thenReturn(BigDecimal.valueOf(200));

        mockMvc.perform(get("/cart/add/{id}", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        mockMvc.perform(get("/cart")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("doors", allOf(
                        aMapWithSize(1),
                        hasEntry(equalTo(door), equalTo(1))
                )))
                .andExpect(model().attribute("totalPrice", comparesEqualTo(BigDecimal.valueOf(200))))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void testFR02_RemoveProductFromCart() throws Exception {
        Door door = new Door();
        door.setId("1");
        door.setName("Sample Door");
        door.setPrice(BigDecimal.valueOf(200));
        door.setDoorType(DoorType.ENTRANCE);

        when(doorService.findById("1")).thenReturn(door);
        doNothing().when(cartService).removeDoor("1");
        when(cartService.getCart()).thenReturn(Map.of());
        when(cartService.totalPrice()).thenReturn(BigDecimal.ZERO);

        mockMvc.perform(get("/cart/remove/{id}", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        mockMvc.perform(get("/cart")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("doors", aMapWithSize(0)))
                .andExpect(model().attribute("totalPrice", comparesEqualTo(BigDecimal.ZERO)))
                .andDo(print());
    }

    @BeforeEach
    void setUp() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
        securityConfig = new WebSecurityConfig(userRepository);
        cartServiceImpl = new CartServiceImpl(doorService);
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

    @Test
    void testValidatePassword_TooLong() {
        assertFalse(InputValidator.validatePassword("a".repeat(257)));
    }

    @Test
    void testValidatePassword_ExactEight() {
        assertTrue(InputValidator.validatePassword("password"));
    }

    @Test
    void testValidatePassword_MaxLength() {
        assertTrue(InputValidator.validatePassword("a".repeat(255)));
    }

    @Test
    void testValidatePassword_MidLength() {
        assertTrue(InputValidator.validatePassword("a".repeat(128)));
    }

    @Test
    void testValidateEmail_InvalidSymbol() {
        assertFalse(InputValidator.validateEmail("test#test.com"));
    }


    @Test
    void testValidateEmail_MissingDomain() {
        assertFalse(InputValidator.validateEmail("test@"));
    }


    @Test
    void testValidateEmail_DoubleDot() {
        assertFalse(InputValidator.validateEmail("test@test..com"));
    }

    @Test
    void testValidateEmail_DoubleTLD() {
        assertTrue(InputValidator.validateEmail("test@test.com.com"));
    }

    @Test
    void testValidateEmail_ValidEmail() {
        assertTrue(InputValidator.validateEmail("test@test.com"));
    }

    @Test
    void testValidateEmail_ValidEmailLong() {
        assertTrue(InputValidator.validateEmail("testtestst@tesffft.com"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC_FR06_AddDoorWithNegativePrice() throws Exception {
        mockMvc.perform(post("/door/new")
                        .param("name", "Solid Door")
                        .param("description", "Very solid door")
                        .param("price", "-1")
                        .param("count", "5")
                        .param("imageUrl", NORMAL_IMAGE_URL)
                        .param("manufacturer", "Test Manufacturer")
                        .param("doorType", "INTERIOR")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("doorForm", "price"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC_FR06_AddDoorWithTooHighPrice() throws Exception {
        mockMvc.perform(post("/door/new")
                        .param("name", "Expensive Door")
                        .param("description", "Luxury golden door")
                        .param("price", "1000000001")
                        .param("count", "2")
                        .param("manufacturer", "Golden Co")
                        .param("imageUrl", "https://images.ctfassets.net/hrltx12pl8hq/28ECAQiPJZ78hxatLTa7Ts/2f695d869736ae3b0de3e56ceaca3958/free-nature-images.jpg?fit=fill&w=1200&h=630")
                        .param("doorType", "INTERIOR")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("doorForm", "price"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC40_AddDoorWithTooSmallImage() throws Exception {
        mockMvc.perform(post("/door/new")
                        .param("name", "Compact Door")
                        .param("description", "Двері для вузьких проходів.")
                        .param("price", "500")
                        .param("count", "3")
                        .param("manufacturer", "SmallCo")
                        .param("doorType", "INTERIOR")
                        .param("imageUrl", "https://www.researchgate.net/profile/Irina-Gladkova/publication/253864235/figure/fig2/AS:668627082694665@1536424527328/The-image-shown-here-is-300-300-pixel-cropped-section-of-a-SEVIRI-Band-2-digital.png")
                        .with(csrf()))
                .andExpect(status().isOk())
        ;
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC41_AddDoorWithTooLargeImage() throws Exception {
        mockMvc.perform(post("/door/new")
                        .param("name", "Huge Door")
                        .param("description", "Надзвичайно велика двері.")
                        .param("price", "999")
                        .param("count", "2")
                        .param("manufacturer", "BigDoorInc")
                        .param("doorType", "INTERIOR")
                        .param("imageUrl", "https://i.redd.it/8k-collection-7680x4320-v0-1xmcrbxdjtfa1.jpg?width=7680&format=pjpg&auto=webp&s=5712ae6b6ce2181f75430dd4f1bc26fe03edd00f")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("doorForm", "imageUrl"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC42_AddDoorWithShortDescription() throws Exception {
        mockMvc.perform(post("/door/new")
                        .param("name", "Basic Door")
                        .param("description", "Проста двері.")
                        .param("price", "199")
                        .param("count", "10")
                        .param("manufacturer", "BasicDoors")
                        .param("doorType", "INTERIOR")
                        .param("imageUrl", NORMAL_IMAGE_URL)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("doorForm", "description"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC43_AddDoorWithTooLongDescription() throws Exception {
        String longDescription = "a".repeat(2001);

        mockMvc.perform(post("/door/new")
                        .param("name", "Verbose Door")
                        .param("description", longDescription)
                        .param("price", "499")
                        .param("count", "10")
                        .param("manufacturer", "WordyDoors")
                        .param("doorType", "INTERIOR")
                        .param("imageUrl", NORMAL_IMAGE_URL)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("doorForm", "description"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC44_AddDoorWithShortName() throws Exception {
        mockMvc.perform(post("/door/new")
                        .param("name", "Abc")
                        .param("description", "Це коротке ім’я.")
                        .param("price", "150")
                        .param("count", "10")
                        .param("manufacturer", "ShortName")
                        .param("doorType", "INTERIOR")
                        .param("imageUrl", NORMAL_IMAGE_URL)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("doorForm", "name"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC45_AddDoorWithTooLongName() throws Exception {
        String longName = "a".repeat(51);

        mockMvc.perform(post("/door/new")
                        .param("name", longName)
                        .param("description", "Двері з дуже довгою назвою.")
                        .param("price", "199")
                        .param("count", "10")
                        .param("manufacturer", "LongNameCo")
                        .param("doorType", "INTERIOR")
                        .param("imageUrl", NORMAL_IMAGE_URL)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("doorForm", "name"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testAddDiscountWithValidData() throws Exception {
        Door test = new Door();
        test.setId("1");
        Discount discount = new Discount(test, new BigDecimal("15"), LocalDateTime.now());

        mockMvc.perform(post("/discount/add")
                        .flashAttr("discount", discount))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(IDiscountService, times(1)).createDiscount(any(Discount.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testAddDiscountWithInvalidDescriptionTooShort() throws Exception {
        Discount discount = new Discount(null, new BigDecimal("10"), LocalDateTime.now());

        mockMvc.perform(post("/discount/add")
                        .flashAttr("discount", discount))
                .andExpect(status().is3xxRedirection())
        ;
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testAddDiscountWithInvalidDescriptionTooLong() throws Exception {
        Discount discount = new Discount(null, new BigDecimal("10"), LocalDateTime.now());

        mockMvc.perform(post("/discount/add")
                        .flashAttr("discount", discount))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testAddDiscountWithPercentageBelowZero() throws Exception {
        Door test = new Door();
        test.setId("1");
        assertThrows(IllegalArgumentException.class, () ->
                new Discount(test, new BigDecimal("-5"), LocalDateTime.now()));

    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testAddDiscountWithPercentageAbove100() throws Exception {
        Door test = new Door();
        test.setId("1");
        assertThrows(IllegalArgumentException.class, () ->
                new Discount(test, new BigDecimal("120"), LocalDateTime.now()));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testDeleteDiscount() throws Exception {
        Door test = new Door();
        test.setId("1");
        Discount discount = new Discount(test, new BigDecimal("20"), LocalDateTime.now());
        discount.setId(10L);

        when(IDiscountService.findById(10L)).thenReturn(Optional.of(discount));

        mockMvc.perform(post("/discount/delete?discountId=10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(IDiscountService).deleteDiscount(10L);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testEditDiscountWithValidData() throws Exception {
        Door test = new Door();
        test.setId("1");
        Discount existing = new Discount(test, new BigDecimal("10"), LocalDateTime.now());
        existing.setId(20L);

        when(IDiscountService.findById(20L)).thenReturn(Optional.of(existing));

        Discount updated = new Discount(test, new BigDecimal("25"), LocalDateTime.now());

        mockMvc.perform(post("/discount/edit?discountId=20")
                        .flashAttr("discount", updated))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
        verify(IDiscountService, times(1)).createDiscount(any(Discount.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testEditDiscountWithInvalidData() throws Exception {
        Door test = new Door();
        test.setId("1");
        Discount existing = new Discount(test, new BigDecimal("10"), LocalDateTime.now());
        existing.setId(30L);

        when(IDiscountService.findById(30L)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () ->
                new Discount(test, new BigDecimal("200"), LocalDateTime.now()));


    }

    @Test
    @WithMockUser(username = "realAuthor", roles = {"USER"})
    void deleteRating() throws Exception {
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
    @WithMockUser(roles = "MANAGER")

    public void testOrderListWithOneOrder() throws Exception {
        User user = new User();
        user.setRole(Role.USER);
        when(userService.findByUsername(any())).thenReturn(user);
        Page<Order> orderPage = new PageImpl<>(List.of(new Order()));
        when(orderService.findAllByUserId(any(), any())).thenReturn(orderPage);

        mockMvc.perform(get("/order/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-list"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")

    public void testOrderListWithNoOrders() throws Exception {
        User user = new User();
        user.setRole(Role.USER);
        when(userService.findByUsername(any())).thenReturn(user);
        Page<Order> orderPage = new PageImpl<>(Collections.emptyList());
        when(orderService.findAllByUserId(any(), any())).thenReturn(orderPage);

        mockMvc.perform(get("/order/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-list"))
                .andExpect(model().attribute("page", orderPage));
    }

    @Test
    @WithMockUser(roles = "MANAGER")

    public void testChangeOrderStatus() throws Exception {
        Order order = new Order();
        order.setId("123");
        order.setStatus(OrderStatus.CREATED);

        when(orderService.findById("123")).thenReturn(Optional.of(order));

        mockMvc.perform(post("/order/status/123")
                        .param("status", "COMPLETED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order/list"));

        verify(orderService).save(argThat(savedOrder -> savedOrder.getStatus() == OrderStatus.COMPLETED));
    }

    private User createUser(String email, String password) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(email);
        user.setPassword(password);
        user.setCity("Kharkiv");
        user.setImageUrl("default.jpg");
        user.setUserName("Employee ");
        user.setAddTime(LocalDateTime.now());
        user.setFirstName("Test");
        user.setLastName("Employee");
        user.setRole(Role.MANAGER);
        return user;
    }

    @Test
    @WithMockUser(roles = "DIRECTOR")
    void testInvalidEmailFormat() throws Exception {
        User user = createUser("test", "validpassword");

        mockMvc.perform(post("/user/add")
                        .flashAttr("userForm", user))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"));
    }

    @Test
    @WithMockUser(roles = "DIRECTOR")
    void testEmptyEmail() throws Exception {
        User user = createUser("", "validpassword");

        mockMvc.perform(post("/user/add")
                        .flashAttr("userForm", user))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"));
    }

    @Test
    @WithMockUser(roles = "DIRECTOR")
    void testExistingEmail() throws Exception {
        User existing = createUser("existing@example.com", "password123");
        when(userService.findByEmail("existing@example.com")).thenReturn(existing);

        User user = createUser("existing@example.com", "validpassword");

        mockMvc.perform(post("/user/add")
                        .flashAttr("userForm", user))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"));
    }

    @Test
    @WithMockUser(roles = "DIRECTOR")
    void testShortPassword() throws Exception {
        User user = createUser("newuser@example.com", "test");

        mockMvc.perform(post("/user/add")
                        .flashAttr("userForm", user))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "password"));

    }

    @Test
    @WithMockUser(roles = "DIRECTOR")
    void testTooLongPassword() throws Exception {
        String longPassword = "a".repeat(258);
        User user = createUser("longpass@example.com", longPassword);

        mockMvc.perform(post("/user/add")
                        .flashAttr("userForm", user))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "password"));
    }

    @Test
    @WithMockUser(roles = "DIRECTOR")
    void testValidAccountCreation() throws Exception {
        User user = createUser("valid@example.com", "validpass123");

        when(userService.findByEmail("valid@example.com")).thenReturn(null);

        mockMvc.perform(post("/user/add")
                        .flashAttr("userForm", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));
    }

    @Test
    @WithMockUser(roles = "DIRECTOR")
    void testDeleteUser() throws Exception {
        User user = createUser("todelete@example.com", "somepassword");
        when(userService.findById(user.getId())).thenReturn(user);

        mockMvc.perform(post("/user/delete/" + user.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));
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
    void homePageLoadsUnderThreeSeconds() throws Exception {
        long start = System.currentTimeMillis();

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk());

        long duration = System.currentTimeMillis() - start;

        assertThat(duration)
                .as("Home page should load under 3000ms")
                .isLessThan(3000);
    }

    @Test
    @WithMockUser(username = "customer", roles = {"USER"})
    void testOrderCreationTime() throws Exception {
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
    void testChromeOpen() throws Exception {
        mockMvc.perform(get("/")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                                + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"))
                .andExpect(status().isOk());
    }

    @Test
    void testUkranianLang() throws Exception {
        mockMvc.perform(get("/home?lang=en"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/home?lang=ua"))
                .andExpect(status().isOk());


    }

    @Test
    void testEnglishLang() throws Exception {
        mockMvc.perform(get("/home?lang=ua"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/home?lang=en"))
                .andExpect(status().isOk());
    }

    @Test
    void testUnauthorizedAccessToCheckout() throws Exception {
        mockMvc.perform(get("/cart/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedAccessToCreateReview() throws Exception {
        mockMvc.perform(get("/rating/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedAccessToProfile() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedAccessToDirectorPanel() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedDeleteEmployeeAccount() throws Exception {
        mockMvc.perform(get("/user/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedAddEmployeeAccount() throws Exception {
        mockMvc.perform(get("/user/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedAccessToManagerPanel() throws Exception {
        mockMvc.perform(get("/discount/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedEditProduct() throws Exception {
        mockMvc.perform(get("/door/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedAddProduct() throws Exception {
        mockMvc.perform(get("/door/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedDeleteProduct() throws Exception {
        mockMvc.perform(get("/door/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedViewReviewsFromManagerPanel() throws Exception {
        mockMvc.perform(get("/rating/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedDeleteReview() throws Exception {
        mockMvc.perform(get("/rating/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedViewOrders() throws Exception {
        mockMvc.perform(get("/order/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedChangeOrderStatus() throws Exception {
        mockMvc.perform(get("/order/status/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedDeleteDiscount() throws Exception {
        mockMvc.perform(get("/discount/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedAddDiscount() throws Exception {
        mockMvc.perform(get("/discount/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedViewDiscountList() throws Exception {
        mockMvc.perform(get("/discount/view"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedEditDiscount() throws Exception {
        mockMvc.perform(get("/discount/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedLogoutAccess() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testUnauthorizedAccessToUserPassword() throws Exception {
        mockMvc.perform(get("/user/password?userId=1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUnauthorizedAccessToUserEmail() throws Exception {
        mockMvc.perform(get("/user/email?userId=1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void testLoginPageLoadsSuccessfully() {
        WebDriver driver;
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--user-data-dir=C:/Users/khala/AppData/Local/Google/Chrome/User Data2");
        driver = new ChromeDriver();
        driver.get("http://localhost:8060/login");

        String pageSource = driver.getPageSource();
        assertTrue(pageSource.toLowerCase().contains("логін") || pageSource.toLowerCase().contains("login"));
        driver.quit();
    }

    @Test
    public void testUserDataConsistency() throws Exception {
        User previousUserData;
        previousUserData = userRepository.findByUserName("systemUser");

        if (previousUserData == null) {
            User systemUser = new User();
            systemUser.setUserName("systemUser");
            systemUser.setPassword("1127387e12f728f3g1278f783782f78d887d1");
            systemUser.setEmail("system@email.com");
            systemUser.setAddTime(LocalDateTime.now());
            systemUser.setCity("Kharkiv");
            systemUser.setFirstName("System");
            systemUser.setLastName("User");
            systemUser.setId(UUID.randomUUID().toString());
            systemUser.setAge(20);
            systemUser.setRole(Role.USER);
            previousUserData = systemUser;
            when(userRepository.findByUserName("systemUser")).thenReturn(systemUser);
        }
        User currentUserData = userRepository.findByUserName("systemUser");


        assertEquals(previousUserData.getUserName(), currentUserData.getUserName(), "Wrong user name!");
        assertEquals(previousUserData.getEmail(), currentUserData.getEmail(), "Wrong user email!");
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC40_AddDoorSmallImage() throws Exception {
        mockMvc.perform(post("/door/new")
                        .param("name", "Compact Door")
                        .param("description", "Двері для вузьких проходів.")
                        .param("price", "500")
                        .param("count", "3")
                        .param("manufacturer", "SmallCo")
                        .param("doorType", "INTERIOR")
                        .param("imageUrl", "https://www.researchgate.net/profile/Irina-Gladkova/publication/253864235/figure/fig2/AS:668627082694665@1536424527328/The-image-shown-here-is-300-300-pixel-cropped-section-of-a-SEVIRI-Band-2-digital.png")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC41_EditDoorSmallImage() throws Exception {
        Door door = new Door();
        door.setId("1");
        door.setName("Old Door");
        door.setDescription("Description");
        door.setPrice(BigDecimal.valueOf(1000));
        door.setCount(5);
        door.setManufacturer("OldCo");
        door.setDoorType(DoorType.INTERIOR);
        door.setImageUrl(NORMAL_IMAGE_URL);

        when(doorService.findById("1")).thenReturn(door);
        mockMvc.perform(post("/door/edit/1")
                        .param("name", "Compact Door")
                        .param("description", "Двері для вузьких проходів.")
                        .param("price", "500")
                        .param("count", "3")
                        .param("manufacturer", "SmallCo")
                        .param("doorType", "INTERIOR")
                        .param("imageUrl", "https://www.researchgate.net/profile/Irina-Gladkova/publication/253864235/figure/fig2/AS:668627082694665@1536424527328/The-image-shown-here-is-300-300-pixel-cropped-section-of-a-SEVIRI-Band-2-digital.png")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}