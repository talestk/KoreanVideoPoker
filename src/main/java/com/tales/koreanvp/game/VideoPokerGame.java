package com.tales.koreanvp.game;

import com.tales.koreanvp.model.Player;
import com.tales.koreanvp.repo.PlayerRepository;

import java.util.*;

/**
 * This game will follow a common Jacks or better Video Poker but with a twist.
 * The bonus will double the prize every time the player guess the secret card correctly.
 */
public class VideoPokerGame {
    private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private static final String[] SUITS = {"Hearts", "Diamonds", "Clubs", "Spades"};
    private List<Map<String, String>> deck;
    private Player player;
    private PlayerRepository playerRepository;

    public VideoPokerGame(Player player, PlayerRepository playerRepository) {
        this.player = player;
        this.playerRepository = playerRepository;
        initializeDeck();
    }

    private void initializeDeck() {
        deck = new ArrayList<>();
        for (String rank : RANKS) {
            for (String suit : SUITS) {
                Map<String, String> card = new HashMap<>();
                card.put("rank", rank);
                card.put("suit", suit);
                deck.add(card);
            }
        }
        Collections.shuffle(deck);
    }

    private List<Map<String, String>> dealHand() {
        List<Map<String, String>> hand = new ArrayList<>();
        if (deck.size() > 4) {
            for (int i = 0; i < 5; i++) {
                hand.add(deck.remove(0));
            }
        }
        return hand;
    }

    private void displayHand(List<Map<String, String>> hand) {
        for (int i = 0; i < hand.size(); i++) {
            System.out.println(i + ": " + hand.get(i).get("rank") + " of " + hand.get(i).get("suit"));
        }
    }

    private List<Map<String, String>> replaceCards(List<Map<String, String>> hand, List<Integer> discardIndices) {
        for (int index : discardIndices) {
            hand.set(index, deck.remove(0));
        }
        return hand;
    }

    /*
        Example Hand Evaluations
        Hand	Result
        10♥ J♥ Q♥ K♥ A♥	Royal Flush
        9♦ 10♦ J♦ Q♦ K♦	Straight Flush
        5♠ 5♥ 5♦ 5♣ 2♠	    Four of a Kind
        7♠ 7♥ 7♦ 4♣ 4♦	    Full House
        2♥ 5♥ 7♥ 9♥ Q♥	    Flush
        3♠ 4♦ 5♥ 6♣ 7♠	    Straight
        J♠ J♥ J♦ 4♣ 7♠	    Three of a Kind
        8♠ 8♥ 4♦ 4♣ 2♠	    Two Pair
        J♠ J♥ 3♦ 5♣ 7♠	    Jacks or Better
        2♠ 4♥ 6♦ 8♣ 10♠	No Win
     */
    protected String evaluateHand(List<Map<String, String>> hand) {
        // Count the number of each rank and suit in the hand
        Map<String, Integer> rankCounts = new HashMap<>();
        Map<String, Integer> suitCounts = new HashMap<>();

        for (Map<String, String> card : hand) {
            String rank = card.get("rank");
            String suit = card.get("suit");

            rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
            suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
        }

        // Check for flush (all cards of the same suit)
        boolean isFlush = suitCounts.containsValue(5);

        // Check for straight (5 consecutive ranks)
        boolean isStraight = false;
        List<Integer> rankValues = new ArrayList<>();
        for (Map<String, String> card : hand) {
            rankValues.add(Arrays.asList(RANKS).indexOf(card.get("rank")));
        }
        Collections.sort(rankValues);
        boolean isConsecutive = true;
        for (int i = 1; i < rankValues.size(); i++) {
            if (rankValues.get(i) != rankValues.get(i - 1) + 1) {
                isConsecutive = false;
                break;
            }
        }
        // Handle low straight (2, 3, 4, 5, A) first
        if (!isConsecutive && rankValues.equals(Arrays.asList(0, 1, 2, 3, 12))) {
            isConsecutive = true;
        }
        isStraight = isConsecutive;

        // Check for Royal Flush
        if (isFlush && isStraight && rankValues.contains(12)) { // 12 corresponds to Ace
            return "Royal Flush";
        }

        // Check for Straight Flush
        if (isFlush && isStraight) {
            return "Straight Flush";
        }

        // Check for Four of a Kind
        if (rankCounts.containsValue(4)) {
            return "Four of a Kind";
        }

        // Check for Full House
        if (rankCounts.containsValue(3) && rankCounts.containsValue(2)) {
            return "Full House";
        }

        // Check for Flush
        if (isFlush) {
            return "Flush";
        }

        // Check for Straight
        if (isStraight) {
            return "Straight";
        }

        // Check for Three of a Kind
        if (rankCounts.containsValue(3)) {
            return "Three of a Kind";
        }

        // Check for Two Pair
        int pairCount = 0;
        for (int count : rankCounts.values()) {
            if (count == 2) {
                pairCount++;
            }
        }
        if (pairCount == 2) {
            return "Two Pair";
        }

        // Check for Jacks or Better (Pair of Jacks, Queens, Kings, or Aces)
        for (Map.Entry<String, Integer> entry : rankCounts.entrySet()) {
            if (entry.getValue() == 2 && Arrays.asList("J", "Q", "K", "A").contains(entry.getKey())) {
                return "Jacks or Better";
            }
        }

        // No winning hand
        return "No Win";
    }

