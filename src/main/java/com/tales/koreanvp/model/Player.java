package com.tales.koreanvp.model;

public class Player {
    private String username;
    private int credits;
    private int wins;
    private int losses;
    private int bet;

    public Player(String username, int credits, int wins, int losses) {
        this.username = username;
        this.credits = credits;
        this.wins = wins;
        this.losses = losses;
        this.bet = 1;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }
    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", credits=" + credits +
                ", wins=" + wins +
                ", losses=" + losses +
                '}';
    }
}