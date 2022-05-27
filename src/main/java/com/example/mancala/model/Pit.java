package com.example.mancala.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Pit implements Serializable {

    @ApiModelProperty(notes = "Pit ID")
    @Id
    private String id;

    @ApiModelProperty(notes = "Game ID")
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ApiModelProperty(notes = "Player number", example = "[1, 2]")
    private int player;

    @ApiModelProperty(notes = "Pit position in the board", example = "1")
    private int position;

    @ApiModelProperty(notes = "Stones in the pit", example = "6")
    private int stones;

    public int pickStones() {
        int value = stones;
        stones = 0;
        return value;
    }

    /**
     * Sows a stone in the pit. In case the pit was previously empty, returns True, if it was not, returns False.
     *
     * @return True if the pit was empty before sowing, false if it was not
     */
    public boolean sow() {
        stones++;
        return stones == 1;
    }
}
