package com.example.mancala.controller;

import com.example.mancala.exception.BadPitSelectionException;
import com.example.mancala.exception.GameNotStartedException;
import com.example.mancala.exception.InvalidMovementException;
import com.example.mancala.exception.WrongPlayerTurnException;
import com.example.mancala.model.Turn;
import com.example.mancala.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

import static com.example.mancala.utils.TestConstants.EMPTY_STONES;
import static com.example.mancala.utils.TestConstants.INITIAL_STONES;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
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
    void givenGameNotStarted_whenStartGame_expectGameStart() throws Exception {
        mockMvc.perform(post("/api/start"))
                .andExpect(status().isOk());

        verify(gameService, times(1)).startGame();
    }

    @Test
    void givenInitialStatus_whenSelectPit_expectMovement() throws Exception {
        mockMvc.perform(put("/api/selectPit/1"))
                .andExpect(status().isOk());

        verify(gameService, times(1)).move(1);
    }

    @Test
    void givenGameNotStarted_whenSelectPit_expectException() throws Exception {
        doThrow(new GameNotStartedException()).when(gameService).move(anyInt());

        mockMvc.perform(put("/api/selectPit/1"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Game not started", ((ResponseStatusException) Objects.requireNonNull(result.getResolvedException())).getReason()));
    }

    @Test
    void givenInvalidPit_whenSelectPit_expectException() throws Exception {
        doThrow(new BadPitSelectionException()).when(gameService).move(anyInt());

        mockMvc.perform(put("/api/selectPit/1"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Bad pit selected", ((ResponseStatusException) Objects.requireNonNull(result.getResolvedException())).getReason()));
    }

    @Test
    void givenWrongTurn_whenSelectPit_expectException() throws Exception {
        doThrow(new WrongPlayerTurnException()).when(gameService).move(anyInt());

        mockMvc.perform(put("/api/selectPit/1"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Wrong turn", ((ResponseStatusException) Objects.requireNonNull(result.getResolvedException())).getReason()));
    }

    @Test
    void givenInvalidMovement_whenSelectPit_expectException() throws Exception {
        doThrow(new InvalidMovementException()).when(gameService).move(anyInt());

        mockMvc.perform(put("/api/selectPit/1"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Invalid movement, pit is empty", ((ResponseStatusException) Objects.requireNonNull(result.getResolvedException())).getReason()));
    }

    @Test
    void givenInitialStatus_whenGetStatus_expectStatus() throws Exception {
        List<Integer> stonesInBoard = List.of(
                EMPTY_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES,
                EMPTY_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES);

        when(gameService.getBoardStatus()).thenReturn(stonesInBoard);

        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(14)))
                .andExpect(jsonPath("$[0]").value(EMPTY_STONES))
                .andExpect(jsonPath("$[1]").value(INITIAL_STONES))
                .andExpect(jsonPath("$[2]").value(INITIAL_STONES))
                .andExpect(jsonPath("$[3]").value(INITIAL_STONES))
                .andExpect(jsonPath("$[4]").value(INITIAL_STONES))
                .andExpect(jsonPath("$[5]").value(INITIAL_STONES))
                .andExpect(jsonPath("$[6]").value(INITIAL_STONES))
                .andExpect(jsonPath("$[7]").value(EMPTY_STONES))
                .andExpect(jsonPath("$[8]").value(INITIAL_STONES))
                .andExpect(jsonPath("$[9]").value(INITIAL_STONES))
                .andExpect(jsonPath("$[10]").value(INITIAL_STONES))
                .andExpect(jsonPath("$[11]").value(INITIAL_STONES))
                .andExpect(jsonPath("$[12]").value(INITIAL_STONES))
                .andExpect(jsonPath("$[13]").value(INITIAL_STONES));
    }

    @Test
    void givenTurnPlayerOne_whenGetTurn_expectTurnPlayerOne() throws Exception {
        when(gameService.getTurn()).thenReturn(Turn.PLAYER_ONE.getLabel());
        mockMvc.perform(get("/api/turn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Turn.PLAYER_ONE.getLabel()));
    }

    @Test
    void givenGameOver_whenGetTurn_expectTurnGameOver() throws Exception {
        when(gameService.getTurn()).thenReturn(Turn.GAME_OVER.getLabel());
        mockMvc.perform(get("/api/turn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Turn.GAME_OVER.getLabel()));
    }
}
