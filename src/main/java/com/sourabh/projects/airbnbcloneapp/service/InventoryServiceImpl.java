package com.sourabh.projects.airbnbcloneapp.service;

import com.sourabh.projects.airbnbcloneapp.entity.Hotel;
import com.sourabh.projects.airbnbcloneapp.entity.Inventory;
import com.sourabh.projects.airbnbcloneapp.entity.Room;
import com.sourabh.projects.airbnbcloneapp.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
@Data
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;


    @Override
    public void initializeRoomForAYear(Room room) {

        LocalDate today = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);
        for(;!today.isAfter(endDate);today= today.plusDays(1)){
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
        }

    }

    @Override
    public void deleteAllInventories(Room room) {
        inventoryRepository.deleteByRoom(room);
    }



}
