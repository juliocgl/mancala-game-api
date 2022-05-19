package com.example.mancala.dao;

import com.example.mancala.configuration.GameConfig;
import com.example.mancala.model.Pit;
import com.example.mancala.model.Turn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.example.mancala.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class GameDAOTest {

    @Mock
    GameConfig gameConfig;

    @InjectMocks
    GameDAO gameDAO;

    @BeforeEach
    public void init() {
        when(gameConfig.getLittlePitsPerPlayer()).thenReturn(DEFAULT_PITS);
        when(gameConfig.getInitialStonesPerPit()).thenReturn(INITIAL_STONES);
    }

    @Test
    void testInitializeBoard() {
        gameDAO.initializeBoard();
        assertEquals(gameDAO.getStonesInPlayerOnePits().size(), 7);
        assertEquals(gameDAO.getStonesInPlayerTwoPits().size(), 7);
    }

    @Test
    void testGetPlayerOneBigPit() {
        gameDAO.initializeBoard();
        assertEquals(EMPTY_STONES, gameDAO.getPlayerOneBigPit().getStones());
    }

    @Test
    void testGetPlayerTwoBigPit() {
        gameDAO.initializeBoard();
        assertEquals(EMPTY_STONES, gameDAO.getPlayerTwoBigPit().getStones());
    }

    @Test
    void testGetPlayerOneLittlePits() {
        gameDAO.initializeBoard();
        assertEquals(DEFAULT_PITS, gameDAO.getPlayerOneLittlePits().size());
        assertEquals(INITIAL_STONES, gameDAO.getPlayerOneLittlePits().get(0).getStones());
    }

    @Test
    void testGetPlayerTwoLittlePits() {
        gameDAO.initializeBoard();
        assertEquals(DEFAULT_PITS, gameDAO.getPlayerTwoLittlePits().size());
        assertEquals(INITIAL_STONES, gameDAO.getPlayerTwoLittlePits().get(0).getStones());
    }

    @Test
    void testGetStonesInPlayerOnePits() {
        gameDAO.initializeBoard();
        assertEquals(7, gameDAO.getStonesInPlayerTwoPits().size());
        assertEquals(36, gameDAO.getStonesInPlayerTwoPits().stream().reduce(0, Integer::sum));
    }

    @Test
    void testGetStonesInPlayerTwoPits() {
        gameDAO.initializeBoard();
        assertEquals(7, gameDAO.getStonesInPlayerTwoPits().size());
        assertEquals(36, gameDAO.getStonesInPlayerTwoPits().stream().reduce(0, Integer::sum));

    }

    @Test
    void testGetTurn() {
        gameDAO.initializeBoard();
        assertInstanceOf(Turn.class, gameDAO.getTurn());
        gameDAO.setTurn(Turn.PLAYER_ONE);
        assertEquals(Turn.PLAYER_ONE, gameDAO.getTurn());
    }

    @Test
    void testSetTurn() {
        gameDAO.initializeBoard();
        gameDAO.setTurn(Turn.PLAYER_ONE);
        assertEquals(Turn.PLAYER_ONE, gameDAO.getTurn());
    }

    @Test
    void testGetLittlePit() {
        gameDAO.initializeBoard();
        Pit littlePit = gameDAO.getLittlePit(Turn.PLAYER_ONE, 2);
        assertEquals(INITIAL_STONES, littlePit.getStones());
    }
}