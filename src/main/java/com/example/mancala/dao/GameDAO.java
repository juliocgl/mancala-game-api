package com.example.mancala.dao;

import com.example.mancala.configuration.GameConfig;
import com.example.mancala.model.Board;
import com.example.mancala.model.Pit;
import com.example.mancala.model.Turn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GameDAO {

    private final GameConfig gameConfig;

    Board board;

    public GameDAO(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
        this.board = new Board();
    }

    public void initializeBoard() {
        board.initializeBoard(gameConfig.getLittlePitsPerPlayer(), gameConfig.getInitialStonesPerPit());
    }

    public Pit getPlayerOneBigPit() {
        return board.getPlayerOneBigPit();
    }

    public Pit getPlayerTwoBigPit() {
        return board.getPlayerTwoBigPit();
    }

    public List<Pit> getPlayerOneLittlePits() {
        return board.getPlayerOneLittlePits();
    }

    public List<Pit> getPlayerTwoLittlePits() {
        return board.getPlayerTwoLittlePits();
    }

    public List<Integer> getStonesInPlayerOnePits() {
        List<Integer> result = new ArrayList<>();
        result.add(board.getPlayerOneBigPit().getStones());
        result.addAll(board.getPlayerOneLittlePits().stream().map(Pit::getStones).collect(Collectors.toList()));
        return result;
    }

    public List<Integer> getStonesInPlayerTwoPits() {
        List<Integer> result = new ArrayList<>();
        result.add(board.getPlayerTwoBigPit().getStones());
        result.addAll(board.getPlayerTwoLittlePits().stream().map(Pit::getStones).collect(Collectors.toList()));
        return result;
    }

    public Turn getTurn() {
        return board.getTurn();
    }

    public void setTurn(Turn turn) {
        board.setTurn(turn);
    }

    public Pit getLittlePit(Turn player, Integer pitIndex) {
        return Turn.PLAYER_ONE.equals(player) ? board.getPlayerOneLittlePits().get(pitIndex - 1) : board.getPlayerTwoLittlePits().get(pitIndex - 1);
    }

}
