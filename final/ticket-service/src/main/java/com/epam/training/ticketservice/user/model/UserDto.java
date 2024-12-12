package com.epam.training.ticketservice.user.model;


import com.epam.training.ticketservice.user.persistence.User;

public record UserDto(String username, User.Role role) {

    @Override
    public String toString() {
        return String.format("Signed in with privileged account '%s'", role.name().toLowerCase());
    }

}
