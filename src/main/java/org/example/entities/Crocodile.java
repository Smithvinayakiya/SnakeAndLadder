package org.example.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Crocodile implements Characters{

    private int position;
    @Override
    public void validateCharacter(int start, int end){
        if (position<=0) {
            throw new IllegalArgumentException("Crocodile should be placed at a position greater than 0");
        }
    }

    public Crocodile(int position){
        validateCharacter(position, position);
        this.position = position;
    }
}
