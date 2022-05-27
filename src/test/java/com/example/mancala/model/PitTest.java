package com.example.mancala.model;

import org.junit.jupiter.api.Test;

import static com.example.mancala.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PitTest {

    @Test
    void testPickStones() {
        Pit pit = new Pit();
        pit.setStones(INITIAL_STONES);
        assertEquals(INITIAL_STONES, pit.getStones());
        pit.pickStones();
        assertEquals(EMPTY_STONES, pit.getStones());
    }

    @Test
    void testSow() {
        Pit pit = new Pit();
        pit.setStones(INITIAL_STONES);
        pit.sow();
        assertEquals(INITIAL_STONES + ONE_STONE, pit.getStones());
    }
}