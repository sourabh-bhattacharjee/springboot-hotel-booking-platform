package com.sourabh.projects.airbnbcloneapp.service;
import com.sourabh.projects.airbnbcloneapp.entity.Room;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);


}
