package com.epam.training.ticketservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Screening {

    @EmbeddedId
    private ScreeningId id;

    @ManyToOne
    @MapsId("movieTitle")
    private Movie movie;

    @ManyToOne
    @MapsId("roomName")
    private Room room;
    private LocalDateTime screeningTime;
    private int duration; // Vetítési idő percben

    public Screening(Movie movie, Room room, LocalDateTime screeningTime, int duration) {
        if (movie == null || room == null) {
            throw new IllegalArgumentException("Movie and Room must not be null");
        }
        this.movie = movie;
        this.room = room;
        this.id = new ScreeningId(movie.getTitle(), room.getName());
        this.screeningTime = screeningTime;
        this.duration = duration;
    }

    @Override
    public String toString() {
        // Film cím és műfaj
        String movieTitle = movie.getTitle();
        String movieGenre = movie.getGenre();  // Feltételezve, hogy létezik getGenre() metódus a Movie osztályban
        String genrePart = movieGenre != null && !movieGenre.isEmpty() ? movieGenre : "Unknown genre";

        // Terem neve
        String roomName = room.getName();

        // Dátum formázása
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = screeningTime.format(formatter);  // Itt formázzuk a dátumot

        // Az eredeti formátumot használjuk, kiegészítve a műfajjal és a hosszal
        return String.format("%s (%s, %d minutes), screened in room %s, at %s",
                movieTitle, genrePart, duration, roomName, formattedDateTime);
    }


}