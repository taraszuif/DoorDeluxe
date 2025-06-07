package me.zuif.doordeluxe.service.impl;

import me.zuif.doordeluxe.model.Discount;
import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.repository.DiscountRepository;
import me.zuif.doordeluxe.service.IDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IDiscountServiceImpl implements IDiscountService {

    private final DiscountRepository discountRepository;

    @Autowired
    public IDiscountServiceImpl(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    public Discount createDiscount(Discount discount) {
        return discountRepository.save(discount);
    }


    @Override
    public Optional<Discount> findByDoor(Door product) {
        return discountRepository.findByDoor(product);
    }

    @Override
    public Optional<Discount> findById(Long id) {
        return discountRepository.findById(id);
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    @Override
    public void deleteDiscount(Long id) {
        discountRepository.deleteById(id);
    }
}