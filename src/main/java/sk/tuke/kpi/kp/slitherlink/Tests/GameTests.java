package sk.tuke.kpi.kp.slitherlink.Tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.slitherlink.game.Game;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {

    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game();
    }

    @Test
    public void testFormatTime_Zero() throws Exception {
        Method method = Game.class.getDeclaredMethod("formatTime", long.class);
        method.setAccessible(true);
        String result = (String) method.invoke(game, 0L);
        Assertions.assertEquals("00:00", result);
    }

    @Test
    public void testFormatTime_NonZero() throws Exception {
        Method method = Game.class.getDeclaredMethod("formatTime", long.class);
        method.setAccessible(true);
        String result = (String) method.invoke(game, 125L); // 2 хв 5 сек
        Assertions.assertEquals("02:05", result);
    }

}
