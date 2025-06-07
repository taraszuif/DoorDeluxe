package me.zuif.doordeluxe.repository;

import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.door.DoorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoorRepository extends JpaRepository<Door, String> {
    Optional<Door> findById(String id);


    Page<Door> findAll(Pageable pageable);

    @Query("SELECT d FROM Door d WHERE " +
            "(:search IS NULL OR d.name LIKE :search) AND " +
            "(:types IS NULL OR d.doorType IN :types) AND " +
            "(:manufacturers IS NULL OR d.manufacturer IN :manufacturers) AND " +
            "(:priceMin IS NULL OR d.price >= :priceMin) AND " +
            "(:priceMax IS NULL OR d.price <= :priceMax)")
    Page<Door> findFiltered(
            @Param("search") String search,
            @Param("types") List<DoorType> types,
            @Param("manufacturers") List<String> manufacturers,
            @Param("priceMin") Double priceMin,
            @Param("priceMax") Double priceMax,
            Pageable pageable
    );

    Page<Door> findAllByNameLike(String name, Pageable pageable);

    Page<Door> findAllByDescriptionLikeOrManufacturerLike(String description, String manufacturer, Pageable pageable);

    Page<Door> findAllBydoorType(DoorType doorType, Pageable pageable);

}