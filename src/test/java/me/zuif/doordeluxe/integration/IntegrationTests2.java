package me.zuif.doordeluxe.integration;

import me.zuif.doordeluxe.controller.DoorController;
import me.zuif.doordeluxe.model.Discount;
import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.door.DoorType;
import me.zuif.doordeluxe.service.IDiscountService;
import me.zuif.doordeluxe.service.IDoorService;
import me.zuif.doordeluxe.validator.DoorValidator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")

@AutoConfigureMockMvc
@Transactional
public class IntegrationTests2 {

    @MockitoBean
    private IDoorService doorService;
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private DoorValidator doorValidator;

    @InjectMocks
    private DoorController doorController;
    @MockitoBean
    private IDiscountService IDiscountService;

    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(doorController).build();
    }

    @Test
    @WithMockUser
    void TC01_01_CreateOrder() throws Exception {
        Door door = new Door();
        door.setId("1");
        door.setName("Sample Door");
        door.setPrice(BigDecimal.valueOf(100.0));
        door.setAddTime(LocalDateTime.now());
        door.setDoorType(DoorType.INTERIOR);
        doorService.save(door);

        mockMvc.perform(MockMvcRequestBuilders.get("/cart/add/{id}", "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andDo(print());

        mockMvc.perform(MockMvcRequestBuilders.get("/cart/checkout")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC02_01_AddDoorSuccessfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/door/new")
                        .param("name", "New Door")
                        .param("description", "High-quality door")
                        .param("price", "100")
                        .param("count", "10")
                        .param("manufacturer", "ABC Corp")
                        .param("doorType", "INTERIOR")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC02_02_AddDoorWithInvalidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/door/new")
                        .param("name", "")
                        .param("description", "High-quality door")
                        .param("price", "-100")
                        .param("count", "10")
                        .param("manufacturer", "ABC Corp")
                        .param("doorType", "INTERIOR")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
        ;
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC03_01_DeleteDoor() throws Exception {
        Door door = new Door();
        door.setId("1");
        door.setName("Sample Door");
        door.setPrice(BigDecimal.valueOf(100.0));
        door.setAddTime(LocalDateTime.now());
        door.setDoorType(DoorType.INTERIOR);
        doorService.save(door);

        mockMvc.perform(MockMvcRequestBuilders.post("/door/delete/{id}", "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC04_01_UpdateDoorSuccessfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/door/edit/{id}", "1")
                        .param("name", "Updated Door")
                        .param("description", "Updated description")
                        .param("price", "150")
                        .param("count", "20")
                        .param("manufacturer", "XYZ Corp")
                        .param("doorType", "INTERIOR")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC04_02_UpdateDoorWithInvalidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/door/edit/{id}", "1")
                        .param("name", "")
                        .param("description", "Updated description")
                        .param("price", "-150")
                        .param("count", "20")
                        .param("manufacturer", "XYZ Corp")
                        .param("doorType", "INTERIOR")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC05_01_AddDiscountSuccessfully() throws Exception {
        Door test = new Door();
        test.setId("1");
        Discount discount = new Discount(test, BigDecimal.valueOf(10), LocalDateTime.now());

        when(IDiscountService.createDiscount(any(Discount.class))).thenReturn(discount);

        mockMvc.perform(post("/discount/add")
                        .param("discountPercentage", "10")
                        .param("validUntil", "2025-12-31")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC05_02_AddDiscountWithInvalidPercentage() throws Exception {
        mockMvc.perform(post("/discount/add")
                        .param("discountPercentage", "-10")
                        .param("validUntil", "2025-12-31")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        ;
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC03_01_DeleteDiscount() throws Exception {
        doNothing().when(IDiscountService).deleteDiscount(1L);

        mockMvc.perform(post("/discount/delete")
                        .param("discountId", "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC04_01_UpdateDiscountSuccessfully() throws Exception {
        Door test = new Door();
        test.setId("1");
        Discount updatedDiscount = new Discount(test, BigDecimal.valueOf(15), LocalDateTime.now());

        when(IDiscountService.createDiscount(any(Discount.class))).thenReturn(updatedDiscount);

        mockMvc.perform(post("/discount/edit")
                        .param("discountId", "1")
                        .param("discountPercentage", "15")
                        .param("validUntil", "2025-12-31")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void TC04_02_UpdateDiscountWithInvalidPercentage() throws Exception {
        mockMvc.perform(post("/discount/edit")
                        .param("discountId", "1")
                        .param("discountPercentage", "-15")
                        .param("validUntil", "2025-12-31")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
        ;
    }
}