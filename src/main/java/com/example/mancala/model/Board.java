package com.example.mancala.model;

import com.example.mancala.utils.GameUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Board {

    private Pit playerOneBigPit;
    private List<Pit> playerOneLittlePits;

    private Pit playerTwoBigPit;
    private List<Pit> playerTwoLittlePits;

    private Turn turn;

    public Board() {
    }

    public void initializeBoard(int pitsPerPlayer, int stonesPerPit) {
        playerOneBigPit = new Pit();
        playerTwoBigPit = new Pit();

        playerOneLittlePits = initializeLittlePits(pitsPerPlayer, stonesPerPit);
        playerTwoLittlePits = initializeLittlePits(pitsPerPlayer, stonesPerPit);

        turn = GameUtils.getRandomTurn();
    }

    private List<Pit> initializeLittlePits(int pitsPerPlayer, int stonesPerPit) {
        List<Pit> littlePits = new ArrayList<>();
        for (int i = 0; i < pitsPerPlayer; i++) {
            littlePits.add(new Pit(stonesPerPit));
        }
        return littlePits;
    }
}
