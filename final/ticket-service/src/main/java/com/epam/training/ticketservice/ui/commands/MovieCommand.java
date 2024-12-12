package com.epam.training.ticketservice.ui.commands;


import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.repository.MovieRepository;
import com.epam.training.ticketservice.user.UserService;
import com.epam.training.ticketservice.user.model.UserDto;
import com.epam.training.ticketservice.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Optional;

@ShellComponent
public class MovieCommand {

    @Autowired
    private UserService userService;

    @Autowired
    private MovieRepository movieRepository;

    @ShellMethod(value = "List all movies", key = "list movies")
    public String listMovies() {
        Iterable<Movie> movies = movieRepository.findAll();
        if (movies.iterator().hasNext()) {
            StringBuilder result = new StringBuilder();
            movies.forEach(movie -> result.append(String.format("%s (%s, %d minutes)%n",
                    movie.getTitle(),
                    movie.getGenre(),
                    movie.getDuration())));
            return result.toString().trim();
        } else {
            return "There are no movies at the moment";
        }
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Create a new movie", key = "create movie")
    public String createMovie(String title, String genre, int durationMinutes) {
        movieRepository.save(new Movie(title, genre, durationMinutes));
        return "Movie created: " + title;
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Update movie", key = "update movie")
    public String updateMovie(String title, String genre, int durationMinutes) {
        Movie found = movieRepository.findByTitle(title);
        found.setGenre(genre);
        found.setDuration(durationMinutes);
        movieRepository.save(found);
        return "Movie updated: " + found.toString();
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Delete movie", key = "delete movie")
    public String deleteMovie(String title) {
        Movie found = movieRepository.findByTitle(title);
        movieRepository.delete(found);
        return "Movie deleted: " + title;
    }

    private Availability isAvailable() {
        Optional<UserDto> user = userService.describe();
        return user.isPresent() && user.get().role() == User.Role.ADMIN
                ? Availability.available()
                : Availability.unavailable("You are not an admin!");
    }
}