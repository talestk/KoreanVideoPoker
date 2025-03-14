package com.tales.koreanvp;

import com.tales.koreanvp.model.Player;
import com.tales.koreanvp.repo.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.github.stefanbirkner.systemlambda.SystemLambda.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppTest {

    @Mock
    private PlayerRepository playerRepository;

    @Test
    void testMain_NewUserRegistration() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock user input for new user registration
        withTextFromSystemIn("n", "testuser", "testpassword")
                .execute(() -> {
                    // Mock PlayerRepository behavior
                    when(playerRepository.registerPlayer("testuser", "testpassword")).thenReturn(true);
                    when(playerRepository.loadPlayer("testuser")).thenReturn(new Player("testuser", 100, 0, 0));

                    // Capture console output
                    String output = tapSystemOutNormalized(() -> {
                        App.main(new String[]{});
                    });

                    // Verify output
                    assertTrue(output.contains("Welcome to Video Poker!"));
                    assertTrue(output.contains("Welcome, testuser!"));
                    assertTrue(output.contains("Your current credits: 100"));
                });

        // Verify PlayerRepository interactions
        verify(playerRepository, times(1)).registerPlayer("testuser", "testpassword");
        verify(playerRepository, times(1)).loadPlayer("testuser");
    }

    @Test
    void testMain_ExistingUserLogin() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock user input for existing user login
        withTextFromSystemIn("y", "testuser", "testpassword")
                .execute(() -> {
                    // Mock PlayerRepository behavior
                    when(playerRepository.loginPlayer("testuser", "testpassword"))
                            .thenReturn(new Player("testuser", 150, 5, 3));

                    // Capture console output
                    String output = tapSystemOutNormalized(() -> {
                        App.main(new String[]{});
                    });

                    // Verify output
                    assertTrue(output.contains("Welcome to Video Poker!"));
                    assertTrue(output.contains("Welcome, testuser!"));
                    assertTrue(output.contains("Your current credits: 150"));
                });

        // Verify PlayerRepository interactions
        verify(playerRepository, times(1)).loginPlayer("testuser", "testpassword");
    }

    @Test
    void testMain_InvalidLogin() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock user input for invalid login
        withTextFromSystemIn("y", "testuser", "wrongpassword")
                .execute(() -> {
                    // Mock PlayerRepository behavior
                    when(playerRepository.loginPlayer("testuser", "wrongpassword")).thenReturn(null);

                    // Capture console output
                    String output = tapSystemOutNormalized(() -> {
                        App.main(new String[]{});
                    });

                    // Verify output
                    assertTrue(output.contains("Welcome to Video Poker!"));
                    assertTrue(output.contains("Invalid username or password."));
                });

        // Verify PlayerRepository interactions
        verify(playerRepository, times(1)).loginPlayer("testuser", "wrongpassword");
    }

    @Test
    void testMain_DuplicateRegistration() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock user input for duplicate registration
        withTextFromSystemIn("n", "testuser", "testpassword")
                .execute(() -> {
                    // Mock PlayerRepository behavior
                    when(playerRepository.registerPlayer("testuser", "testpassword")).thenReturn(false);

                    // Capture console output
                    String output = tapSystemOutNormalized(() -> {
                        App.main(new String[]{});
                    });

                    // Verify output
                    assertTrue(output.contains("Welcome to Video Poker!"));
                    assertTrue(output.contains("Registration failed. Username may already exist."));
                });

        // Verify PlayerRepository interactions
        verify(playerRepository, times(1)).registerPlayer("testuser", "testpassword");
    }
}