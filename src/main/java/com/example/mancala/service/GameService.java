package com.example.mancala.service;

import com.example.mancala.configuration.GameConfig;
import com.example.mancala.exception.*;
import com.example.mancala.model.Game;
import com.example.mancala.model.Pit;
import com.example.mancala.model.Turn;
import com.example.mancala.repository.GameRepository;
import com.example.mancala.utils.GameUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class GameService {

    private final static int PLAYER_ONE = 1;
    private final static int PLAYER_TWO = 2;

    private final GameConfig gameConfig;
    private final GameRepository gameRepository;

    GameService(GameConfig gameConfig, GameRepository gameRepository) {
        this.gameConfig = gameConfig;
        this.gameRepository = gameRepository;
    }

    public Game createGame() {
        Game game = gameRepository.save(initializeNewGame());
        log.info("New game started with ID: {} - next turn: {}", game.getId(), game.getTurn().getLabel());
        return game;
    }

    public Game getGame(String gameId) {
        return gameRepository.findById(gameId).orElse(null);
    }

    public void move(String gameId, int pitIndex) throws GameNotFoundException, BadPitSelectionException, WrongPlayerTurnException, InvalidMovementException, GameOverException {
        Game game = getGameAndValidateMovement(gameId, pitIndex);

        pickStones(game, pitIndex);
    }

    private Game initializeNewGame() {
        Game game = new Game();
        game.setId(UUID.randomUUID().toString());
        game.setTurn(GameUtils.getRandomTurn());
        game.setPits(createLittlePits(game));
        return game;
    }

    private List<Pit> createLittlePits(Game game) {
        List<Pit> littlePits = new ArrayList<>();
        for (int position = 1; position <= gameConfig.getLittlePitsPerPlayer() * 2; position++) {
            littlePits.add(new Pit(UUID.randomUUID().toString(), game, position <= gameConfig.getLittlePitsPerPlayer() ? PLAYER_ONE : PLAYER_TWO, position, gameConfig.getInitialStonesPerPit()));
        }
        return littlePits;
    }

    private Game getGameAndValidateMovement(String gameId, int pitIndex) throws WrongPlayerTurnException, BadPitSelectionException, GameOverException, GameNotFoundException, InvalidMovementException {
        Optional<Game> game = gameRepository.findById(gameId);

        if (game.isEmpty()) {
            log.error("Game not found");
            throw new GameNotFoundException();
        }

        if (isGameOver(game.get())) {
            int scorePlayerOne = game.get().getBigPitPlayerOne();
            int scorePlayerTwo = game.get().getBigPitPlayerTwo();
            log.error("Game is over. {}!! ({}-{})", scorePlayerOne > scorePlayerTwo ? Turn.PLAYER_ONE.getLabel() + " wins" : scorePlayerOne < scorePlayerTwo ? Turn.PLAYER_TWO.getLabel() + " wins" : "It's a draw", scorePlayerOne, scorePlayerTwo);
            throw new GameOverException();
        }

        if (!isValidPitSelection(game.get(), pitIndex)) {
            log.error("Pit selected ({}) is out of bounds [1-{}]", pitIndex, getTotalLittlePits(game.get()));
            throw new BadPitSelectionException();
        }

        Turn turnFromSelection = getTurnFromPit(game.get(), pitIndex);
        if (!isValidTurn(game.get(), turnFromSelection)) {
            log.error("Player turn incorrect. The turn is for {}", GameUtils.getOppositeTurn(turnFromSelection).getLabel());
            throw new WrongPlayerTurnException();
        }

        if (isPitEmpty(game.get(), pitIndex)) {
            log.error("Pit selected ({}) is empty", pitIndex);
            throw new InvalidMovementException();
        }

        return game.get();
    }

    private void pickStones(Game game, int pitIndex) {
        boolean isBigPitLast = false;
        boolean isLastPitEmpty;

        int stonesToSow = game.getPit(pitIndex).pickStones();
        int pitToSow = getNextPitToSow(game, pitIndex);

        while (stonesToSow > 0) {
            if (!isBigPitLast && isFirstPitOppositePlayerAndNeedToSowBigPit(game, pitToSow)) {
                sowBigPit(game);
                isBigPitLast = true;
                stonesToSow--;
            } else {
                isLastPitEmpty = game.getPit(pitToSow).sow();
                isBigPitLast = false;
                stonesToSow--;

                if (isLastPitEmpty && stonesToSow == 0 && game.getTurn().equals(getTurnFromPit(game, pitToSow))) {
                    stealOppositePlayerPit(game, pitToSow);
                } else {
                    pitToSow = getNextPitToSow(game, pitToSow);
                }
            }
        }

        checkNextTurn(game, isBigPitLast);

        gameRepository.save(game);
    }

    private void checkNextTurn(Game game, boolean isBigPitLast) {
        if (isEveryPitEmpty(game)) {
            endGame(game);
        } else if (!isBigPitLast) {
            game.setTurn(GameUtils.getOppositeTurn(game.getTurn()));
        }
    }

    private void endGame(Game game) {
        game.addStonesBigPitPlayerTwo(game.getPits().stream().filter(p -> p.getPlayer() == PLAYER_TWO).mapToInt(Pit::pickStones).sum());
        game.addStonesBigPitPlayerOne(game.getPits().stream().filter(p -> p.getPlayer() == PLAYER_ONE).mapToInt(Pit::pickStones).sum());
        game.setTurn(Turn.GAME_OVER);
    }

    private int getLittlePitsPerPlayer(Game game) {
        return getTotalLittlePits(game) / 2;
    }

    private int getTotalLittlePits(Game game) {
        return game.getPits().size();
    }

    private Turn getTurnFromPit(Game game, int pitIndex) {
        return pitIndex <= getLittlePitsPerPlayer(game) ? Turn.PLAYER_ONE : Turn.PLAYER_TWO;
    }

    private int getNextPitToSow(Game game, int pitIndex) {
        return pitIndex == getTotalLittlePits(game) ? 1 : pitIndex + 1;
    }

    private boolean isEveryPitEmpty(Game game) {
        if (Turn.PLAYER_ONE.equals(game.getTurn())) {
            return game.getPits().stream().filter(p -> p.getPlayer() == PLAYER_ONE).noneMatch(pit -> pit.getStones() > 0);
        } else {
            return game.getPits().stream().filter(p -> p.getPlayer() == PLAYER_TWO).noneMatch(pit -> pit.getStones() > 0);
        }
    }

    private boolean isGameOver(Game game) {
        return Turn.GAME_OVER.equals(game.getTurn());
    }

    private boolean isValidPitSelection(Game game, int pitIndex) {
        return pitIndex > 0 && pitIndex <= getTotalLittlePits(game);
    }

    private boolean isValidTurn(Game game, Turn turn) {
        return game.getTurn().equals(turn);
    }

    private boolean isPitEmpty(Game game, int pitIndex) {
        return game.getPit(pitIndex).getStones() == 0;
    }

    private boolean isFirstPitOppositePlayerAndNeedToSowBigPit(Game game, int pitIndex) {
        return (Turn.PLAYER_TWO.equals(game.getTurn()) && pitIndex == 1) ||
                (Turn.PLAYER_ONE.equals(game.getTurn()) && pitIndex == (getTotalLittlePits(game) / 2) + 1);
    }

    private void sowBigPit(Game game) {
        if (Turn.PLAYER_ONE.equals(game.getTurn())) {
            game.addStonesBigPitPlayerOne(1);
        } else {
            game.addStonesBigPitPlayerTwo(1);
        }
    }

    private void stealOppositePlayerPit(Game game, int pitIndex) {
        int oppositePitIndex = getTotalLittlePits(game) - pitIndex + 1;
        int stones = game.getPit(pitIndex).pickStones() + game.getPit(oppositePitIndex).pickStones();

        if (Turn.PLAYER_ONE.equals(game.getTurn())) {
            game.addStonesBigPitPlayerOne(stones);
        } else {
            game.addStonesBigPitPlayerTwo(stones);
        }
    }
}