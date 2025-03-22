package sk.tuke.kpi.kp.slitherlink.Tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.slitherlink.core.Field;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTests {

    private Field field;

    @BeforeEach
    public void setUp() {
        String[][] horizontal = new String[4][3]; // rows+1 x cols
        String[][] vertical = new String[3][4];   // rows x cols+1
        field = new Field(3, 3, horizontal, vertical);
    }

    @Test
    public void testCountLinesAroundEmptyCell() {
        int lines = field.countLinesAroundCell(1, 1);
        Assertions.assertEquals(0, lines);
    }

    @Test
    public void testDrawLineInAllDirections() {
        Assertions.assertTrue(field.drawLine(1, 1, "N"));
        Assertions.assertTrue(field.drawLine(1, 1, "S"));
        Assertions.assertTrue(field.drawLine(1, 1, "E"));
        Assertions.assertTrue(field.drawLine(1, 1, "W"));

        Assertions.assertEquals(4, field.countLinesAroundCell(1, 1));
    }

    @Test
    public void testRemoveLineFromCell() {
        field.drawLine(1, 1, "N");
        field.removeLine(1, 1, "N");

        Assertions.assertEquals(0, field.countLinesAroundCell(1, 1));
    }

    @Test
    public void testClearMapResetsAllLines() {
        field.drawLine(0, 0, "S");
        field.drawLine(1, 1, "E");

        field.clearMap();

        Assertions.assertEquals(0, field.countLinesAroundCell(0, 0));
        Assertions.assertEquals(0, field.countLinesAroundCell(1, 1));
    }


    @Test
    public void testSetAndGetCellValue() {
        field.setCell(2, 2, "3");
        Assertions.assertEquals("3", getCellValue(field, 2, 2));
    }

    @Test
    public void testCheckVictoryReturnsFalseIfWrong() {
        field.setDifficulty(1);
        field.setMapIndex(0);

        Assertions.assertFalse(field.checkVictory());
    }

    private String getCellValue(Field field, int row, int col) {
        try {
            var fieldValues = Field.class.getDeclaredField("fieldValues");
            fieldValues.setAccessible(true);
            String[][] values = (String[][]) fieldValues.get(field);
            return values[row][col];
        } catch (Exception e) {
            Assertions.fail("Error accessing fieldValues: " + e.getMessage());
            return null;
        }
    }
}


