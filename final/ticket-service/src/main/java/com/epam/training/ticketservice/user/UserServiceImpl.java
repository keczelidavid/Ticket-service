package com.epam.training.ticketservice.user;


import com.epam.training.ticketservice.user.model.UserDto;
import com.epam.training.ticketservice.user.persistence.User;
import com.epam.training.ticketservice.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private UserDto loggedInUser = null;
  private User user;

  @PostConstruct
  public void init() {
    this.user = new User("admin", "admin", User.Role.ADMIN);
    userRepository.save(user);
  }

  @Override
  public Optional<UserDto> login(String username, String password) {
    Optional<User> user = userRepository.findByUsernameAndPassword(username, password);
    if (user.isEmpty()) {
      return Optional.empty();
    }
    loggedInUser = new UserDto(user.get().getUsername(), user.get().getRole());
    return describe();
  }

  @Override
  public Optional<UserDto> logout() {
    Optional<UserDto> previouslyLoggedInUser = describe();
    loggedInUser = null;
    return previouslyLoggedInUser;
  }

  @Override
  public Optional<UserDto> describe() {
    return Optional.ofNullable(loggedInUser);
  }

  @Override
  public void registerUser(String username, String password) {
    User user = new User(username, password, User.Role.USER);
    userRepository.save(user);
  }
}
