package sk.tuke.kpi.kp.slitherlink.Tests;

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
        assertEquals(0, lines);
    }

    @Test
    public void testDrawLineInAllDirections() {
        assertTrue(field.drawLine(1, 1, "N"));
        assertTrue(field.drawLine(1, 1, "S"));
        assertTrue(field.drawLine(1, 1, "E"));
        assertTrue(field.drawLine(1, 1, "W"));

        assertEquals(4, field.countLinesAroundCell(1, 1));
    }

    @Test
    public void testRemoveLineFromCell() {
        field.drawLine(1, 1, "N");
        field.removeLine(1, 1, "N");

        assertEquals(0, field.countLinesAroundCell(1, 1));
    }

    @Test
    public void testClearMapResetsAllLines() {
        field.drawLine(0, 0, "S");
        field.drawLine(1, 1, "E");

        field.clearMap();

        assertEquals(0, field.countLinesAroundCell(0, 0));
        assertEquals(0, field.countLinesAroundCell(1, 1));
    }


    @Test
    public void testSetAndGetCellValue() {
        field.setCell(2, 2, "3");
        assertEquals("3", getCellValue(field, 2, 2));
    }

    @Test
    public void testCheckVictoryReturnsFalseIfWrong() {
        field.setDifficulty(1);
        field.setMapIndex(0);

        assertFalse(field.checkVictory(123));
    }

    private String getCellValue(Field field, int row, int col) {
        try {
            var fieldValues = Field.class.getDeclaredField("fieldValues");
            fieldValues.setAccessible(true);
            String[][] values = (String[][]) fieldValues.get(field);
            return values[row][col];
        } catch (Exception e) {
            fail("Error accessing fieldValues: " + e.getMessage());
            return null;
        }
    }
}


