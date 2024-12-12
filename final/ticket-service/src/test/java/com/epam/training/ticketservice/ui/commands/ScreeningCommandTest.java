package com.epam.training.ticketservice.ui.commands;

import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.model.Room;
import com.epam.training.ticketservice.model.Screening;
import com.epam.training.ticketservice.repository.MovieRepository;
import com.epam.training.ticketservice.repository.RoomRepository;
import com.epam.training.ticketservice.repository.ScreeningRepository;
import com.epam.training.ticketservice.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScreeningCommandTest {

    @InjectMocks
    private ScreeningCommand screeningCommand;

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserService userService;

    private Room room;
    private Movie movie;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        room = new Room("Room1", 10, 100);
        movie = new Movie("Movie1", "teszt1", 120);
    }

    @Test
    void testCreateScreening_Success() {
        when(roomRepository.findByName("Room1")).thenReturn(room);
        when(movieRepository.findByTitle("Movie1")).thenReturn(movie);
        when(screeningRepository.findByMovieAndRoom(movie, room)).thenReturn(null);
        when(screeningRepository.findAll()).thenReturn(List.of());

        String result = screeningCommand.createScreening("Movie1", "Room1", "2023-10-10 15:00");

        assertEquals("Screening created successfully.", result);
        verify(screeningRepository).save(any(Screening.class));
    }

    @Test
    void testCreateScreening_RoomNotFound() {
        when(roomRepository.findByName("Room1")).thenReturn(null);

        String result = screeningCommand.createScreening("Movie1", "Room1", "2023-10-10 15:00");

        assertEquals("Room not found.", result);
    }

    @Test
    void testCreateScreening_MovieNotFound() {
        when(roomRepository.findByName("Room1")).thenReturn(room);
        when(movieRepository.findByTitle("Movie1")).thenReturn(null);

        String result = screeningCommand.createScreening("Movie1", "Room1", "2023-10-10 15:00");

        assertEquals("Movie not found.", result);
    }

    @Test
    void testCreateScreening_InvalidDateFormat() {
        String result = screeningCommand.createScreening("Movie1", "Room1", "invalid-date");

        assertEquals("Invalid date and time format. Please use 'yyyy-MM-dd HH:mm'.", result);
    }

    @Test
    void testCreateScreening_OverlapDetected() {
        LocalDateTime existingTime = LocalDateTime.of(2023, 10, 10, 14, 0);
        Screening existingScreening = new Screening(movie, room, existingTime, 120);

        when(roomRepository.findByName("Room1")).thenReturn(room);
        when(movieRepository.findByTitle("Movie1")).thenReturn(movie);
        when(screeningRepository.findByMovieAndRoom(movie, room)).thenReturn(null);
        when(screeningRepository.findAll()).thenReturn(List.of(existingScreening));

        String result = screeningCommand.createScreening("Movie1", "Room1", "2023-10-10 15:00");

        assertEquals("This would start in the break period after another screening in this room", result);
    }

    @Test
    void testDeleteScreening_Success() {
        Screening screening = new Screening(movie, room, LocalDateTime.now(), movie.getDuration());
        when(movieRepository.findByTitle("Movie1")).thenReturn(movie);
        when(roomRepository.findByName("Room1")).thenReturn(room);
        when(screeningRepository.findByMovieAndRoom(movie, room)).thenReturn(screening);

        String result = screeningCommand.deleteScreening("Movie1", "Room1");

        assertEquals("Screening deleted successfully.", result);
        verify(screeningRepository).delete(screening);
    }

    @Test
    void testDeleteScreening_NotFound() {
        when(movieRepository.findByTitle("Movie1")).thenReturn(movie);
        when(roomRepository.findByName("Room1")).thenReturn(room);
        when(screeningRepository.findByMovieAndRoom(movie, room)).thenReturn(null);

        String result = screeningCommand.deleteScreening("Movie1", "Room1");

        assertEquals("Screening not found.", result);
    }

    @Test
    void testListScreenings_Empty() {
        when(screeningRepository.findAll()).thenReturn(List.of());

        String result = screeningCommand.listScreenings();

        assertEquals("There are no screenings", result);
    }

    @Test
    void testListScreenings_WithResults() {
        Screening screening = new Screening(movie, room, LocalDateTime.of(2023, 10, 10, 15, 0), movie.getDuration());
        when(screeningRepository.findAll()).thenReturn(List.of(screening));

        String result = screeningCommand.listScreenings();

        assertEquals(screening.toString() + "\n", result);
    }
}
