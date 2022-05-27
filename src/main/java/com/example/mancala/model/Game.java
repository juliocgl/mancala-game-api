package com.example.mancala.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Game implements Serializable {

    @ApiModelProperty(notes = "Game ID")
    @Id
    private String id;

    @ApiModelProperty(notes = "Current turn")
    private Turn turn;

    @ApiModelProperty(notes = "Player 1 big pit (score points)")
    private int bigPitPlayerOne;

    @ApiModelProperty(notes = "Player 2 big pit (score points)")
    private int bigPitPlayerTwo;

    @ApiModelProperty(notes = "Little pits from both players")
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @OrderBy("position asc")
    private List<Pit> pits;

    public void addStonesBigPitPlayerOne(int stones) {
        bigPitPlayerOne += stones;
    }

    public void addStonesBigPitPlayerTwo(int stones) {
        bigPitPlayerTwo += stones;
    }

    public Pit getPit(int index) {
        return pits.get(index - 1);
    }
}
