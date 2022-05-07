package com.example.mancala;

import com.example.mancala.controller.GameRestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MancalaRestAPIApplicationTests {
    @Autowired
    GameRestController gameRestController;

    @Test
    void contextLoads() {
        assertThat(gameRestController).isNotNull();
    }
}