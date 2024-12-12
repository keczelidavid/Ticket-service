package com.epam.training.ticketservice.ui.commands;

import com.epam.training.ticketservice.user.UserService;
import com.epam.training.ticketservice.user.UserServiceImpl;
import com.epam.training.ticketservice.user.model.UserDto;
import com.epam.training.ticketservice.user.persistence.User;
import com.epam.training.ticketservice.user.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.shell.standard.ShellComponent;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ShellComponent
public class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserService userService = mock(UserService.class);
    private final UserCommand userCommand = new UserCommand(userService);
    private final UserService underTest = new UserServiceImpl(userRepository);


    @Test
    void testLoginShouldSetLoggedInUserWhenUsernameAndPasswordAreCorrect() {
        // Given
        User user = new User("user", "password", User.Role.USER);
        Optional<User> expected = Optional.of(user);
        when(userRepository.findByUsernameAndPassword("user", "pass")).thenReturn(Optional.of(user));

        // When
        Optional<UserDto> actual = underTest.login("user", "pass");

        // Then
        assertEquals(expected.get().getUsername(), actual.get().username());
        assertEquals(expected.get().getRole(), actual.get().role());
        verify(userRepository).findByUsernameAndPassword("user", "pass");
    }

    @Test
    void testLoginShouldReturnOptionalEmptyWhenUsernameOrPasswordAreNotCorrect() {
        // Given
        Optional<UserDto> expected = Optional.empty();
        when(userRepository.findByUsernameAndPassword("dummy", "dummy")).thenReturn(Optional.empty());

        // When
        Optional<UserDto> actual = underTest.login("dummy", "dummy");

        // Then
        assertEquals(expected, actual);
        verify(userRepository).findByUsernameAndPassword("dummy", "dummy");
    }

    @Test
    void testLogoutShouldReturnOptionalEmptyWhenThereIsNoOneLoggedIn() {
        // Given
        Optional<UserDto> expected = Optional.empty();

        // When
        Optional<UserDto> actual = underTest.logout();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testLogoutShouldReturnThePreviouslyLoggedInUserWhenThereIsALoggedInUser() {
        // Given
        User user = new User("user", "password", User.Role.USER);
        when(userRepository.findByUsernameAndPassword("user", "pass")).thenReturn(Optional.of(user));
        Optional<UserDto> expected = underTest.login("user", "password");

        // When
        Optional<UserDto> actual = underTest.logout();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testDescribeShouldReturnTheLoggedInUserWhenThereIsALoggedInUser() {
        // Given
        User user = new User("user", "password", User.Role.USER);
        when(userRepository.findByUsernameAndPassword("user", "pass")).thenReturn(Optional.of(user));
        Optional<UserDto> expected = underTest.login("user", "password");

        // When
        Optional<UserDto> actual = underTest.describe();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testDescribeShouldReturnOptionalEmptyWhenThereIsNoOneLoggedIn() {
        // Given
        Optional<UserDto> expected = Optional.empty();

        // When
        Optional<UserDto> actual = underTest.describe();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testRegisterUserShouldCallUserRepositoryWhenTheInputIsValid() {
        // Given
        // When
        underTest.registerUser("user", "pass");

        // Then
        verify(userRepository).save(new User("user", "pass", User.Role.USER));
    }

    @Test
    public void testLogoutFailure() {
        when(userService.logout()).thenReturn(Optional.empty());

        String result = userCommand.logout();
        assertEquals("You need to login first!", result);
    }

    @Test
    public void testRegisterUserSuccess() {
        String userName = "newUser";
        String password = "newPassword";

        String result = userCommand.registerUser(userName, password);
        assertEquals("Registration was successful!", result);
    }

    @Test
    public void testRegisterUserFailure() {
        String userName = "existingUser";
        String password = "existingPassword";
        Mockito.doThrow(new RuntimeException()).when(userService).registerUser(userName, password);

        String result = userCommand.registerUser(userName, password);
        assertEquals("Registration failed!", result);
    }
}