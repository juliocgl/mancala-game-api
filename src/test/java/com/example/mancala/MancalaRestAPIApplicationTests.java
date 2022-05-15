package com.example.mancala;

import com.example.mancala.controller.GameRestController;
import com.example.mancala.dao.GameDAO;
import com.example.mancala.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MancalaRestAPIApplicationTests {

    @Autowired
    GameRestController gameRestController;

    @Autowired
    GameService gameService;

    @Autowired
    GameDAO gameDAO;

    @Test
    void contextLoads() {
        assertThat(gameRestController).isNotNull();
    }
}