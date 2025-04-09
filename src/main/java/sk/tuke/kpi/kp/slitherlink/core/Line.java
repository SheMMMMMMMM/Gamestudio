package sk.tuke.kpi.kp.slitherlink.core;

import sk.tuke.kpi.kp.slitherlink.consoleui.ConsoleUI;

public class Line {
    private final boolean[][][] lines;
    private final int rows, cols;

    public Line(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.lines = new boolean[rows][cols][4];
    }

    public boolean addLine(int row, int col, String direction) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            ConsoleUI.printInvalidCoordinates(row, col);
            return false;
        }

        int dir = getDirectionFromString(direction);
        if (lines[row][col][dir]) {
            ConsoleUI.printLineAlreadyExists(direction);
            return false;
        }
        lines[row][col][dir] = true;
        return true;
    }

    public boolean delLine(int row, int col, String direction) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            ConsoleUI.printInvalidCoordinates(row, col);
            return false;
        }

        int dir = getDirectionFromString(direction);
        if (!lines[row][col][dir]) {
            ConsoleUI.printLineAlreadyExists(direction);
            return false;
        }

        lines[row][col][dir] = false;
        return true;
    }

    private int getDirectionFromString(String direction) {
        return switch (direction) {
            case "N" -> 0; // Північ
            case "S" -> 1; // Південь
            case "W" -> 2; // Захід
            case "E" -> 3; // Схід
            default -> -1;
        };
    }

    public boolean[][][] getLines() {
        return lines;
    }
}
