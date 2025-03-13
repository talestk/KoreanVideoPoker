package com.tales.koreanvp.game;

import com.tales.koreanvp.model.Player;
import com.tales.koreanvp.repo.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VideoPokerGameTest {

    @Mock
    private PlayerRepository playerRepository;

    private VideoPokerGame videoPokerGame;
    private Player player;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        player = new Player("testuser", 100, 0, 0);
        videoPokerGame = new VideoPokerGame(player, playerRepository);
    }

    @Test
    void testEvaluateHand_RoyalFlush() {
        List<Map<String, String>> hand = new ArrayList<>();
        hand.add(createCard("10", "Hearts"));
        hand.add(createCard("J", "Hearts"));
        hand.add(createCard("Q", "Hearts"));
        hand.add(createCard("K", "Hearts"));
        hand.add(createCard("A", "Hearts"));

        String result = videoPokerGame.evaluateHand(hand);
        assertEquals("Royal Flush", result, "Expected Royal Flush");
    }

    @Test
    void testEvaluateHand_StraightFlush() {
        List<Map<String, String>> hand = new ArrayList<>();
        hand.add(createCard("9", "Diamonds"));
        hand.add(createCard("10", "Diamonds"));
        hand.add(createCard("J", "Diamonds"));
        hand.add(createCard("Q", "Diamonds"));
        hand.add(createCard("K", "Diamonds"));

        String result = videoPokerGame.evaluateHand(hand);
        assertEquals("Straight Flush", result, "Expected Straight Flush");
    }

    @Test
    void testEvaluateHand_FourOfAKind() {
        List<Map<String, String>> hand = new ArrayList<>();
        hand.add(createCard("5", "Spades"));
        hand.add(createCard("5", "Hearts"));
        hand.add(createCard("5", "Diamonds"));
        hand.add(createCard("5", "Clubs"));
        hand.add(createCard("2", "Spades"));

        String result = videoPokerGame.evaluateHand(hand);
        assertEquals("Four of a Kind", result, "Expected Four of a Kind");
    }

    @Test
    void testEvaluateHand_FullHouse() {
        List<Map<String, String>> hand = new ArrayList<>();
        hand.add(createCard("7", "Spades"));
        hand.add(createCard("7", "Hearts"));
        hand.add(createCard("7", "Diamonds"));
        hand.add(createCard("4", "Clubs"));
        hand.add(createCard("4", "Diamonds"));

        String result = videoPokerGame.evaluateHand(hand);
        assertEquals("Full House", result, "Expected Full House");
    }

    @Test
    void testEvaluateHand_Flush() {
        List<Map<String, String>> hand = new ArrayList<>();
        hand.add(createCard("2", "Hearts"));
        hand.add(createCard("5", "Hearts"));
        hand.add(createCard("7", "Hearts"));
        hand.add(createCard("9", "Hearts"));
        hand.add(createCard("Q", "Hearts"));

        String result = videoPokerGame.evaluateHand(hand);
        assertEquals("Flush", result, "Expected Flush");
    }

    @Test
    void testEvaluateHand_Straight() {
        List<Map<String, String>> hand = new ArrayList<>();
        hand.add(createCard("3", "Spades"));
        hand.add(createCard("4", "Diamonds"));
        hand.add(createCard("5", "Hearts"));
        hand.add(createCard("6", "Clubs"));
        hand.add(createCard("7", "Spades"));

        String result = videoPokerGame.evaluateHand(hand);
        assertEquals("Straight", result, "Expected Straight");
    }

    @Test
    void testEvaluateHand_ThreeOfAKind() {
        List<Map<String, String>> hand = new ArrayList<>();
        hand.add(createCard("J", "Spades"));
        hand.add(createCard("J", "Hearts"));
        hand.add(createCard("J", "Diamonds"));
        hand.add(createCard("4", "Clubs"));
        hand.add(createCard("7", "Spades"));

        String result = videoPokerGame.evaluateHand(hand);
        assertEquals("Three of a Kind", result, "Expected Three of a Kind");
    }

    @Test
    void testEvaluateHand_TwoPair() {
        List<Map<String, String>> hand = new ArrayList<>();
        hand.add(createCard("8", "Spades"));
        hand.add(createCard("8", "Hearts"));
        hand.add(createCard("4", "Diamonds"));
        hand.add(createCard("4", "Clubs"));
        hand.add(createCard("2", "Spades"));

        String result = videoPokerGame.evaluateHand(hand);
        assertEquals("Two Pair", result, "Expected Two Pair");
    }

    @Test
    void testEvaluateHand_JacksOrBetter() {
        List<Map<String, String>> hand = new ArrayList<>();
        hand.add(createCard("J", "Spades"));
        hand.add(createCard("J", "Hearts"));
        hand.add(createCard("3", "Diamonds"));
        hand.add(createCard("5", "Clubs"));
        hand.add(createCard("7", "Spades"));

        String result = videoPokerGame.evaluateHand(hand);
        assertEquals("Jacks or Better", result, "Expected Jacks or Better");
    }

    @Test
    void testEvaluateHand_NoWin() {
        List<Map<String, String>> hand = new ArrayList<>();
        hand.add(createCard("2", "Spades"));
        hand.add(createCard("4", "Hearts"));
        hand.add(createCard("6", "Diamonds"));
        hand.add(createCard("8", "Clubs"));
        hand.add(createCard("10", "Spades"));

        String result = videoPokerGame.evaluateHand(hand);
        assertEquals("No Win", result, "Expected No Win");
    }

    // Helper method to create a card
    private Map<String, String> createCard(String rank, String suit) {
        Map<String, String> card = new HashMap<>();
        card.put("rank", rank);
        card.put("suit", suit);
        return card;
    }
}