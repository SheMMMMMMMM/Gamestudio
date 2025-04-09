package sk.tuke.kpi.kp.slitherlink.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.slitherlink.core.Line;

import static org.junit.jupiter.api.Assertions.*;

public class LineTests {
    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line(3, 3);
    }

    @Test
    void testAddLineSuccessfully() {
        boolean result = line.addLine(1, 1, "N");
        assertTrue(result, "Line should be added successfully");
        assertTrue(line.getLines()[1][1][0], "Direction 'N' should be set to true");
    }

    @Test
    void testAddLineAlreadyExists() {
        line.addLine(1, 1, "E");
        boolean result = line.addLine(1, 1, "E");
        assertFalse(result, "Adding the same line again should return false");
    }

    @Test
    void testDeleteLineSuccessfully() {
        line.addLine(2, 2, "W");
        boolean result = line.delLine(2, 2, "W");
        assertTrue(result, "Line should be removed successfully");
        assertFalse(line.getLines()[2][2][2], "Direction 'W' should be set to false after deletion");
    }

    @Test
    void testDeleteNonExistentLine() {
        boolean result = line.delLine(0, 0, "S");
        assertFalse(result, "Deleting a non-existent line should return false");
    }

    @Test
    void testAddLineWithInvalidCoordinates() {
        boolean result = line.addLine(-1, 0, "N");
        assertFalse(result, "Adding a line with invalid coordinates should return false");
    }

    @Test
    void testDeleteLineWithInvalidCoordinates() {
        boolean result = line.delLine(3, 3, "E");
        assertFalse(result, "Deleting a line with invalid coordinates should return false");
    }

    @Test
    void testLinesArrayInitialization() {
        boolean[][][] linesArray = line.getLines();
        assertEquals(3, linesArray.length, "Line array should have 3 rows");
        assertEquals(3, linesArray[0].length, "Each row should have 3 columns");
        assertEquals(4, linesArray[0][0].length, "Each cell should have 4 directions");
    }
}

