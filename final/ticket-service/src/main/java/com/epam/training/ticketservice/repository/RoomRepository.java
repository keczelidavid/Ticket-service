package com.epam.training.ticketservice.repository;

import com.epam.training.ticketservice.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room,String> {
    Room findByName(String name);
}
