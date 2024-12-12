package com.epam.training.ticketservice.ui.commands;

import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.model.Room;
import com.epam.training.ticketservice.model.Screening;
import com.epam.training.ticketservice.repository.MovieRepository;
import com.epam.training.ticketservice.repository.RoomRepository;
import com.epam.training.ticketservice.repository.ScreeningRepository;
import com.epam.training.ticketservice.user.UserService;
import com.epam.training.ticketservice.user.model.UserDto;
import com.epam.training.ticketservice.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@ShellComponent
public class ScreeningCommand {

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserService userService;

    @ShellMethod(value = "Create screening", key = "create screening")
    public String createScreening(String filmTitle, String roomName, String screeningTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime time;
        try {
            time = LocalDateTime.parse(screeningTime, formatter);
        } catch (Exception e) {
            return "Invalid date and time format. Please use 'yyyy-MM-dd HH:mm'.";
        }

        // Check if room is exist
        Room room = roomRepository.findByName(roomName);
        if (room == null) {
            return "Room not found.";
        }

        // Check if movie is exist
        Movie movie = movieRepository.findByTitle(filmTitle);
        if (movie == null) {
            return "Movie not found.";
        }

        // Check if screening already exists
        Screening existingScreening = screeningRepository
                .findByMovieAndRoom(movie, room);
        if (existingScreening != null) {
            return "Screening already exists.";
        }

        // Check if there is any overlap with existing screenings
        String overlapResult = checkScreeningOverlap(time, room, movie.getDuration());
        if (!overlapResult.equals("No overlap detected")) {
            return overlapResult;  // Return the appropriate overlap message
        }

        // Save the new screening
        screeningRepository.save(new Screening(movie, room, time, movie.getDuration()));
        return "Screening created successfully.";
    }

    private String checkScreeningOverlap(LocalDateTime screeningTime, Room room, int duration) {
        for (Screening existingScreening : screeningRepository.findAll()) {
            if (existingScreening.getRoom().getName().equals(room.getName())) {
                // Start and end of existing screening, cleaning time added.
                LocalDateTime existingScreeningStart = existingScreening.getScreeningTime();
                LocalDateTime existingScreeningEnd = existingScreeningStart.plusMinutes(existingScreening.getDuration());
                LocalDateTime breakEnd = existingScreeningEnd.plusMinutes(10);

                // End of new screening.
                LocalDateTime newScreeningEnd = screeningTime.plusMinutes(duration);

                // Check if the new screening starts in the break period after the previous screening
                if (screeningTime.isBefore(breakEnd) && newScreeningEnd.isAfter(existingScreeningEnd)) {
                    return "This would start in the break period after another screening in this room";
                }

                // Check for overlapping during the screening itself
                if (screeningTime.isBefore(existingScreeningEnd) && newScreeningEnd.isAfter(existingScreeningStart)) {
                    return "There is an overlapping screening";
                }
            }
        }
        return "No overlap detected"; // No overlap or collision
    }

    private boolean isOverlappingScreening(LocalDateTime screeningTime, Room room, int duration) {
        String result = checkScreeningOverlap(screeningTime, room, duration);
        return !result.equals("No overlap detected");
    }


    @ShellMethod(value = "Delete screening", key = "delete screening")
    public String deleteScreening(String filmTitle, String roomName) {
        Movie movie = movieRepository.findByTitle(filmTitle);
        Room room = roomRepository.findByName(roomName);
        Screening screening = screeningRepository
                .findByMovieAndRoom(movie, room);
        if (screening != null) {
            screeningRepository.delete(screening);
            return "Screening deleted successfully.";
        } else {
            return "Screening not found.";
        }
    }

    @ShellMethod(value = "List all screenings", key = "list screenings")
    public String listScreenings() {
        Iterable<Screening> screenings = screeningRepository.findAll();
        StringBuilder result = new StringBuilder();
        if (screenings.iterator().hasNext()) {
            for (Screening screening : screenings) {
                result.append(screening.toString()).append("\n");
            }
            return result.toString();
        }
        return "There are no screenings";
    }

    private Availability isAvailable() {
        Optional<UserDto> user = userService.describe();
        return user.isPresent() && user.get().role() == User.Role.ADMIN
                ? Availability.available()
                : Availability.unavailable("You are not an admin!");
    }

}