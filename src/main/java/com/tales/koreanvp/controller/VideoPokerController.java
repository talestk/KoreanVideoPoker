package com.tales.koreanvp.controller;

import com.tales.koreanvp.game.VideoPokerGame;
import com.tales.koreanvp.model.Player;
import com.tales.koreanvp.repo.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
@SessionAttributes({"currentPlayer", "videoPokerGame"})
public class VideoPokerController {

    @Autowired
    private PlayerRepository playerRepository;

    private VideoPokerGame videoPokerGame;
    private Player currentPlayer;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Welcome to Video Poker!");
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        currentPlayer = playerRepository.loginPlayer(username, password);
        if (currentPlayer != null) {
            videoPokerGame = new VideoPokerGame(currentPlayer, playerRepository);
            model.addAttribute("currentPlayer", currentPlayer); // Add to session
            model.addAttribute("videoPokerGame", videoPokerGame); // Add to session
            return "game";
        } else {
            model.addAttribute("error", "Invalid username or password.");
            return "index";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, Model model) {
        if (playerRepository.registerPlayer(username, password)) {
            currentPlayer = playerRepository.loadPlayer(username);
            videoPokerGame = new VideoPokerGame(currentPlayer, playerRepository);
            model.addAttribute("currentPlayer", currentPlayer); // Add to session
            model.addAttribute("videoPokerGame", videoPokerGame); // Add to session
            return "game";
        } else {
            model.addAttribute("error", "Registration failed. Username may already exist.");
            return "index";
        }
    }

    @PostMapping("/play")
    public String play(
            @RequestParam(required = false) List<Integer> discardIndices,
            @RequestParam(required = false) List<String> bonusGuesses,
            Model model,
            @ModelAttribute("currentPlayer") Player currentPlayer, // Retrieve from session
            @ModelAttribute("videoPokerGame") VideoPokerGame videoPokerGame // Retrieve from session
    ) {
        if (discardIndices != null) {
            videoPokerGame.play(discardIndices);

            // Evaluate the hand
            String result = videoPokerGame.evaluateHand(videoPokerGame.getHand());
            int bet = 1; // Assuming a fixed bet of 1 unit
            int payout = videoPokerGame.determinePayout(result, bet);

            // Check if the player qualifies for the bonus feature
            if (result.equals("Two Pair") || result.equals("Three of a Kind") ||
                    result.equals("Straight") || result.equals("Flush") ||
                    result.equals("Full House") || result.equals("Four of a Kind") ||
                    result.equals("Straight Flush") || result.equals("Royal Flush")) {
                if (bonusGuesses != null && !bonusGuesses.isEmpty()) {
                    // Apply the bonus feature
                    payout = videoPokerGame.bonusFeature(payout, bonusGuesses);
                }
            }

            // Update player credits and save
            currentPlayer.setCredits(currentPlayer.getCredits() + payout);
            playerRepository.savePlayer(currentPlayer);

            // Pass data to the template
            model.addAttribute("player", currentPlayer);
            model.addAttribute("videoPokerGame", videoPokerGame);
            model.addAttribute("payout", payout);
            model.addAttribute("result", result);
        }

        return "game";
    }

    @PostMapping("/discard")
    public String discardCards(@RequestParam(value = "discardIndices", required = false) List<Integer> discardIndices,
                               @RequestParam(value = "bet", required = true) int bet, // Get bet amount from request
                               Model model) {
        List<Map<String, String>> hand = videoPokerGame.getHand();

        if (discardIndices != null && !discardIndices.isEmpty()) {
            videoPokerGame.replaceCards(hand, discardIndices);
        }

        String result = videoPokerGame.evaluateHand(hand); // Get the hand evaluation
        int payout = videoPokerGame.determinePayout(result, bet); // Pass the correct parameters

        // Update player credits
        Player player = videoPokerGame.getCurrentPlayer();
        if (player != null) {
            player.setCredits(player.getCredits() + payout);
            if (payout > 0) {
                player.setWins(player.getWins() + 1);
            } else {
                player.setLosses(player.getLosses() + 1);
            }
        }

        model.addAttribute("videoPokerGame", videoPokerGame);
        model.addAttribute("currentPlayer", player);
        model.addAttribute("result", result);
        model.addAttribute("payout", payout);
        model.addAttribute("hand", hand);

        return "game";  // Redirect back to the game page
    }
    @PostMapping("/double-or-nothing")
    public String doubleOrNothing(Model model) {
        Player player = videoPokerGame.getCurrentPlayer();

        if (player == null) {
            return "redirect:/game";  // Ensure a valid player exists
        }

        int lastPayout = (int) model.getAttribute("payout"); // Get last payout
        if (lastPayout <= 0) {
            return "redirect:/game";  // No winnings to gamble
        }

        Random random = new Random();
        boolean win = random.nextBoolean();  // 50/50 chance

        if (win) {
            player.setCredits(player.getCredits() + lastPayout);  // Double the winnings
            model.addAttribute("doubleResult", "You won! Your payout doubled to " + (lastPayout * 2) + " credits!");
        } else {
            player.setCredits(player.getCredits() - lastPayout);  // Lose the payout
            model.addAttribute("doubleResult", "You lost! Your winnings are gone.");
        }

        model.addAttribute("currentPlayer", player);
        model.addAttribute("payout", win ? lastPayout * 2 : 0);

        return "game";  // Reload the game page with the result
    }


}