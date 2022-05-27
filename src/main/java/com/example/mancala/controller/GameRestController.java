package com.example.mancala.controller;

import com.example.mancala.exception.*;
import com.example.mancala.model.Game;
import com.example.mancala.service.GameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Api(tags = "mancala-game")
@RestController
@RequestMapping("/api/game")
public class GameRestController {

    private final GameService gameService;

    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @ApiOperation(value = "Starts a new game for 2 players", notes = "Returns the game")
    @PostMapping
    public Game startGame() {
        return gameService.createGame();
    }

    @ApiOperation(value = "Retrieves the game according to the game ID provided", response = Game.class)
    @GetMapping("/{gameId}")
    public Game get(@PathVariable String gameId) {
        return gameService.getGame(gameId);
    }

    @ApiOperation(value = "Selects the pit for the movement. Default: 1-6 for pits from player 1, 7-12 for pits from player 2")
    @PutMapping("/{gameId}/selectPit/{pit}")
    public void selectPit(@PathVariable String gameId, @PathVariable Integer pit) {
        try {
            gameService.move(gameId, pit);
        } catch (GameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game not started", e);
        } catch (BadPitSelectionException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad pit selected", e);
        } catch (WrongPlayerTurnException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong turn", e);
        } catch (InvalidMovementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid movement, pit is empty", e);
        } catch (GameOverException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game is over", e);
        }
    }
}
