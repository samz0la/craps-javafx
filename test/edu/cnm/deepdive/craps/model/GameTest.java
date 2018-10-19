package edu.cnm.deepdive.craps.model;

import static org.junit.jupiter.api.Assertions.*;

import edu.cnm.deepdive.craps.model.Game.State;
import java.security.SecureRandom;
import org.junit.jupiter.api.Test;

class GameTest {
    @Test
    void testComeoutLoss() {
      for (int roll : new int[]{2, 3, 12}) {
        assertSame(State.LOSS, State.COME_OUT.roll(roll, 0));
      }
    }

    @Test
    void testComeoutWin() {
      for (int roll : new int[]{7, 11}) {
        assertSame(State.WIN, State.COME_OUT.roll(roll, 0));
      }
    }

    @Test
    void testPointPoint() {
      for (int point : new int[]{4,5, 6, 8, 9, 10}) {
        for (int roll = 2; roll <= 12; roll++) {
          if (roll != 7 && roll != point) {
            assertSame(State.POINT, State.POINT.roll(roll, point));
          }
        }
      }
    }

  @Test
  void play() {
    Game game = new Game(new SecureRandom());
    game.play();
    assertEquals(1, game.getLosses() + game.getWins());
  }

  @Test
  void reset() {
    Game game = new Game(new SecureRandom());
    game.play();
    game.reset();
    assertEquals(State.COME_OUT, game.getState());
    assertEquals(0, game.getRolls().size());

  }
}