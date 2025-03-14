package com.tales.koreanvp;

import com.tales.koreanvp.game.VideoPokerGame;
import com.tales.koreanvp.model.Player;
import com.tales.koreanvp.repo.PlayerRepository;
import com.tales.koreanvp.service.DatabaseConnection;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Video Poker!");

        System.out.print("Do you have an account? (y/n): ");
        String hasAccount = scanner.nextLine().toLowerCase();

        PlayerRepository playerRepository = new PlayerRepository();
        Player player = null;

        if (hasAccount.equals("y")) {
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();
            player = playerRepository.loginPlayer(username, password);
            if (player == null) {
                System.out.println("Invalid username or password.");
                return;
            }
        } else {
            System.out.print("Enter a new username: ");
            String username = scanner.nextLine();
            System.out.print("Enter a new password: ");
            String password = scanner.nextLine();
            if (playerRepository.registerPlayer(username, password)) {
                player = playerRepository.loadPlayer(username);
            } else {
                System.out.println("Registration failed. Username may already exist.");
                return;
            }
        }

        System.out.println("\nWelcome, " + player.getUsername() + "!");
        System.out.println("Your current credits: " + player.getCredits());

        VideoPokerGame game = new VideoPokerGame(player, playerRepository);
        while(true) {
            game.play();
        }
    }
}