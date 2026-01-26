package com.sourabh.projects.airbnbcloneapp.service;

import com.sourabh.projects.airbnbcloneapp.dto.RoomDto;
import com.sourabh.projects.airbnbcloneapp.entity.Hotel;
import com.sourabh.projects.airbnbcloneapp.entity.Room;
import com.sourabh.projects.airbnbcloneapp.entity.User;
import com.sourabh.projects.airbnbcloneapp.exception.ResourceNotFoundException;
import com.sourabh.projects.airbnbcloneapp.exception.UnAuthorisedException;
import com.sourabh.projects.airbnbcloneapp.repository.BookingRepository;
import com.sourabh.projects.airbnbcloneapp.repository.HotelRepository;
import com.sourabh.projects.airbnbcloneapp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@Slf4j
@RequiredArgsConstructor
@Data
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final BookingRepository bookingRepository;

    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating room with hotelId {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not find with ID:" +id));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user is not the owner of this hotel with id: " + hotelId);
        }

        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);


        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {

        log.info("Getting all rooms with hotelId {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not find with ID:" +id));
        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long hotelId, Long roomId) {
        log.info("Getting room with roomId {}", roomId);
        Room room = roomRepository.findByIdAndHotelId(roomId,hotelId).orElseThrow(() -> new ResourceNotFoundException("Room not found in this hotel:" +roomId));
        return modelMapper.map(room,RoomDto.class);
    }

    @Transactional
    @Override
    public void deleteRoomById(Long hotelId, Long roomId) {



        log.info("Deleting room with roomId {}", roomId);
        Room room = roomRepository.findByIdAndHotelId(roomId,hotelId).orElseThrow(() -> new ResourceNotFoundException("Room not found in this hotel" +roomId));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnAuthorisedException("This user is not the owner of this room with id: " + roomId);
        }
        if(bookingRepository.existsByRoomId(roomId))
        {
            throw new IllegalStateException("Cannot delete room with active booking for this room");
        }
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);


    }

}
