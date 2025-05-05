package sk.tuke.kpi.kp.slitherlink.consoleui;

import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.kpi.kp.slitherlink.Entity.Comment;
import sk.tuke.kpi.kp.slitherlink.Entity.Rating;
import sk.tuke.kpi.kp.slitherlink.Entity.Score;
import sk.tuke.kpi.kp.slitherlink.Service.CommentService;
import sk.tuke.kpi.kp.slitherlink.Service.RatingService;
import sk.tuke.kpi.kp.slitherlink.Service.ScoreService;
import sk.tuke.kpi.kp.slitherlink.core.Field;
import java.util.Date;

public class ConsoleUI {
    private Field field;
    public ConsoleUI(Field field) {
        this.field = field;
    }

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;

    public void start() {

        scoreService.addScore(new Score("player1", "game1", 100, new Date()));
        commentService.addComment(new Comment("game1", "player1", "Great game!", new Date()));
        ratingService.setRating(new Rating("game1", "player1", 5, new Date()));
    }
    public static void printInvalidName() {
        System.out.println("\033[91m❌ The name cannot be empty. Please try again.\033[0m");
    }
    public static void printInstructions() {
        System.out.println("\n\033[93m📘 Instructions for beginners:\033[0m\033[97m");

        System.out.println("""
    🔹 In Slitherlink, you have to connect the lines around the cells to form one closed ring.
    🔹 Each cell has a number (0-3) that indicates the number of lines around it.
                
    📋 \033[93m Control commands:\033[0m\033[97m
       S12N - put a line from cell (1,2) to the north (N)
       U34E - remove line from cell (3,4) to the east (E)
       B21W - block the line
       L12 - check the number of lines and how many are needed
       C - check the entire solution
       R - restart
       E - exit

    🧭 \033[93mDirections:\033[0m\033[97m
      N - North (up), S - South (down),
      W - West (left), E - East (right)

    🔔 \033[93mCommands can be entered in any case (s12n or S12N).
                 Good luck in the game!!!\033[0m 🤞
    """);
    }
    public static void printHintsDisabled() {
        System.out.println("🔒\033[91m Tooltips are disabled. The command is not available in this mode.\033[0m");
    }


    public static void printGreeting(String name) {
        System.out.println("\n\033[92m👋 Welcome, " + name + "! We wish you a pleasant game!\033[0m\n");
    }

    public static void printGameTitle() {
        System.out.println("\033[38;5;213m");
        System.out.println(" ███████  ██      ██ ████████ ██   ██ ███████ ██████  ██       ██ ███    ██ ██   ██");
        System.out.println(" ██       ██      ██    ██    ██   ██ ██      ██   ██ ██       ██ ████   ██ ██  ██");
        System.out.println(" ███████  ██      ██    ██    ███████ █████   ██████  ██       ██ ██ ██  ██ ████");
        System.out.println("      ██  ██      ██    ██    ██   ██ ██      ██   ██ ██       ██ ██  ██ ██ ██  ██");
        System.out.println(" ███████  ███████ ██    ██    ██   ██ ███████ ██   ██ ███████  ██ ██   ████ ██   ██");
        System.out.println("\033[0m");

    }

    public static void printWelcomeMessage() {
        System.out.println("\033[93m\033[1mChoose a difficulty level: 1 for Easy, 2 for Medium, 3 for Hard:");
        System.out.println("\033[97m1. \033[1mEasy");
        System.out.println("\033[97m2. \033[1mMedium");
        System.out.println("\033[97m3. \033[1mHard\033[0m");
    }

    public static void printInvalidDifficulty() {
        System.out.println("\033[91m❌ Wrong choice!\033[0m\n");
    }

    public static void printCommandHelp() {
        System.out.println("\n\n\033[91m> \033[93m\033[1m(E - exit \"(E)\"  R - restart \"(R)\"  S  - select \"(S12N)\"  U - unselect \"(U12N)\"  B - block \"(B12N)\"  C - enter comment F - enter rating L - check solution for cell\"(L11)\")\033[0m");
    }

    public static void printExitMessage() {
        System.out.println("\033[91m>\033[0m \033[93m\033[1mExiting game... thx for game! 👋\033[0m");
    }

    public static void printRestartMessage() {
        System.out.println("\033[91m>\033[0m \033[93m\033[1m\uD83D\uDD04 Restarting game...\033[0m\n");
    }

    public static void printInvalidCommand() {
        System.out.println("\033[91m❌ Wrong command! It must be: S12N, U34S, B56W або L12, C\033[0m\n");
    }

    public static void printCheckStart() {
        System.out.println("\n\033[97m🔎 Checking the field...\033[0m");
    }
    public static void printSolutionCheckStart() {
        System.out.println("\n\033[97m📋 Checking the solution...\033[0m");
    }

    public static void printVictoryMessage(long score, String formattedTime) {
        System.out.println("🎉 You won! 🎉");
        System.out.println("⏱ Your score: " + formattedTime + " (" + score + " seconds)");
    }

    public static void printCommentThankYou(String comment) {
        System.out.println("\n\033[97m💬 Thank you for your comment!\033[0m"); // Зелене повідомлення
        System.out.println("\033[97m📝 You write: \033[1m\"\033[93m" + comment + "\"\033[0m"); // Білий текст + жирний коментар
    }

    public static void printRatingThankYou(int rating) {
        System.out.println("\n\033[97m🌟 Thank you for your feedback!\033[0m"); // Жовтий колір
        System.out.print("🎮 \033[97mYou have rated the game as follows: \033[0m");

        // Відображаємо зірочки відповідно до рейтингу
        for (int i = 0; i < rating; i++) {
            System.out.print("⭐");
        }

        System.out.println("\033[97m (\033[93m" + rating + "/5)\033[0m");
    }

    public static void printLineRemoval(String direction, int row, int col) {
        System.out.printf("\n\033[97m🛠 Remove a line in the %s direction from a cell (%d, %d)\033[0m%n",
                direction, row + 1, col + 1);
    }

    public static void printAllLinesCleared() {
        System.out.println("\n\n\033[91m🔄 All lines have been cleared!\033[0m");
    }

    public static void printInvalidCoordinates(int row, int col) {
        System.out.println("\033[91m❌ Incorrect coordinates (" + (row + 1) + ", " + (col + 1) + ")\033[0m");
    }
    public static void printLineAlreadyExists(String direction) {
        System.out.println("\033[91m❌ The line already exists in the direction of " + direction + ".\033[0m");
    }
    public static void printLineCheck(int row, int col, int actual, int expected) {
        String status = actual == expected ? "\033[92m✅ That's right.\033[0m" : "\033[91m❌ Incorrect\033[0m";

        System.out.printf("🔍\033[97m Cell (%d, %d): Lines = %d, Expected = %d → %s\033[0m\n",
                row + 1, col + 1, actual, expected, status);
    }


    public void play() {
    }
}
