package sk.tuke.kpi.kp.slitherlink.core;

import sk.tuke.kpi.kp.slitherlink.consoleui.ConsoleUI;

public class Field {
    private final int rows;
    private final int cols;

    private String[][] fieldValues;
    private boolean[][][] lines;
    private final Line lineManager;
    private final String[][] horizontalLines;
    private final String[][] verticalLines;
    private int difficulty;
    private int mapIndex;

    public Field(int rows, int cols, String[][] horizontalLines, String[][] verticalLines) {
        this.rows = rows;
        this.cols = cols;
        this.horizontalLines = horizontalLines;
        this.verticalLines = verticalLines;
        fieldValues = new String[rows][cols];
        lineManager = new Line(rows, cols);
        lines = new boolean[rows][cols][4];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                fieldValues[i][j] = null;
            }
        }
    }

    public int getMapIndex() {
        return mapIndex;
    }

    public void setMapIndex(int mapIndex) {
        this.mapIndex = mapIndex;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public boolean checkVictory() {
        ConsoleUI.printCheckStart();
        String[][] winningMap = Maps.getWinningMap(difficulty, mapIndex);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int actualLines = countLinesAroundCell(i, j);
                int expectedLines = Integer.parseInt(winningMap[i][j]);

                if (actualLines != expectedLines) {
                    return false;
                }
            }
        }

        return true;
    }

    public void checkSolution() {
        String[][] winningMap = Maps.getWinningMap(difficulty, mapIndex);

        ConsoleUI.printSolutionCheckStart();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (winningMap[i][j].equals("•")) continue;

                int expectedLines = Integer.parseInt(winningMap[i][j]);
                int actualLines = countLinesAroundCell(i, j);

                System.out.printf("\033[97m(%d, %d): Expected = %d, is = %d\033[0m", i + 1, j + 1, expectedLines, actualLines);
                if (expectedLines == actualLines) {
                    System.out.println(" ✅");
                } else {
                    System.out.println(" ❌");
                }
            }
        }
    }
    public int countLinesAroundCell(int row, int col) {
        int lineCount = 0;

        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            ConsoleUI.printInvalidCoordinates(row, col);
            return -1;
        }

        if (lines[row][col][0]) {lineCount++;}
        if (lines[row][col][1]) {lineCount++;}
        if (lines[row][col][2]) {lineCount++;}
        if (lines[row][col][3]) {lineCount++;}

        return lineCount;
    }




    public void setCell(int row, int col, String value) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            fieldValues[row][col] = value;
        }
    }
    public void removeLine(int row, int col, String direction) {
        boolean success = lineManager.delLine(row, col, direction);

        if (success) {
            removeLineColor(row, col, direction);



            switch (direction) {
                case "N":
                    if (row > 0) {
                        lines[row - 1][col][1] = false;
                        ConsoleUI.printLineRemoval(direction, row, col);
                    }
                    lines[row][col][0] = false;
                    break;

                case "S":
                    if (row < rows - 1) {
                        lines[row + 1][col][0] = false;
                        ConsoleUI.printLineRemoval(direction, row, col);
                    }
                    lines[row][col][1] = false;
                    break;

                case "W":
                    if (col > 0) {
                        lines[row][col - 1][2] = false;
                        ConsoleUI.printLineRemoval(direction, row, col);
                    }
                    lines[row][col][3] = false;
                    break;

                case "E":
                    if (col < cols - 1) {
                        lines[row][col + 1][3] = false;
                        ConsoleUI.printLineRemoval(direction, row, col);
                    }
                    lines[row][col][2] = false;
                    break;

                default:
                    break;
            }

        }
    }


    public void removeLineColor(int row, int col, String direction) {
        switch (direction) {
            case "N": // Північ
                if (row + 1 < horizontalLines.length) {
                    horizontalLines[row][col] = "───";
                }
                break;
            case "S": // Південь
                if (row >= 0) {
                    horizontalLines[row + 1][col] = "───";
                }
                break;

            case "W": // Захід
                if (col + 1 < verticalLines[row].length) {
                    verticalLines[row][col] = "|";
                }
                break;

            case "E": // Схід
                if (col + 1 >= 0) {
                    verticalLines[row][col + 1] = "|";
                }
                break;

            default:
                //System.out.println("Incorrect line direction!");
                break;
        }
    }
    public void clearMap() {
        for (int i = 0; i < horizontalLines.length; i++) {
            for (int j = 0; j < horizontalLines[i].length; j++) {
                horizontalLines[i][j] = "───";
            }
        }

        for (int i = 0; i < verticalLines.length; i++) {
            for (int j = 0; j < verticalLines[i].length; j++) {
                verticalLines[i][j] = "|";
            }
        }

        for (int i = 0; i < lines.length; i++) {
            for (int j = 0; j < lines[i].length; j++) {
                for (int d = 0; d < 4; d++) {
                    lines[i][j][d] = false;
                }
            }
        }

        ConsoleUI.printAllLinesCleared();
    }
    public boolean drawLine(int row, int col, String direction) {
        boolean success = lineManager.addLine(row, col, direction);
        if (success) {
            updateLineColor(row, col, direction);

            switch (direction) {
                case "N":
                    if (row > 0) {
                        lines[row - 1][col][1] = true;
                    }
                    lines[row][col][0] = true;
                    break;

                case "S":
                    if (row < rows - 1) {
                        lines[row + 1][col][0] = true;
                    }
                    lines[row][col][1] = true;
                    break;

                case "W":
                    if (col > 0) {
                        lines[row][col - 1][2] = true;
                    }
                    lines[row][col][3] = true;
                    break;

                case "E":
                    if (col < cols - 1) {
                        lines[row][col + 1][3] = true;
                    }
                    lines[row][col][2] = true;
                    break;
            }
        }
        return success;
    }
    public boolean blockLine(int row, int col, String direction) {
        boolean success = lineManager.addLine(row, col, direction);
        if (success) {
            updateBlockLineColor(row, col, direction);
        }
        return success;
    }

    public void updateBlockLineColor(int row, int col, String direction) {
        switch (direction) {
            case "N":
                if (row + 1 < horizontalLines.length) {
                    horizontalLines[row][col] = "\033[91m\033[1m───\033[0m";
                }
                break;
            case "S":
                if (row >= 0) {
                    horizontalLines[row + 1][col] = "\033[91m\033[1m───\033[0m";
                }
                break;

            case "W":
                if (col + 1 < verticalLines[row].length) {
                    verticalLines[row][col] = "\033[91m\033[1m|\033[0m";
                }
                break;

            case "E":
                if (col + 1 >= 0) {
                    verticalLines[row][col + 1] = "\033[91m\033[1m|\033[0m";
                }
                break;

            default:
                //System.out.println("Incorrect line direction!");
                break;
        }
    }

    public void updateLineColor(int row, int col, String direction) {
        switch (direction) {
            case "N":
                if (row + 1 < horizontalLines.length) {
                    horizontalLines[row][col] = "\033[95m\033[1m───\033[0m";
                }
                break;
            case "S":
                if (row >= 0) {
                    horizontalLines[row + 1][col] = "\033[95m\033[1m───\033[0m";
                }
                break;

            case "W":
                if (col + 1 < verticalLines[row].length) {
                    verticalLines[row][col] = "\033[95m\033[1m|\033[0m";
                }
                break;

            case "E":
                if (col + 1 >= 0) {
                    verticalLines[row][col + 1] = "\033[95m\033[1m|\033[0m";
                }
                break;

            default:
                //System.out.println("Incorrect line direction!");
                break;
        }
    }


    public void printField() {
        System.out.print("      ");
        for (int j = 0; j < cols; j++) {
            System.out.print(String.format("\033[38;5;57m\033[1m%-4d\033[0m", j + 1));
        }
        System.out.println();

        System.out.print("    ");
        for (int j = 0; j < cols; j++) {
            if (j < horizontalLines[0].length) {
                System.out.print("\033[38;5;239m+" + horizontalLines[0][j]);
            }
        }
        System.out.println("\033[38;5;239m+\033[0m");

        for (int i = 0; i < rows; i++) {
            System.out.print(String.format("\033[38;5;57m\033[1m%-2d  \033[0m", i + 1));

            for (int j = 0; j < cols; j++) {
                String cellValue = fieldValues[i][j];
                if (j < verticalLines[i].length) {
                    System.out.print("\033[38;5;239m"+ verticalLines[i][j] + "\033[0m " + String.format("%-2s", cellValue));
                }
            }

            if (cols - 1 < verticalLines[i].length) {
                System.out.print("\033[38;5;239m" + verticalLines[i][verticalLines[i].length - 1]+ "\033[0m");
            }

            System.out.println();

            if (i < rows - 1) {
                System.out.print("    ");
                for (int j = 0; j < cols; j++) {
                    if (j <= horizontalLines[i + 1].length) {
                        System.out.print("\033[38;5;239m+" + horizontalLines[i + 1][j]+ "\033[0m");
                    }
                }
                System.out.print("\033[38;5;239m+\033[0m");
                System.out.println();
            }
        }

        System.out.print("    ");
        for (int j = 0; j < cols; j++) {
            if (j < horizontalLines[rows].length) {
                System.out.print("\033[38;5;239m+" + horizontalLines[rows][j]+ "\033[0m");
            }
        }
        System.out.print("\033[38;5;239m+\033[0m");
        System.out.println();
    }
}
