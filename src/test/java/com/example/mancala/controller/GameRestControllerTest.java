package com.example.mancala.controller;

import com.example.mancala.exception.*;
import com.example.mancala.model.Game;
import com.example.mancala.model.Pit;
import com.example.mancala.model.Turn;
import com.example.mancala.service.GameService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.mancala.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GameRestController.class)
public class GameRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    void givenNothing_whenStartGame_expectGameStarted() throws Exception {
        mockMvc.perform(post("/api/game"))
                .andExpect(status().isOk());

        verify(gameService, times(1)).createGame();
    }

    @Test
    void givenGameCreated_whenGetGame_expectGameReturned() throws Exception {
        Game game = new Game();
        game.setId(GAME_ID);
        game.setTurn(Turn.PLAYER_ONE);
        Pit pit = new Pit();
        pit.setId(PIT_ID);
        pit.setPlayer(PLAYER_TWO_NUMBER);
        pit.setPosition(POSITION_3);
        pit.setStones(INITIAL_STONES);
        List<Pit> littlePits = new ArrayList<>();
        littlePits.add(pit);
        littlePits.add(new Pit());
        game.setPits(littlePits);

        when(gameService.getGame(GAME_ID)).thenReturn(game);

        mockMvc.perform(get("/api/game/" + GAME_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(GAME_ID))
                .andExpect(jsonPath("$.turn").value(Turn.PLAYER_ONE.name()))
                .andExpect(jsonPath("$.bigPitPlayerOne").value(EMPTY_STONES))
                .andExpect(jsonPath("$.bigPitPlayerTwo").value(EMPTY_STONES))
                .andExpect(jsonPath("$.pits", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.pits[0].id").value(PIT_ID))
                .andExpect(jsonPath("$.pits[0].player").value(PLAYER_TWO_NUMBER))
                .andExpect(jsonPath("$.pits[0].position").value(POSITION_3))
                .andExpect(jsonPath("$.pits[0].stones").value(INITIAL_STONES));
    }

    @Test
    void givenInitialStatus_whenSelectPit_expectMovement() throws Exception {
        mockMvc.perform(put("/api/game/" + GAME_ID + "/selectPit/" + POSITION_3))
                .andExpect(status().isOk());

        verify(gameService, times(1)).move(GAME_ID, POSITION_3);
    }

    @Test
    void givenInitialStatus_whenGameNotFoundAndSelectPit_expectException() throws Exception {
        doThrow(new GameNotFoundException()).when(gameService).move(GAME_ID, POSITION_3);

        mockMvc.perform(put("/api/game/" + GAME_ID + "/selectPit/" + POSITION_3))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Game not started", ((ResponseStatusException) Objects.requireNonNull(result.getResolvedException())).getReason()));
    }

    @Test
    void givenInitialStatus_whenSelectInvalidPit_expectException() throws Exception {
        doThrow(new BadPitSelectionException()).when(gameService).move(GAME_ID, POSITION_3);

        mockMvc.perform(put("/api/game/" + GAME_ID + "/selectPit/" + POSITION_3))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Bad pit selected", ((ResponseStatusException) Objects.requireNonNull(result.getResolvedException())).getReason()));
    }

    @Test
    void givenInitialStatus_whenWrongTurnAndSelectPit_expectException() throws Exception {
        doThrow(new WrongPlayerTurnException()).when(gameService).move(GAME_ID, POSITION_3);

        mockMvc.perform(put("/api/game/" + GAME_ID + "/selectPit/" + POSITION_3))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Wrong turn", ((ResponseStatusException) Objects.requireNonNull(result.getResolvedException())).getReason()));
    }

    @Test
    void givenInitialStatus_whenInvalidMove_expectException() throws Exception {
        doThrow(new InvalidMovementException()).when(gameService).move(GAME_ID, POSITION_3);

        mockMvc.perform(put("/api/game/" + GAME_ID + "/selectPit/" + POSITION_3))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Invalid movement, pit is empty", ((ResponseStatusException) Objects.requireNonNull(result.getResolvedException())).getReason()));
    }

    @Test
    void givenGameIsOver_whenSelectPit_expectException() throws Exception {
        doThrow(new GameOverException()).when(gameService).move(GAME_ID, POSITION_3);

        mockMvc.perform(put("/api/game/" + GAME_ID + "/selectPit/" + POSITION_3))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Game is over", ((ResponseStatusException) Objects.requireNonNull(result.getResolvedException())).getReason()));
    }
}
