package com.example.mancala.service;

import com.example.mancala.configuration.GameConfig;
import com.example.mancala.dao.GameDAO;
import com.example.mancala.exception.BadPitSelectionException;
import com.example.mancala.exception.GameNotStartedException;
import com.example.mancala.exception.InvalidMovementException;
import com.example.mancala.exception.WrongPlayerTurnException;
import com.example.mancala.model.Pit;
import com.example.mancala.model.Turn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static com.example.mancala.utils.TestConstants.EMPTY_STONES;
import static com.example.mancala.utils.TestConstants.INITIAL_STONES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class GameServiceTest {

    @Mock
    GameConfig gameConfig;

    @Mock
    GameDAO gameDAO;

    @InjectMocks
    GameService gameService;

    @BeforeEach
    public void init() {
        when(gameConfig.getLittlePitsPerPlayer()).thenReturn(6);
        when(gameConfig.getInitialStonesPerPit()).thenReturn(6);
    }

    @Test
    public void givenGameNotStarted_whenStartGame_expectInitializeBoard() {
        gameService.startGame();

        verify(gameDAO, times(1)).initializeBoard();
    }

    @Test
    public void givenGameNotStarted_whenMove_expectException() {
        when(gameDAO.getTurn()).thenReturn(null);

        assertThrows(GameNotStartedException.class, () -> gameService.move(1));
    }

    @Test
    public void givenGameStarted_whenInvalidMove_expectException() {
        when(gameDAO.getTurn()).thenReturn(Turn.PLAYER_ONE);

        assertThrows(BadPitSelectionException.class, () -> gameService.move(50));
    }

    @Test
    public void givenTurnPlayerOne_whenInvalidTurn_expectException() {
        when(gameDAO.getTurn()).thenReturn(Turn.PLAYER_ONE);

        assertThrows(WrongPlayerTurnException.class, () -> gameService.move(8));
    }

    @Test
    public void givenTurnPlayerOne_whenInvalidMove_expectException() {
        when(gameDAO.getTurn()).thenReturn(Turn.PLAYER_ONE);
        when(gameDAO.getLittlePit(Turn.PLAYER_ONE, 4)).thenReturn(new Pit());

        assertThrows(InvalidMovementException.class, () -> gameService.move(4));
    }

    @Test
    public void givenTurnPlayerOne_whenMove_expectStonesPicked() throws GameNotStartedException, WrongPlayerTurnException, InvalidMovementException, BadPitSelectionException {
        Pit littlePit = mock(Pit.class);
        when(littlePit.getStones()).thenReturn(INITIAL_STONES);
        when(littlePit.pickStones()).thenReturn(INITIAL_STONES);
        when(littlePit.sow()).thenReturn(false);

        when(gameDAO.getTurn()).thenReturn(Turn.PLAYER_ONE);
        when(gameDAO.getLittlePit(any(), any())).thenReturn(littlePit);
        when(gameDAO.getPlayerOneBigPit()).thenReturn(mock(Pit.class));
        when(gameDAO.getPlayerOneLittlePits()).thenReturn(List.of(new Pit(7), new Pit(7), new Pit(7), new Pit(EMPTY_STONES), new Pit(INITIAL_STONES), new Pit(INITIAL_STONES)));

        gameService.move(4);

        verify(gameDAO, times(2)).getLittlePit(Turn.PLAYER_ONE, 4);
        verify(gameDAO, times(1)).getLittlePit(Turn.PLAYER_ONE, 3);
        verify(gameDAO, times(1)).getLittlePit(Turn.PLAYER_ONE, 2);
        verify(gameDAO, times(1)).getLittlePit(Turn.PLAYER_ONE, 1);
        verify(gameDAO.getPlayerOneBigPit(), times(1)).sow();
        verify(gameDAO, times(1)).getLittlePit(Turn.PLAYER_TWO, 6);
        verify(gameDAO, times(1)).getLittlePit(Turn.PLAYER_TWO, 5);
        verify(gameDAO, times(1)).setTurn(Turn.PLAYER_TWO);
    }

    @Test
    public void givenTurnPlayerTwo_whenMove_expectStonesPicked() throws GameNotStartedException, WrongPlayerTurnException, InvalidMovementException, BadPitSelectionException {
        Pit littlePit = mock(Pit.class);
        when(littlePit.getStones()).thenReturn(INITIAL_STONES);
        when(littlePit.pickStones()).thenReturn(INITIAL_STONES);
        when(littlePit.sow()).thenReturn(false);

        when(gameDAO.getTurn()).thenReturn(Turn.PLAYER_TWO);
        when(gameDAO.getLittlePit(any(), any())).thenReturn(littlePit);
        when(gameDAO.getPlayerTwoBigPit()).thenReturn(mock(Pit.class));
        when(gameDAO.getPlayerTwoLittlePits()).thenReturn(List.of(new Pit(EMPTY_STONES), new Pit(INITIAL_STONES), new Pit(INITIAL_STONES), new Pit(INITIAL_STONES), new Pit(INITIAL_STONES), new Pit(INITIAL_STONES)));

        gameService.move(7);

        verify(gameDAO, times(2)).getLittlePit(Turn.PLAYER_TWO, 1);
        verify(gameDAO.getPlayerTwoBigPit(), times(1)).sow();
        verify(gameDAO, times(1)).getLittlePit(Turn.PLAYER_ONE, 6);
        verify(gameDAO, times(1)).getLittlePit(Turn.PLAYER_ONE, 5);
        verify(gameDAO, times(1)).getLittlePit(Turn.PLAYER_ONE, 4);
        verify(gameDAO, times(1)).getLittlePit(Turn.PLAYER_ONE, 3);
        verify(gameDAO, times(1)).getLittlePit(Turn.PLAYER_ONE, 2);
        verify(gameDAO, times(1)).setTurn(Turn.PLAYER_ONE);
    }

    @Test
    public void givenPlayerOneTurn_whenMoveEndsInEmptyPit_expectStealOppositeStones() throws GameNotStartedException, WrongPlayerTurnException, InvalidMovementException, BadPitSelectionException {
        Pit littlePit = mock(Pit.class);
        when(littlePit.getStones()).thenReturn(1);
        when(littlePit.pickStones()).thenReturn(1);
        when(littlePit.sow()).thenReturn(false);

        Pit emptyPit = mock(Pit.class);
        when(emptyPit.sow()).thenReturn(true);
        when(emptyPit.getStones()).thenReturn(0);

        Pit opponentPit = mock(Pit.class);
        when(opponentPit.pickStones()).thenReturn(INITIAL_STONES);

        when(gameDAO.getTurn()).thenReturn(Turn.PLAYER_ONE);
        when(gameDAO.getLittlePit(Turn.PLAYER_ONE, 2)).thenReturn(littlePit);
        when(gameDAO.getLittlePit(Turn.PLAYER_ONE, 1)).thenReturn(emptyPit);
        when(gameDAO.getLittlePit(Turn.PLAYER_TWO, 6)).thenReturn(opponentPit);
        when(gameDAO.getPlayerOneBigPit()).thenReturn(mock(Pit.class));
        when(gameDAO.getPlayerOneLittlePits()).thenReturn(List.of(new Pit(EMPTY_STONES), new Pit(INITIAL_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES)));

        gameService.move(2);

        verify(gameDAO, times(2)).getLittlePit(Turn.PLAYER_ONE, 1);
        verify(gameDAO.getPlayerOneBigPit(), times(1)).sow();
        verify(gameDAO.getPlayerOneBigPit(), times(1)).addStones(INITIAL_STONES);
        verify(gameDAO, times(1)).setTurn(Turn.PLAYER_TWO);
    }

    @Test
    public void givenPlayerTwoTurn_whenMoveEndsInEmptyPit_expectStealOppositeStones() throws GameNotStartedException, WrongPlayerTurnException, InvalidMovementException, BadPitSelectionException {
        Pit littlePit = mock(Pit.class);
        when(littlePit.getStones()).thenReturn(1);
        when(littlePit.pickStones()).thenReturn(1);
        when(littlePit.sow()).thenReturn(false);

        Pit emptyPit = mock(Pit.class);
        when(emptyPit.sow()).thenReturn(true);
        when(emptyPit.getStones()).thenReturn(0);

        Pit opponentPit = mock(Pit.class);
        when(opponentPit.pickStones()).thenReturn(INITIAL_STONES);

        when(gameDAO.getTurn()).thenReturn(Turn.PLAYER_TWO);
        when(gameDAO.getLittlePit(Turn.PLAYER_TWO, 2)).thenReturn(littlePit);
        when(gameDAO.getLittlePit(Turn.PLAYER_TWO, 1)).thenReturn(emptyPit);
        when(gameDAO.getLittlePit(Turn.PLAYER_ONE, 6)).thenReturn(opponentPit);
        when(gameDAO.getPlayerTwoBigPit()).thenReturn(mock(Pit.class));
        when(gameDAO.getPlayerTwoLittlePits()).thenReturn(List.of(new Pit(EMPTY_STONES), new Pit(INITIAL_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES)));

        gameService.move(8);

        verify(gameDAO, times(2)).getLittlePit(Turn.PLAYER_TWO, 1);
        verify(gameDAO.getPlayerTwoBigPit(), times(1)).sow();
        verify(gameDAO.getPlayerTwoBigPit(), times(1)).addStones(INITIAL_STONES);
        verify(gameDAO, times(1)).setTurn(Turn.PLAYER_ONE);
    }

    @Test
    public void givenPlayerOneTurn_whenMoveEndInBigPit_expectRepeatTurn() throws GameNotStartedException, WrongPlayerTurnException, InvalidMovementException, BadPitSelectionException {
        Pit littlePit = mock(Pit.class);
        when(littlePit.getStones()).thenReturn(1);
        when(littlePit.pickStones()).thenReturn(1);
        when(littlePit.sow()).thenReturn(true);

        when(gameDAO.getTurn()).thenReturn(Turn.PLAYER_ONE);
        when(gameDAO.getLittlePit(any(), any())).thenReturn(littlePit);
        when(gameDAO.getPlayerOneBigPit()).thenReturn(mock(Pit.class));
        when(gameDAO.getPlayerOneLittlePits()).thenReturn(List.of(new Pit(EMPTY_STONES), new Pit(INITIAL_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES)));
        when(gameDAO.getPlayerTwoBigPit()).thenReturn(mock(Pit.class));

        gameService.move(1);

        verify(gameDAO, times(2)).getLittlePit(Turn.PLAYER_ONE, 1);
        verify(gameDAO.getPlayerOneBigPit(), times(1)).sow();
        verify(gameDAO, times(0)).setTurn(any());
    }

    @Test
    public void givenLastTurnPlayerOne_whenMove_expectGameOver() throws GameNotStartedException, WrongPlayerTurnException, InvalidMovementException, BadPitSelectionException {
        Pit littlePit = mock(Pit.class);
        when(littlePit.getStones()).thenReturn(1);
        when(littlePit.pickStones()).thenReturn(1);
        when(littlePit.sow()).thenReturn(false);

        when(gameDAO.getTurn()).thenReturn(Turn.PLAYER_ONE);
        when(gameDAO.getLittlePit(any(), any())).thenReturn(littlePit);
        when(gameDAO.getPlayerOneBigPit()).thenReturn(mock(Pit.class));
        when(gameDAO.getPlayerOneLittlePits()).thenReturn(List.of(new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES)));
        when(gameDAO.getPlayerTwoBigPit()).thenReturn(mock(Pit.class));

        gameService.move(1);

        verify(gameDAO, times(2)).getLittlePit(Turn.PLAYER_ONE, 1);
        verify(gameDAO.getPlayerOneBigPit(), times(1)).sow();
        verify(gameDAO, times(1)).setTurn(Turn.GAME_OVER);
    }

    @Test
    public void givenLastTurnPlayerTwo_whenMove_expectGameOver() throws GameNotStartedException, WrongPlayerTurnException, InvalidMovementException, BadPitSelectionException {
        Pit littlePit = mock(Pit.class);
        when(littlePit.getStones()).thenReturn(1);
        when(littlePit.pickStones()).thenReturn(1);
        when(littlePit.sow()).thenReturn(false);

        when(gameDAO.getTurn()).thenReturn(Turn.PLAYER_TWO);
        when(gameDAO.getLittlePit(any(), any())).thenReturn(littlePit);
        when(gameDAO.getPlayerTwoBigPit()).thenReturn(mock(Pit.class));
        when(gameDAO.getPlayerTwoLittlePits()).thenReturn(List.of(new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES), new Pit(EMPTY_STONES)));
        when(gameDAO.getPlayerOneBigPit()).thenReturn(mock(Pit.class));

        gameService.move(7);

        verify(gameDAO, times(2)).getLittlePit(Turn.PLAYER_TWO, 1);
        verify(gameDAO.getPlayerTwoBigPit(), times(1)).sow();
        verify(gameDAO, times(1)).setTurn(Turn.GAME_OVER);
    }

    @Test
    public void givenInitialStatus_whenGetBoardStatus_expectBoardStatus() {
        when(gameDAO.getStonesInPlayerOnePits()).thenReturn(List.of(
                EMPTY_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES));
        when(gameDAO.getStonesInPlayerTwoPits()).thenReturn(List.of(
                EMPTY_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES, INITIAL_STONES));

        List<Integer> result = gameService.getBoardStatus();
        assertEquals(EMPTY_STONES, result.get(0));
        assertEquals(INITIAL_STONES, result.get(1));
        assertEquals(INITIAL_STONES, result.get(2));
        assertEquals(INITIAL_STONES, result.get(3));
        assertEquals(INITIAL_STONES, result.get(4));
        assertEquals(INITIAL_STONES, result.get(5));
        assertEquals(INITIAL_STONES, result.get(6));
        assertEquals(EMPTY_STONES, result.get(7));
        assertEquals(INITIAL_STONES, result.get(8));
        assertEquals(INITIAL_STONES, result.get(9));
        assertEquals(INITIAL_STONES, result.get(10));
        assertEquals(INITIAL_STONES, result.get(11));
        assertEquals(INITIAL_STONES, result.get(12));
        assertEquals(INITIAL_STONES, result.get(13));
    }

    @Test
    public void givenPlayerOneTurn_whenGetTurn_thenReturnPlayerOne() {
        when(gameDAO.getTurn()).thenReturn(Turn.PLAYER_ONE);

        assertEquals(Turn.PLAYER_ONE.getLabel(), gameService.getTurn());
    }

    @Test
    public void givenPlayerTwoTurn_whenGetTurn_thenReturnPlayerTwo() {
        when(gameDAO.getTurn()).thenReturn(Turn.PLAYER_TWO);

        assertEquals(Turn.PLAYER_TWO.getLabel(), gameService.getTurn());
    }

    @Test
    public void givenGameOver_whenGetTurn_thenReturnGameOver() {
        when(gameDAO.getTurn()).thenReturn(Turn.GAME_OVER);

        assertEquals(Turn.GAME_OVER.getLabel(), gameService.getTurn());
    }
}