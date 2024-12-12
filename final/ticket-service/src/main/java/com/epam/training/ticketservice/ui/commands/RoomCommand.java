package com.epam.training.ticketservice.ui.commands;

import com.epam.training.ticketservice.model.Room;
import com.epam.training.ticketservice.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class RoomCommand {
    @Autowired
    private RoomRepository roomRepository;

    @ShellMethod(value = "Create a new Room", key = "create room")
    public String createRoom(String title, int seatRows, int seatColumns) {
        roomRepository.save(new Room(title, seatRows, seatColumns));
        return "Movie created: " + title;
    }

    @ShellMethod(value = "Update room", key = "update room")
    public String updateRoom(String name, int seatRows, int seatColumns) {
        Room found = roomRepository.findByName(name);
        found.setSeatColumns(seatColumns);
        found.setSeatRows(seatRows);
        roomRepository.save(found);
        return "Room Updated: " + found.toString();
    }

    @ShellMethod(value = "Delete room", key = "delete room")
    public String deleteRoom(String name) {
        Room found = roomRepository.findByName(name);
        roomRepository.delete(found);
        return "Movie deleted: " + name;
    }

    @ShellMethod(value = "List all rooms", key = "list rooms")
    public String listRooms() {
        Iterable<Room> rooms = roomRepository.findAll();
        if (rooms.iterator().hasNext()) {
            StringBuilder result = new StringBuilder();
            rooms.forEach(room -> result.append(room.toString()).append("\n"));
            return result.toString();
        } else {
            return "There are no rooms at the moment";
        }
    }
}
