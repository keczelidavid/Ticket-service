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
public class Room {
    @Id
    private String name;
    private int seatRows;
    private int seatColumns;

    @Override
    public String toString() {
        int totalSeats = seatRows * seatColumns;
        return String.format("Room %s with %d seats, %d rows and %d columns", name, totalSeats, seatRows, seatColumns);
    }
}