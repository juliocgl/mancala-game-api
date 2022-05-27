package com.example.mancala.model;

import org.junit.jupiter.api.Test;

import static com.example.mancala.utils.TestConstants.FIVE_STONES;
import static com.example.mancala.utils.TestConstants.INITIAL_STONES;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameTest {

    @Test
    void testAddStonesBigPitPlayerOne() {
        Game game = new Game();
        game.setBigPitPlayerOne(INITIAL_STONES);
        assertEquals(INITIAL_STONES, game.getBigPitPlayerOne());
        game.addStonesBigPitPlayerOne(FIVE_STONES);
        assertEquals(INITIAL_STONES + FIVE_STONES, game.getBigPitPlayerOne());
    }

    @Test
    void testAddStonesBigPitPlayerTwo() {
        Game game = new Game();
        game.setBigPitPlayerTwo(INITIAL_STONES);
        assertEquals(INITIAL_STONES, game.getBigPitPlayerTwo());
        game.addStonesBigPitPlayerTwo(FIVE_STONES);
        assertEquals(INITIAL_STONES + FIVE_STONES, game.getBigPitPlayerTwo());
    }
}