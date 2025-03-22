package sk.tuke.kpi.kp.slitherlink.Tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.slitherlink.core.Maps;

public class MapsTests {

    @Test
    public void testGetMapValidEasy() {
        String[][] map = Maps.getMap(1, 0);
        Assertions.assertNotNull(map, "Map should not be null for difficulty 1 and index 0");
    }

    @Test
    public void testGetMapInvalidDifficulty() {
        String[][] map = Maps.getMap(4, 0);
        Assertions.assertNull(map, "Map should be null for an invalid difficulty level");
    }

    @Test
    public void testGetWinningMapValidMedium() {
        String[][] winMap = Maps.getWinningMap(2, 0);
        Assertions.assertNotNull(winMap, "Winning map should not be null for difficulty 2 and index 0");
    }

    @Test
    public void testGetWinningMapOutOfBounds() {
        String[][] winMap = Maps.getWinningMap(1, 10);
        Assertions.assertNull(winMap, "Winning map should be null for an out-of-bounds index");
    }

    @Test
    public void testGetMapCountHard() {
        int count = Maps.getMapCount(3);
        Assertions.assertEquals(6, count, "There should be 6 maps for difficulty level 3 (hard)");
    }

    @Test
    public void testGetMapCountInvalidDifficulty() {
        int count = Maps.getMapCount(5);
        Assertions.assertEquals(0, count, "Map count should be 0 for an invalid difficulty level");
    }
}
