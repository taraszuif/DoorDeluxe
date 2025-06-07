package me.zuif.doordeluxe.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import me.zuif.doordeluxe.model.door.DoorType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class FindOptions {
    private String search;
    private List<DoorType> doorTypes;
    private List<String> manufacturers;
    private Double priceMin;
    private Double priceMax;

    private boolean sort;
    private boolean sortDescending;
    private String sortField;

    public static FindOptions retrieveFromRequest(HttpServletRequest request) {
        FindOptions findOptions = new FindOptions();

        String search = request.getParameter("search");
        if (search != null && !search.isEmpty()) {
            findOptions.setSearch("%" + search + "%");
        }

        String doorTypesParam = request.getParameter("doorTypes");
        if (doorTypesParam != null && !doorTypesParam.isEmpty()) {
            String[] types = doorTypesParam.split(",");
            List<DoorType> typeList = new ArrayList<>();
            for (String typeStr : types) {
                try {
                    typeList.add(DoorType.valueOf(typeStr.replaceAll("type\\.", "").toUpperCase()));
                } catch (IllegalArgumentException ignored) {
                }
            }
            findOptions.setDoorTypes(typeList);
        } else {
            findOptions.setDoorTypes(null);
        }


        String manufacturersParam = request.getParameter("manufacturers");
        if (manufacturersParam == null) {
            findOptions.setManufacturers(null);

        } else {
            List<String> manufacturers = Arrays.stream(manufacturersParam.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .toList();
            findOptions.setManufacturers(manufacturers);

        }
        String priceMinStr = request.getParameter("priceMin");
        if (priceMinStr != null && !priceMinStr.isEmpty()) {
            try {
                findOptions.setPriceMin(Double.parseDouble(priceMinStr));
            } catch (NumberFormatException ignored) {
            }
        }

        String priceMaxStr = request.getParameter("priceMax");
        if (priceMaxStr != null && !priceMaxStr.isEmpty()) {
            try {
                findOptions.setPriceMax(Double.parseDouble(priceMaxStr));
            } catch (NumberFormatException ignored) {
            }
        }

        String sortField = request.getParameter("sort");
        List<String> validSortFields = List.of("count", "discountedPrice", "addTime");
        if (sortField != null && validSortFields.contains(sortField)) {
            findOptions.setSort(true);
            findOptions.setSortField(sortField);
        }

        String sortDir = request.getParameter("sortBy");
        if (sortDir != null && sortDir.equalsIgnoreCase("descend")) {
            findOptions.setSortDescending(true);
        }
        return findOptions;
    }
}