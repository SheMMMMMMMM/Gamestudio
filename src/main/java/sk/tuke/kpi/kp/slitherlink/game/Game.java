package sk.tuke.kpi.kp.slitherlink.game;


import sk.tuke.kpi.kp.slitherlink.Entity.Comment;
import sk.tuke.kpi.kp.slitherlink.Entity.Rating;
import sk.tuke.kpi.kp.slitherlink.Entity.Score;
import sk.tuke.kpi.kp.slitherlink.Service.*;
import sk.tuke.kpi.kp.slitherlink.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.slitherlink.core.Field;
import sk.tuke.kpi.kp.slitherlink.core.Maps;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Field field;
    private long startTime;
    private long score;
    boolean gameInProgress = true;
    private boolean hintsEnabled = false;
    private final ScoreService scoreService = new ScoreServiceJDBC();
    private final CommentService commentService = new CommentServiceJDBC();
    private final RatingService ratingService = new RatingServiceJDBC();
    private String playerName;
    public Game() {

    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        ConsoleUI.printGameTitle();
        playerName = "";
        while (playerName.isBlank()) {
            System.out.print("\033[96m👤 Please enter your name: \033[0m");
            playerName = scanner.nextLine().trim();

            if (playerName.isBlank()) {
                ConsoleUI.printInvalidName();
            }
        }

        ConsoleUI.printGreeting(playerName);

        System.out.print("\n\033[96m📘 Want to read the game rules and controls❓ (yes/no): \033[0m");
        String showRules = scanner.nextLine().trim().toLowerCase();

        if (showRules.equals("yes")) {
            ConsoleUI.printInstructions();
        }
        System.out.print("\n\033[96m💡 Want to play with hints (L and C commands are allowed)❓ (yes/no): \033[0m");
        String hints = scanner.nextLine().trim().toLowerCase();

        hintsEnabled = hints.equals("yes");

        printCommentAndRating();
        printTopPlayers();
        int difficulty;

        while (true) {
            ConsoleUI.printWelcomeMessage();
            try {
                difficulty = Integer.parseInt(scanner.nextLine().trim());

                if (difficulty >= 1 && difficulty <= 3) {
                    break;
                } else {
                    ConsoleUI.printInvalidDifficulty();
                }
            } catch (NumberFormatException e) {
                ConsoleUI.printInvalidDifficulty();
            }
        }


        generateFieldByDifficulty(difficulty);

        if (field != null) {
            field.printField();
        }

        startTime = System.currentTimeMillis();

        while (gameInProgress) {
            long elapsedTime = System.currentTimeMillis() - startTime;

            long minutes = (elapsedTime / 1000) / 60;
            long seconds = (elapsedTime / 1000) % 60;

            System.out.print("\033[38;5;202m\rTime : " + String.format("%02d:%02d\033[0m", minutes, seconds));

            ConsoleUI.printCommandHelp();
            String command = scanner.next();

            if (command.equalsIgnoreCase("E")) {
                ConsoleUI.printExitMessage();
                gameInProgress = false;
                break;
            }
            if (command.equalsIgnoreCase("R")) {
                ConsoleUI.printRestartMessage();
                System.out.println();
                field.clearMap();
                start();
            }

            processCommand(command);
        }

    }
    public void printCommentAndRating() {
        System.out.println();
        List<Comment> comments = commentService.getComments("Slitherlink");

        for (Comment comment : comments) {
            String playerName = comment.getPlayer();
            String commentText = comment.getComment();
            String date = (comment.getDate() != null) ? comment.getDate().toString() : "Дата не задана";
            int rating = ratingService.getRating("Slitherlink", playerName);
            System.out.printf("Player %s commented \"%s\" on %s and rated %d\n", playerName, commentText, date, rating);
        }
        System.out.println();
    }
    public void printTopPlayers() {
        System.out.println("\n🏆 Top Players (Lowest Score First):");
        List<Score> topScores = scoreService.getTopScores("Slitherlink");

        if (topScores.isEmpty()) {
            System.out.println("No scores available.");
            return;
        }

        topScores.sort((score1, score2) -> Integer.compare(score1.getPoints(), score2.getPoints()));

        for (Score score : topScores) {
            String playerName = score.getPlayer();
            int points = score.getPoints();
            String date = score.getPlayedOn().toString();

            System.out.printf("👤 %s | 🕹 %d points | 📅 %s\n", playerName, points, date);
        }
        System.out.println();
    }



    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
    public void printRating(){
        Scanner scanner = new Scanner(System.in);
        int rating = -1;
        while (rating < 0 || rating > 5) {
            System.out.println("🔢 \033[38;5;158mEnter a rating from 0 to 5:\033[0m");
            try {
                rating = Integer.parseInt(scanner.nextLine().trim());
                if (rating < 0 || rating > 5) {
                    System.out.println("❌ \033[38;5;158mInvalid value! Enter a number between 0 and 5.\033[0m");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ \033[38;5;158mPlease enter a number!\033[0m");
            }
        }
        ConsoleUI.printRatingThankYou(rating);
        ratingService.setRating(new Rating("Slitherlink", playerName, rating, new Date()));
        System.out.println("⭐ Average rating: " + ratingService.getAverageRating("Slitherlink"));

    }

    public void printComment(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("✍ \033[38;5;158mEnter your comment:\033[0m");
        String comment = scanner.nextLine();
        ConsoleUI.printCommentThankYou(comment);
        commentService.addComment(new Comment("Slitherlink", playerName, comment, new Date()));
    }
    public void processCommand(String command) {
        if (command == null || command.isEmpty()) {
            return;
        }

        command = command.toUpperCase();

//        if (command.equals("C")) {
//            if (!hintsEnabled) {
//                ConsoleUI.printHintsDisabled();
//                return;
//            }
//            field.checkSolution();
//            return;
//        }

        if((command.startsWith("C"))){
            printComment();
        }
        if((command.startsWith("F"))){
            printRating();
        }
        if (command.startsWith("L")) {
            if (!hintsEnabled) {
                ConsoleUI.printHintsDisabled();
                return;
            }

            if (command.length() != 3) {
                ConsoleUI.printInvalidCommand();
                return;
            }

            try {
                int row = Integer.parseInt(command.substring(1, 2)) - 1;
                int col = Integer.parseInt(command.substring(2, 3)) - 1;

                int actualLines = field.countLinesAroundCell(row, col);
                String[][] winningMap = Maps.getWinningMap(field.getDifficulty(), field.getMapIndex());
                String value = winningMap[row][col];

                if (!value.equals("•")) {
                    int expectedLines = Integer.parseInt(value);
                    ConsoleUI.printLineCheck(row, col, actualLines, expectedLines);
                }
            } catch (Exception e) {
                System.out.println("❌ Error entering coordinates for the L command.");
            }
            return;
        }

        if (command.length() < 4) {
            ConsoleUI.printInvalidCommand();
            return;
        }

        char action = command.charAt(0);
        int row, col;
        String direction;

        try {
            row = Integer.parseInt(command.substring(1, 2)) - 1;
            col = Integer.parseInt(command.substring(2, 3)) - 1;
            direction = command.substring(3);

            if (!direction.matches("[NSEW]")) {
                ConsoleUI.printInvalidCommand();
                return;
            }
        } catch (Exception e) {
            ConsoleUI.printInvalidCommand();
            return;
        }

        switch (action) {
            case 'S':
                field.drawLine(row, col, direction);
                break;
            case 'U':
                field.removeLine(row, col, direction);
                break;
            case 'B':
                field.blockLine(row, col, direction);
                break;
            default:
                ConsoleUI.printInvalidCommand();
                return;
        }

        field.printField();

        if (field.checkVictory()) {
            gameInProgress = false;
            score = (System.currentTimeMillis() - startTime) / 1000;
            ConsoleUI.printVictoryMessage(score, formatTime(score));
            scoreService.addScore(new Score("Slitherlink", playerName, (int) score, new Date()));


            System.out.println("\n🏆 Top Scores:");
            for (Score s : scoreService.getTopScores("Slitherlink")) {
                System.out.printf("👤 %s | 🕹 %d points | 📅 %s\n", s.getPlayer(), s.getPoints(), s.getPlayedOn());
            }


            Scanner scanner = new Scanner(System.in);

            System.out.println("\n📝\033[38;5;158m Want to leave a comment❓ (yes/no)\033[0m");
            String commentChoice = scanner.nextLine().trim().toLowerCase();

            if (commentChoice.equals("yes")) {
                System.out.println("✍ \033[38;5;158mEnter your comment:\033[0m");
                String comment = scanner.nextLine();
                ConsoleUI.printCommentThankYou(comment);
                commentService.addComment(new Comment("Slitherlink", playerName, comment, new Date()));
            }

            System.out.println("\n⭐ \033[38;5;158mWant to give the game a rating?❓ (yes/no)\033[0m");
            String ratingChoice = scanner.nextLine().trim().toLowerCase();

            if (ratingChoice.equals("yes")) {
                int rating = -1;
                while (rating < 0 || rating > 5) {
                    System.out.println("🔢 \033[38;5;158mEnter a rating from 0 to 5:\033[0m");
                    try {
                        rating = Integer.parseInt(scanner.nextLine().trim());
                        if (rating < 0 || rating > 5) {
                            System.out.println("❌ \033[38;5;158mInvalid value! Enter a number between 0 and 5.\033[0m");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ \033[38;5;158mPlease enter a number!\033[0m");
                    }
                }
                ConsoleUI.printRatingThankYou(rating);

                ratingService.setRating(new Rating("Slitherlink", playerName, rating, new Date()));
                System.out.println("⭐ Average rating: " + ratingService.getAverageRating("Slitherlink"));

            }

            System.out.println("\n🔄 Want to continue the game❓ (yes/no)");
            String playAgainChoice = scanner.nextLine().trim().toLowerCase();

            if (playAgainChoice.equals("yes")) {
                field.clearMap();
                start();
            } else if (playAgainChoice.equals("no")){
                ConsoleUI.printExitMessage();
                System.exit(0);
            }
        }

    }

    private void generateFieldByDifficulty(int difficulty) {
        Random rand = new Random();

        int maxMaps = Maps.getMapCount(difficulty);
        if (maxMaps == 0) {
            return;
        }

        int mapIndex = rand.nextInt(maxMaps);

        String[][] map = Maps.getMap(difficulty, mapIndex);
        if (map == null) {
            return;
        }

        String[][] horizontalLines = Maps.HorizontalLines[0];
        String[][] verticalLines = Maps.VerticalLines[0];

        field = new Field(map.length, map[0].length, horizontalLines, verticalLines);

        field.setMapIndex(mapIndex);
        field.setDifficulty(difficulty);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                field.setCell(i, j, String.valueOf(map[i][j]));
            }
        }
    }
}

