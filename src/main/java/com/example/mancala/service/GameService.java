package com.example.mancala.service;

import com.example.mancala.configuration.GameConfig;
import com.example.mancala.dao.GameDAO;
import com.example.mancala.exception.BadPitSelectionException;
import com.example.mancala.exception.GameNotStartedException;
import com.example.mancala.exception.InvalidMovementException;
import com.example.mancala.exception.WrongPlayerTurnException;
import com.example.mancala.model.Pit;
import com.example.mancala.model.Turn;
import com.example.mancala.utils.GameUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class GameService {

    private final GameConfig gameConfig;
    private final GameDAO gameDAO;

    GameService(GameConfig gameConfig, GameDAO gameDAO) {
        this.gameConfig = gameConfig;
        this.gameDAO = gameDAO;
    }

    public void startGame() {
        gameDAO.initializeBoard();
    }

    public void move(int pitIndex) throws GameNotStartedException, BadPitSelectionException, WrongPlayerTurnException, InvalidMovementException {
        if (isGameStarted()) {
            if (isValidPitSelection(pitIndex)) {
                if (isPitFromPlayerOne(pitIndex)) {
                    move(Turn.PLAYER_ONE, pitIndex);
                } else {
                    move(Turn.PLAYER_TWO, pitIndex - gameConfig.getLittlePitsPerPlayer());
                }
            } else {
                log.error("Pit selected {} is out of bounds", pitIndex);
                throw new BadPitSelectionException();
            }
        } else {
            log.error("Game not started");
            throw new GameNotStartedException();
        }
    }

    public List<Integer> getBoardStatus() {
        return Stream.concat(
                        gameDAO.getStonesInPlayerOnePits().stream(),
                        gameDAO.getStonesInPlayerTwoPits().stream())
                .collect(Collectors.toList());
    }

    public String getTurn() {
        return gameDAO.getTurn().getLabel();
    }

    private void move(Turn player, Integer pitIndex) throws WrongPlayerTurnException, InvalidMovementException {
        if (isValidTurn(player)) {
            pickStones(player, pitIndex);
        } else {
            log.error("Player turn incorrect. The turn is for player {}", GameUtils.getOppositeTurn(player));
            throw new WrongPlayerTurnException();
        }
    }

    private void pickStones(Turn player, Integer pitIndex) throws InvalidMovementException {
        if (isValidMovement(player, pitIndex)) {
            boolean isBigPitLast = false;
            boolean isLastPitEmpty = false;

            Integer stonesToSow = gameDAO.getLittlePit(player, pitIndex).pickStones();
            int pitToSow = pitIndex - 1;

            while (stonesToSow > 0 && pitToSow > 0) {
                isLastPitEmpty = gameDAO.getLittlePit(player, pitToSow--).sow();
                stonesToSow--;
            }

            if (stonesToSow > 0) {
                sowBigPit();
                isBigPitLast = true;
                stonesToSow--;
            } else if (isLastPitEmpty) {
                putStoneInBigPitFromEmptyLastPit(player, pitToSow + 1);
                stealOppositePlayerPit(player, pitToSow);
            }

            if (stonesToSow > 0) {
                sowStonesForOppositePlayer(GameUtils.getOppositeTurn(player), stonesToSow);
                isBigPitLast = false;
            }

            checkNextTurn(isBigPitLast);
        } else {
            log.error("Movement is invalid. The selected pit {} for player {} is empty.", pitIndex, player);
            throw new InvalidMovementException();
        }
    }

    private void checkNextTurn(boolean isBigPitLast) {
        if (isEveryPitEmpty()) {
            endGame();
        } else if (!isBigPitLast) {
            gameDAO.setTurn(GameUtils.getOppositeTurn(gameDAO.getTurn()));
        }
    }

    private void endGame() {
        if (Turn.PLAYER_ONE.equals(gameDAO.getTurn())) {
            gameDAO.getPlayerTwoBigPit().addStones(gameDAO.getPlayerTwoLittlePits().stream().mapToInt(Pit::getStones).sum());
        } else {
            gameDAO.getPlayerOneBigPit().addStones(gameDAO.getPlayerOneLittlePits().stream().mapToInt(Pit::getStones).sum());
        }
        gameDAO.setTurn(Turn.GAME_OVER);
    }


    private boolean isEveryPitEmpty() {
        if (Turn.PLAYER_ONE.equals(gameDAO.getTurn())) {
            return gameDAO.getPlayerOneLittlePits().stream().noneMatch(pit -> pit.getStones() > 0);
        } else {
            return gameDAO.getPlayerTwoLittlePits().stream().noneMatch(pit -> pit.getStones() > 0);
        }
    }

    private boolean isGameStarted() {
        return gameDAO.getTurn() != null;
    }

    private boolean isPitFromPlayerOne(int pitIndex) {
        return pitIndex <= gameConfig.getLittlePitsPerPlayer();
    }

    private boolean isValidPitSelection(int pitIndex) {
        return pitIndex <= gameConfig.getLittlePitsPerPlayer() * 2;
    }

    private boolean isValidTurn(Turn player) {
        return gameDAO.getTurn().equals(player);
    }

    private boolean isValidMovement(Turn player, Integer pitIndex) {
        return gameDAO.getLittlePit(player, pitIndex).getStones() > 0;
    }

    private void putStoneInBigPitFromEmptyLastPit(Turn player, Integer pitIndex) {
        gameDAO.getLittlePit(player, pitIndex).pickStones();

        sowBigPit();
    }

    private void sowBigPit() {
        if (Turn.PLAYER_ONE.equals(gameDAO.getTurn())) {
            gameDAO.getPlayerOneBigPit().sow();
        } else {
            gameDAO.getPlayerTwoBigPit().sow();
        }
    }

    private void sowStonesForOppositePlayer(Turn player, Integer stonesToSow) {
        int pitToSow = gameConfig.getLittlePitsPerPlayer();
        while (stonesToSow > 0 && pitToSow > 0) {
            gameDAO.getLittlePit(player, pitToSow--).sow();
            stonesToSow--;
        }
    }

    private void stealOppositePlayerPit(Turn player, Integer pitIndex) {
        if (Turn.PLAYER_ONE.equals(player)) {
            gameDAO.getPlayerOneBigPit().addStones(gameDAO.getLittlePit(Turn.PLAYER_TWO, gameConfig.getLittlePitsPerPlayer() - pitIndex).pickStones());
        } else {
            gameDAO.getPlayerTwoBigPit().addStones(gameDAO.getLittlePit(Turn.PLAYER_ONE, gameConfig.getLittlePitsPerPlayer() - pitIndex).pickStones());
        }
    }
}
