package org.example.entities;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {
    private int size;

    public Board(int size) {
        validateBoardSize(size);
        this.size = size;
    }

    public void validateBoardSize(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Board size should be greater than 1");
        }
    }

}
