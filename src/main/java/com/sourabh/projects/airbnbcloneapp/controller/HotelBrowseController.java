package com.sourabh.projects.airbnbcloneapp.controller;

import com.sourabh.projects.airbnbcloneapp.dto.HotelDto;
import com.sourabh.projects.airbnbcloneapp.dto.HotelInfoDto;
import com.sourabh.projects.airbnbcloneapp.dto.HotelPriceDto;
import com.sourabh.projects.airbnbcloneapp.dto.HotelSearchRequestDto;
import com.sourabh.projects.airbnbcloneapp.service.HotelService;
import com.sourabh.projects.airbnbcloneapp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(@RequestBody HotelSearchRequestDto hotelSearchRequestDto){

       Page<HotelPriceDto> page =  inventoryService.searchHotels(hotelSearchRequestDto);

       return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }

}
