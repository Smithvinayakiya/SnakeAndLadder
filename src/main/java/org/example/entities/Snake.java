package org.example.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class Snake implements Characters{
    private int start;
    private int end;

    public Snake(int start, int end) {

        validateCharacter(start,end);
        this.start = start;
        this.end = end;
    }

    @Override
    public void validateCharacter(int start, int end) {
        if (start<=end) {
            throw new IllegalArgumentException("Start of snake should be greater than end");
        }
    }
}
