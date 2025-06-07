package me.zuif.doordeluxe.unit;

import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.repository.DoorRepository;
import me.zuif.doordeluxe.service.impl.DoorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoorServiceImplTest {
    @Mock
    private DoorRepository doorRepository;

    @InjectMocks
    private DoorServiceImpl doorService;

    private Door door1;
    private Door door2;

    @BeforeEach
    void setUp() {
        door1 = new Door();
        door1.setId("1");
        door1.setName("Oak Door");
        door1.setPrice(BigDecimal.ONE);
        door1.setCount(1);

        door2 = new Door();
        door2.setId("2");
        door2.setPrice(BigDecimal.ONE);
        door2.setName("Pine Door");
        door2.setCount(1);
    }

    @Test
    void addProduct_noDoorsInStock_doorIsAdded() {
        when(doorRepository.save(any(Door.class))).thenReturn(door1);

        doorService.save(door1);

        verify(doorRepository, times(1)).save(door1);
    }

    @Test
    void addProduct_oneDoorInStock_doorIsAdded() {
        when(doorRepository.save(any(Door.class))).thenReturn(door2);
        doorService.save(door2);

        when(doorRepository.save(any(Door.class))).thenReturn(door1);

        doorService.save(door1);

        verify(doorRepository, times(1)).save(door1);
    }

    @Test
    void deleteProduct_noDoorsInStock_returnsFalse() {
        when(doorRepository.findById("1")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> doorService.delete("1"));

        assertNotNull(exception);
        verify(doorRepository, never()).delete(any(Door.class));
    }

    @Test
    void deleteProduct_otherDoorExists_returnsFalse() {
        when(doorRepository.findById("1")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> doorService.delete("1"));

        assertNotNull(exception);
        verify(doorRepository, never()).delete(any(Door.class));
    }

    @Test
    void deleteProduct_doorExists_returnsTrue() {
        when(doorRepository.findById("1")).thenReturn(Optional.of(door1));
        doNothing().when(doorRepository).delete(any(Door.class));

        doorService.delete("1");

        verify(doorRepository, times(1)).delete(door1);
    }
}