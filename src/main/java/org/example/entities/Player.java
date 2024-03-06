package org.example.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private String name;
    private int position;
    private int skipTurns;

    public Player(String name, int position){
        this.name = name;
        this.position = position;
        this.skipTurns = 0;
    }
}
