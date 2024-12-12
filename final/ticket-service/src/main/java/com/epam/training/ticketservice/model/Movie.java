package com.epam.training.ticketservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Movie {
    @Id
    private String title;
    private String genre;
    private int duration; // Vetítési idő percben

    @Override
    public String toString() {
        return String.format("%s (%s, %d minutes)", title, genre, duration);
    }

}