    private int determinePayout(String result, int bet) {
        Map<String, Integer> payoutTable = new HashMap<>();
        payoutTable.put("Royal Flush", 250);
        payoutTable.put("Straight Flush", 50);
        payoutTable.put("Four of a Kind", 25);
        payoutTable.put("Full House", 9);
        payoutTable.put("Flush", 6);
        payoutTable.put("Straight", 4);
        payoutTable.put("Three of a Kind", 3);
        payoutTable.put("Two Pair", 2);
        payoutTable.put("Jacks or Better", 1);
        payoutTable.put("No Win", 0);
        return payoutTable.getOrDefault(result, 0) * bet;
    }

    private int bonusFeature(int bet) {
        Scanner scanner = new Scanner(System.in);
        int multiplier = 1;
        int correctGuesses = 0;

        System.out.println("\nBonus Feature Activated!");
        System.out.println("You have a chance to double or quadruple your payout!");

        while (correctGuesses < 7) {
            Map<String, String> card = deck.remove(0);
            System.out.println("\nFace-Down Card: ???");

            System.out.print("Is the face-down card higher or lower than 7? (h/l): ");
            String guess = scanner.nextLine().toLowerCase();

            int cardRank = Arrays.asList(RANKS).indexOf(card.get("rank"));
            int sevenRank = Arrays.asList(RANKS).indexOf("7");

            if (cardRank == sevenRank) {
                System.out.println("The card is 7 of " + card.get("suit") + ". Drawing another card...");
                continue;
            }

            System.out.println("The card is " + card.get("rank") + " of " + card.get("suit") + ".");

            if ((cardRank > sevenRank && guess.equals("h")) || (cardRank < sevenRank && guess.equals("l"))) {
                System.out.println("Correct! Your bet is doubled!");
                multiplier *= 2;
                correctGuesses++;
            } else {
                System.out.println("Incorrect. Bonus feature ends.");
                break;
            }

            System.out.println("Current multiplier: " + multiplier + "x");
        }

        System.out.println("\nFinal bonus multiplier: " + multiplier + "x");
        return bet * multiplier;
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Video Poker - Jacks or Better!");

        List<Map<String, String>> hand = dealHand();
        System.out.println("\nYour hand:");
        displayHand(hand);

        System.out.print("\nEnter the indices of cards to discard (0-4, separated by spaces): ");
        String[] indices = scanner.nextLine().split(" ");
        List<Integer> discardIndices = new ArrayList<>();
        for (String index : indices) {
            discardIndices.add(Integer.parseInt(index));
        }

        hand = replaceCards(hand, discardIndices);
        System.out.println("\nYour new hand:");
        displayHand(hand);

        String result = evaluateHand(hand);
        System.out.println("\nResult: " + result);

        int bet = 1; // Assuming a bet of 1 unit for simplicity
        int payout = determinePayout(result, bet);

        if (result.equals("Two Pair") || result.equals("Three of a Kind") ||
                result.equals("Straight") || result.equals("Flush") ||
                result.equals("Full House") || result.equals("Four of a Kind") ||
                result.equals("Straight Flush") || result.equals("Royal Flush")) {
            System.out.println("\nYou qualified for the bonus feature!");
            payout = bonusFeature(payout);
        }

        player.setCredits(player.getCredits() + payout);
        playerRepository.savePlayer(player); // Save the updated player progress
        System.out.println("\nFinal Payout: " + payout + " units");
        System.out.println("Your total credits: " + player.getCredits());
    }
}