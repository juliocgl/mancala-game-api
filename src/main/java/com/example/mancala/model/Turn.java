package com.example.mancala.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This enum is used to refer to the turn of the 2 players in Mancala game
 */
@AllArgsConstructor
@Getter
public enum Turn {
    PLAYER_ONE("Player one"),
    PLAYER_TWO("Player two"),
    GAME_OVER("Game Over");

    private final String label;
}
