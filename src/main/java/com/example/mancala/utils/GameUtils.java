package com.example.mancala.utils;

import com.example.mancala.model.Turn;
import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class GameUtils {

    public static Turn getRandomTurn() {
        return Turn.values()[new Random().nextInt(Turn.values().length - 1)];
    }

    public static Turn getOppositeTurn(Turn turn) {
        return Turn.PLAYER_ONE.equals(turn) ? Turn.PLAYER_TWO : Turn.PLAYER_ONE;
    }
}
