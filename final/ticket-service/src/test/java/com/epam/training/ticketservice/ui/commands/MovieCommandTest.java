package com.epam.training.ticketservice.ui.commands;


import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieCommandTest {

    @InjectMocks
    private MovieCommand movieCommand;

    @Mock
    private MovieRepository movieRepository;


    @Test
    void test_create_movie_should_return_correct_movie_created() {
        // Given
        String expected = "Movie created: Teszt";
        Movie testMovie = new Movie("Teszt", "test", 120);
        when(movieRepository.save(any())).thenReturn(testMovie);

        // When
        var actual = movieCommand.createMovie("Teszt", "test", 120);

        // Then
        assertEquals(expected, actual);
        verify(movieRepository, times(1)).save(any());
    }

    @Test
    public void testUpdateMovie_should_update_movie_correctly() {
        // Given
        String title = "Avatar";
        String genre = "Action";
        int duration = 162;
        Movie movie = new Movie(title, "Adventure", 162);
        when(movieRepository.findByTitle(title)).thenReturn(movie);

        // When
        String result = movieCommand.updateMovie(title, genre, duration);

        // Then
        assertEquals("Movie updated: " + movie.toString(), result);
        verify(movieRepository, times(1)).save(movie);
        assertEquals(genre, movie.getGenre());
        assertEquals(duration, movie.getDuration());
    }



    @Test
    public void testDeleteMovie_should_delete_movie_successfully() {
        // Given
        String title = "Avatar";
        Movie movie = new Movie(title, "Action", 162);
        when(movieRepository.findByTitle(title)).thenReturn(movie);

        // When
        String result = movieCommand.deleteMovie(title);

        // Then
        verify(movieRepository, times(1)).delete(movie);
        assertEquals("Movie deleted: Avatar", result);
    }




    @Test
    public void testListMovies_should_return_message_if_no_movies() {
        // Given
        when(movieRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        String result = movieCommand.listMovies();

        // Then
        assertEquals("There are no movies at the moment", result);
    }
}