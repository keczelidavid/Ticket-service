package com.epam.training.ticketservice.ui.commands;

import com.epam.training.ticketservice.model.Room;
import com.epam.training.ticketservice.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RoomCommandTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomCommand roomCommand;

    public RoomCommandTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_create_room_should_return_correct_room_created() {
        // Given
        String expected = "Movie created: Teszt";
        Room testRoom = new Room("Teszt", 10, 20);
        when(roomRepository.save(any())).thenReturn(testRoom);

        // When
        var actual = roomCommand.createRoom("Teszt", 10, 20);

        // Then
        assertEquals(expected, actual);
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    public void testUpdateRoom() {
        String name = "Room A";
        int seatRows = 6;
        int seatColumns = 6;
        Room room = new Room(name, 5, 5);

        when(roomRepository.findByName(name)).thenReturn(room);

        String result = roomCommand.updateRoom(name, seatRows, seatColumns);

        verify(roomRepository, times(1)).save(room);
        assertEquals("Room Updated: " + room.toString(), result);
    }

    @Test
    public void testDeleteRoom() {
        String name = "Room A";
        Room room = new Room(name, 5, 5);
        when(roomRepository.findByName(name)).thenReturn(room);

        String result = roomCommand.deleteRoom(name);

        verify(roomRepository, times(1)).delete(room);
        assertEquals("Movie deleted: " + name, result);
    }


    @Test
    public void testListRoomsEmpty() {

        List<Room> rooms = List.of();
        when(roomRepository.findAll()).thenReturn(rooms);

        String result = roomCommand.listRooms();

        assertEquals("There are no rooms at the moment", result);
    }
    
}