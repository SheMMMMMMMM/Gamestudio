package sk.tuke.kpi.kp.slitherlink.Tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.slitherlink.core.Cell;

import static org.junit.jupiter.api.Assertions.*;

public class CellTests {

    @Test
    public void testCellNumberGetterAndSetter() {
        Cell cell = new Cell(2);
        Assertions.assertEquals(2, cell.getNumber());

        cell.setNumber(5);
        Assertions.assertEquals(5, cell.getNumber());
    }
}
