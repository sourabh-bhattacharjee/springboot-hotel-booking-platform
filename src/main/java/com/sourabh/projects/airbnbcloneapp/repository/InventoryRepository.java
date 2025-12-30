package com.sourabh.projects.airbnbcloneapp.repository;

import com.sourabh.projects.airbnbcloneapp.entity.Inventory;
import com.sourabh.projects.airbnbcloneapp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface InventoryRepository  extends JpaRepository<Inventory,Long> {

    void deleteByRoom(Room room);
}
