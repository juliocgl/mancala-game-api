package com.example.mancala.service;

import com.example.mancala.configuration.GameConfig;
import com.example.mancala.exception.*;
import com.example.mancala.model.Game;
import com.example.mancala.model.Pit;
import com.example.mancala.model.Turn;
import com.example.mancala.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.mancala.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class GameServiceTest {

    @Mock
    GameConfig gameConfig;

    @Mock
    GameRepository gameRepository;

    @InjectMocks
    GameService gameService;

    @BeforeEach
    public void init() {
        when(gameConfig.getLittlePitsPerPlayer()).thenReturn(DEFAULT_PITS);
        when(gameConfig.getInitialStonesPerPit()).thenReturn(INITIAL_STONES);
    }

    @Test
    public void givenGameNotCreated_whenCreateGame_expectGameCreated() {
        Game game = new Game();
        game.setId(GAME_ID);
        game.setTurn(Turn.PLAYER_ONE);
        when(gameRepository.save(any())).thenReturn(game);

        gameService.createGame();

        verify(gameRepository, times(1)).save(any());
    }

    @Test
    public void givenGameCreated_whenGet_expectReturnGame() {
        Game game = new Game();
        game.setId(GAME_ID);
        game.setTurn(Turn.PLAYER_ONE);
        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        Game result = gameService.getGame(GAME_ID);
        assertEquals(game.getId(), result.getId());
        assertEquals(game.getTurn(), result.getTurn());
    }

    @Test
    public void givenGameCreated_whenGetWrongGameId_expectNullValue() {
        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.empty());

        assertNull(gameService.getGame(GAME_ID));
    }

    @Test
    public void givenGameCreated_whenMoveWrongGameId_expectGameNotFoundException() {
        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.move(GAME_ID, POSITION_1));
    }

    @Test
    public void givenGameOver_whenMove_expectGameOverException() {
        Game game = new Game();
        game.setTurn(Turn.GAME_OVER);
        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        assertThrows(GameOverException.class, () -> gameService.move(GAME_ID, POSITION_1));
    }

    @Test
    public void givenGameCreated_whenMoveInvalidPit_expectBadPitSelectionException() {
        Game game = new Game();
        game.setTurn(Turn.PLAYER_ONE);
        List<Pit> littlePits = new ArrayList<>();
        littlePits.add(new Pit());
        littlePits.add(new Pit());
        game.setPits(littlePits);
        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        assertThrows(BadPitSelectionException.class, () -> gameService.move(GAME_ID, -1));
        assertThrows(BadPitSelectionException.class, () -> gameService.move(GAME_ID, 0));
        assertThrows(BadPitSelectionException.class, () -> gameService.move(GAME_ID, 3));
    }

    @Test
    public void givenTurnPlayerOne_whenInvalidTurn_expectWrongPlayerTurnException() {
        Game game = new Game();
        game.setTurn(Turn.PLAYER_ONE);
        List<Pit> littlePits = new ArrayList<>();
        littlePits.add(new Pit());
        littlePits.add(new Pit());
        game.setPits(littlePits);
        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        assertThrows(WrongPlayerTurnException.class, () -> gameService.move(GAME_ID, 2));
    }

    @Test
    public void givenTurnPlayerOne_whenInvalidMove_expectInvalidMovementException() {
        Game game = new Game();
        game.setTurn(Turn.PLAYER_ONE);
        List<Pit> littlePits = new ArrayList<>();
        littlePits.add(new Pit());
        littlePits.add(new Pit());
        game.setPits(littlePits);
        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        assertThrows(InvalidMovementException.class, () -> gameService.move(GAME_ID, POSITION_1));
    }

    @Test
    public void givenTurnPlayerOne_whenMove_expectStonesPicked() throws GameNotFoundException, WrongPlayerTurnException, InvalidMovementException, BadPitSelectionException, GameOverException {
        Game game = initializeGame(Turn.PLAYER_ONE);

        Pit pit1 = initializePit(1, INITIAL_STONES, false);
        Pit pit2 = initializePit(2, INITIAL_STONES, false);
        Pit pit3 = initializePit(3, INITIAL_STONES, false);
        Pit pit4 = initializePit(4, INITIAL_STONES, false);
        List<Pit> littlePits = new ArrayList<>();
        littlePits.add(pit1);
        littlePits.add(pit2);
        littlePits.add(pit3);
        littlePits.add(pit4);
        game.setPits(littlePits);

        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        gameService.move(GAME_ID, POSITION_1);

        verify(pit1, times(1)).pickStones();
        verify(pit2, times(1)).sow();
        verify(pit3, times(1)).sow();
        verify(pit4, times(1)).sow();

        verify(gameRepository, times(1)).save(any());
    }

    @Test
    public void givenTurnPlayerTwo_whenMove_expectStonesPicked() throws GameNotFoundException, WrongPlayerTurnException, InvalidMovementException, BadPitSelectionException, GameOverException {
        Game game = initializeGame(Turn.PLAYER_TWO);

        Pit pit1 = initializePit(1, INITIAL_STONES, false);
        Pit pit2 = initializePit(2, INITIAL_STONES, false);
        Pit pit3 = initializePit(3, INITIAL_STONES, false);
        Pit pit4 = initializePit(4, INITIAL_STONES, false);
        List<Pit> littlePits = new ArrayList<>();
        littlePits.add(pit1);
        littlePits.add(pit2);
        littlePits.add(pit3);
        littlePits.add(pit4);
        game.setPits(littlePits);

        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        gameService.move(GAME_ID, POSITION_3);

        verify(pit3, times(1)).pickStones();
        verify(pit4, times(1)).sow();
        verify(pit1, times(1)).sow();
        verify(pit2, times(1)).sow();

        verify(gameRepository, times(1)).save(any());
    }

    @Test
    public void givenPlayerOneTurn_whenMoveEndsInEmptyPit_expectStealOppositeStones() throws GameNotFoundException, WrongPlayerTurnException, InvalidMovementException, BadPitSelectionException, GameOverException {
        Game game = initializeGame(Turn.PLAYER_ONE);

        Pit pit1 = initializePit(1, 1,false);
        Pit pit2 = initializePit(2, EMPTY_STONES,true);
        Pit pit3 = initializePit(3, INITIAL_STONES,false);
        Pit pit4 = initializePit(4, INITIAL_STONES,false);
        List<Pit> littlePits = new ArrayList<>();
        littlePits.add(pit1);
        littlePits.add(pit2);
        littlePits.add(pit3);
        littlePits.add(pit4);
        game.setPits(littlePits);

        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        gameService.move(GAME_ID, POSITION_1);

        verify(pit1, times(1)).pickStones();
        verify(pit2, times(1)).sow();
        verify(pit2, times(1)).pickStones();
        verify(pit3, times(1)).pickStones();
        verify(pit3, times(0)).sow();
        verify(pit4, times(0)).sow();

        verify(gameRepository, times(1)).save(any());
    }

        @Test
    public void givenPlayerTwoTurn_whenMoveEndsInEmptyPit_expectStealOppositeStones() throws GameNotFoundException, WrongPlayerTurnException, InvalidMovementException, BadPitSelectionException, GameOverException {
            Game game = initializeGame(Turn.PLAYER_TWO);

            Pit pit1 = initializePit(1, INITIAL_STONES,false);
            Pit pit2 = initializePit(2, INITIAL_STONES,false);
            Pit pit3 = initializePit(3, 1,false);
            Pit pit4 = initializePit(4, EMPTY_STONES,true);
            List<Pit> littlePits = new ArrayList<>();
            littlePits.add(pit1);
            littlePits.add(pit2);
            littlePits.add(pit3);
            littlePits.add(pit4);
            game.setPits(littlePits);

            when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

            gameService.move(GAME_ID, POSITION_3);

            verify(pit3, times(1)).pickStones();
            verify(pit4, times(1)).sow();
            verify(pit4, times(1)).pickStones();
            verify(pit1, times(1)).pickStones();
            verify(pit2, times(0)).sow();

            verify(gameRepository, times(1)).save(any());
    }

        @Test
    public void givenPlayerOneTurn_whenMoveEndInBigPit_expectRepeatTurn() throws GameNotFoundException, WrongPlayerTurnException, InvalidMovementException, BadPitSelectionException, GameOverException {
            Game game = initializeGame(Turn.PLAYER_ONE);

            Pit pit1 = initializePit(1, INITIAL_STONES,false);
            Pit pit2 = initializePit(2, 1,false);
            Pit pit3 = initializePit(3, INITIAL_STONES,false);
            Pit pit4 = initializePit(4, INITIAL_STONES,false);
            List<Pit> littlePits = new ArrayList<>();
            littlePits.add(pit1);
            littlePits.add(pit2);
            littlePits.add(pit3);
            littlePits.add(pit4);
            game.setPits(littlePits);

            when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

            gameService.move(GAME_ID, 2);

            verify(pit1, times(0)).sow();
            verify(pit1, times(0)).pickStones();
            verify(pit2, times(1)).pickStones();
            verify(pit3, times(0)).sow();
            verify(pit3, times(0)).pickStones();
            verify(pit4, times(0)).sow();
            verify(pit4, times(0)).pickStones();

            verify(gameRepository, times(1)).save(any());
    }

    private Game initializeGame(Turn turn) {
        Game game = new Game();
        game.setId(GAME_ID);
        game.setTurn(turn);
        return game;
    }

    private Pit initializePit(int position, int stones, boolean empty) {
        Pit pit = mock(Pit.class);
        pit.setPosition(position);
        when(pit.getStones()).thenReturn(stones);
        when(pit.pickStones()).thenReturn(stones);
        when(pit.sow()).thenReturn(empty);
        return pit;
    }
}