package com.example.spinnerguess;

import java.util.Random;

public class GuessGame {

    public enum Difficulty {
        EASY(50, 10, 30),
        MEDIUM(100, 7, 20),
        HARD(200, 5, 10);

        private final int maxNumber;
        private final int maxAttempts;
        private final int timeSeconds;

        Difficulty(int maxNumber, int maxAttempts, int timeSeconds) {
            this.maxNumber = maxNumber;
            this.maxAttempts = maxAttempts;
            this.timeSeconds = timeSeconds;
        }

        public int getMaxNumber() {
            return maxNumber;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public int getTimeSeconds() {
            return timeSeconds;
        }
    }

    private int targetNumber;
    private int remainingAttempts;
    private boolean isGameOver;
    private Difficulty currentDifficulty;
    private int score;

    public GuessGame(Difficulty difficulty) {
        this.score = 0;
        resetGame(difficulty);
    }

    public void resetGame(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
        this.remainingAttempts = difficulty.getMaxAttempts();
        this.isGameOver = false;
        Random random = new Random();
        this.targetNumber = random.nextInt(difficulty.getMaxNumber()) + 1;
    }

    public String makeGuess(int userGuess, int remainingSeconds) {
        if (isGameOver) {
            return "Game is over! Restart to play again.";
        }

        remainingAttempts--;

        if (userGuess == targetNumber) {
            isGameOver = true;
            int roundScore = (remainingAttempts * 10) + (remainingSeconds * 5);
            score += roundScore;
            return "Correct! You won this round. Score added: " + roundScore;
        }

        if (remainingAttempts <= 0) {
            isGameOver = true;
            return "No attempts left! The correct number was " + targetNumber;
        }

        if (userGuess < targetNumber) {
            return "Too low! Try a higher number.";
        } else {
            return "Too high! Try a lower number.";
        }
    }

    public void timeOut() {
        this.isGameOver = true;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public int getScore() {
        return score;
    }
}