package org.example.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Ladder implements Characters{
    private int start;
    private int end;

    public Ladder(int start, int end) {

        if(end<=start){
            throw new IllegalArgumentException("End of ladder should be greater than start");
        }
        this.start = start;
        this.end = end;
    }

    @Override
    public void validateCharacter() {
        if (end<=start) {
            throw new IllegalArgumentException("Start and end of ladder should be greater than 0");
        }
    }
}
