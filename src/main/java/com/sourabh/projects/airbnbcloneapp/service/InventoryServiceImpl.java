package com.sourabh.projects.airbnbcloneapp.service;

import com.sourabh.projects.airbnbcloneapp.dto.HotelDto;
import com.sourabh.projects.airbnbcloneapp.dto.HotelPriceDto;
import com.sourabh.projects.airbnbcloneapp.dto.HotelSearchRequestDto;
import com.sourabh.projects.airbnbcloneapp.entity.Inventory;
import com.sourabh.projects.airbnbcloneapp.entity.Room;
import com.sourabh.projects.airbnbcloneapp.repository.HotelMinPriceRepository;
import com.sourabh.projects.airbnbcloneapp.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
@Slf4j
@Data
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;


    @Override
    public void initializeRoomForAYear(Room room) {

        LocalDate today = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);
        for(;!today.isAfter(endDate);today= today.plusDays(1)){
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
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
        log.info("Deleting all inventories of room with id {}", room.getId());
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequestDto hotelSearchRequestDto) {

        log.info("Searching hotels for {} city, from {} to {}", hotelSearchRequestDto.getCity(), hotelSearchRequestDto.getStartDate(), hotelSearchRequestDto.getEndDate());

        Pageable pageable = PageRequest.of(hotelSearchRequestDto.getPage(), hotelSearchRequestDto.getSize());
        long dateCount = ChronoUnit.DAYS.between(hotelSearchRequestDto.getStartDate(),hotelSearchRequestDto.getEndDate()) + 1;

        Page<HotelPriceDto> hotelPage =  hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequestDto.getCity(),
                                                                hotelSearchRequestDto.getStartDate(),
                                                                hotelSearchRequestDto.getEndDate(),
                                                                hotelSearchRequestDto.getRoomsCount(),
                                                                dateCount,pageable);

        return hotelPage;
    }


}
