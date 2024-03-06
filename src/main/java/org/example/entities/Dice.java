package org.example.entities;

import lombok.Getter;

@Getter
public class Dice{
    private int MAX;

    public Dice(int MAX){
        if (MAX <= 0) {
            throw new IllegalArgumentException("Dice should have atleast 1 face");
        }
        this.MAX = MAX;
    }

    public int rollDice(){
            return (int) (Math.random() * MAX) + 1;
    }
}
