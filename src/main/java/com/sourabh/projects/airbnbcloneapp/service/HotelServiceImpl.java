package com.sourabh.projects.airbnbcloneapp.service;


import com.sourabh.projects.airbnbcloneapp.dto.HotelDto;
import com.sourabh.projects.airbnbcloneapp.dto.HotelInfoDto;
import com.sourabh.projects.airbnbcloneapp.dto.RoomDto;
import com.sourabh.projects.airbnbcloneapp.entity.Hotel;
import com.sourabh.projects.airbnbcloneapp.entity.Room;
import com.sourabh.projects.airbnbcloneapp.entity.User;
import com.sourabh.projects.airbnbcloneapp.exception.ResourceNotFoundException;
import com.sourabh.projects.airbnbcloneapp.exception.UnAuthorisedException;
import com.sourabh.projects.airbnbcloneapp.repository.HotelRepository;
import com.sourabh.projects.airbnbcloneapp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating hotel with name: {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);
        hotel = hotelRepository.save(hotel);
        log.info("Creating hotel with id: {}", hotelDto.getId());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {

        log.info("Getting the hotel with id: {}", id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not find with ID:" +id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user is not the owner of this hotel with id: " + id);
        }
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("updating the hotel with id: {}", id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not find with ID:" +id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user is not the owner of this hotel with id: " + id);
        }
        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long hotelId) {

        log.info("Deleting hotel with id: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not find with ID:" +hotelId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user is not the owner of this hotel with id: " + hotelId);
        }

        log.info("Deleted hotel with id: {}", hotelId);
        for(Room room : hotel.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());

        }
        hotelRepository.delete(hotel);

    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {

        //assume we can set as active only once

        log.info("Activating hotel with id: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not find with ID:" +hotelId));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user is not the owner of this hotel with id: " + hotelId);
        }
        hotel.setActive(true);
        for(Room room : hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }


    }

    //public method
    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        log.info("Getting hotel info with id: {}", hotelId);

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not find with ID:" +hotelId));
        List<RoomDto> rooms = hotel.getRooms()
                                    .stream()
                                    .map((element) -> modelMapper.map(element, RoomDto.class))
                                    .toList();

        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);
    }

}
