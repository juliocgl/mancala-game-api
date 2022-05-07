package com.example.mancala.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GameRestController {

    @PostMapping("/start")
    public void startGame() {
        // Initializes the application for a new game
    }

    @PutMapping("/selectPit")
    public void selectPit() {
        // Selects a pit to pick the stones and start sowing them
    }

    @GetMapping("/status")
    public void getStatus() {
        // Returns the status of the board
    }

    @GetMapping("/turn")
    public Integer getTurn() {
        // Returns the player number that is the next to move
        return 1;
    }
}
