package com.example.mancala.controller;

import com.example.mancala.exception.BadPitSelectionException;
import com.example.mancala.exception.GameNotStartedException;
import com.example.mancala.exception.InvalidMovementException;
import com.example.mancala.exception.WrongPlayerTurnException;
import com.example.mancala.service.GameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Api(tags = "mancala-game")
@RestController
@RequestMapping("/api")
public class GameRestController {

    private final GameService gameService;

    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @ApiOperation(value = "Start Mancala game for 2 players")
    @PostMapping("/start")
    public void startGame() {
        gameService.startGame();
    }

    @ApiOperation(value = "Selects the pit for the movement")
    @PutMapping("/selectPit/{pit}")
    public void selectPit(@PathVariable Integer pit) {
        try {
            gameService.move(pit);
        } catch (GameNotStartedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game not started", e);
        } catch (BadPitSelectionException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad pit selected", e);
        } catch (WrongPlayerTurnException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong turn", e);
        } catch (InvalidMovementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid movement, pit is empty", e);
        }
    }

    @ApiOperation(value = "Gets the current status of the board", response = List.class)
    @GetMapping("/status")
    public List<Integer> getStatus() {
        return gameService.getBoardStatus();
    }

    @ApiOperation(value = "Gets the current player turn or game over", response = String.class)
    @GetMapping("/turn")
    public String getTurn() {
        return gameService.getTurn();
    }
}
