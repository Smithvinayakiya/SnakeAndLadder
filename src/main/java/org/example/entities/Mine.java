package org.example.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mine{

    private int position;
    private int skipTurns = 0;

    public Mine(int position){
        this.position = position;
    }
}
