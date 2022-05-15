package com.example.mancala.model;

import lombok.Data;

@Data
public class Pit {

    private Integer stones;

    public Pit() {
        this.stones = 0;
    }

    public Pit(Integer stones) {
        this.stones = stones;
    }

    public Integer pickStones() {
        Integer value = stones;
        stones = 0;
        return value;
    }

    public void addStones(Integer stones) {
        this.stones += stones;
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
