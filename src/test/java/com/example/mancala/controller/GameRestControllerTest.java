package com.example.mancala.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GameRestController.class)
public class GameRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void startGame() throws Exception {
        mockMvc.perform(post("/api/start"))
                .andExpect(status().isOk());
    }

    @Test
    void selectPit() throws Exception {
        mockMvc.perform(put("/api/selectPit"))
                .andExpect(status().isOk());
    }

    @Test
    void getStatus() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk());
    }

    @Test
    void getTurn() throws Exception {
        mockMvc.perform(get("/api/turn"))
                .andExpect(status().isOk());
    }
}
