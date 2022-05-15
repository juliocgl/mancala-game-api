package com.example.mancala.model;

import org.junit.jupiter.api.Test;

import static com.example.mancala.utils.TestConstants.EMPTY_STONES;
import static com.example.mancala.utils.TestConstants.INITIAL_STONES;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PitTest {

    @Test
    void testPickStones() {
        Pit pit = new Pit(INITIAL_STONES);
        assertEquals(INITIAL_STONES, pit.getStones());
        pit.pickStones();
        assertEquals(EMPTY_STONES, pit.getStones());
    }

    @Test
    void testAddStones() {
        Pit pit = new Pit(INITIAL_STONES);
        pit.addStones(2);
        assertEquals(INITIAL_STONES + 2, pit.getStones());
    }

    @Test
    void testSow() {
        Pit pit = new Pit(INITIAL_STONES);
        pit.sow();
        assertEquals(INITIAL_STONES + 1, pit.getStones());

    }
}