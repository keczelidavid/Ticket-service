package com.epam.training.ticketservice.ui.commands;


import com.epam.training.ticketservice.user.UserService;
import com.epam.training.ticketservice.user.model.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Optional;

@ShellComponent
@AllArgsConstructor
public class UserCommand {

  private final UserService userService;

  @ShellMethod(key = "sign out", value = "User logout")
  public String logout() {
    return userService.logout()
            .map(userDto -> "Logged out successfully")
            .orElse("You need to login first!");
  }

  @ShellMethod(key = "sign in privileged", value = "User login")
  public String login(String username, String password) {
    Optional<UserDto> user = userService.login(username, password);

    if (user.isPresent()) {
      return "Signed in with privileged account 'admin'";
    } else {
      return "Login failed due to incorrect credentials"; // Sikertelen belépés üzenet
    }
  }


  @ShellMethod(key = "user register", value = "User registration")
  public String registerUser(String userName, String password) {
    try {
      userService.registerUser(userName, password);
      return "Registration was successful!";
    } catch (Exception e) {
      return "Registration failed!";
    }
  }

  @ShellMethod(key = "describe account", value = "Get user information")
  public String print() {
    return userService.describe()
            .map(Record::toString)
            .orElse("You are not signed in");
  }
}
