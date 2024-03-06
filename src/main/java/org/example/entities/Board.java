package org.example.entities;

import java.util.HashMap;
import java.util.Map;
import javax.swing.text.Position;
import lombok.Getter;
import lombok.Setter;
import org.example.constants.Character;

@Getter
@Setter
public class Board {
    private int size;
    private Map<Integer, Characters> characters;

    public Board(int size) {
        this.size = size;
        this.characters = new HashMap<>();
    }

    public void addCharacter(Character character, int startPosition, int endPosition) {

        Characters ch = null;
        switch (character) {
            case LADDER:
                ch = new Ladder(startPosition, endPosition);
                break;
            case SNAKE:
                ch = new Snake(startPosition, endPosition);
                break;
        }
        ch.validateCharacter();
        validateBoard(ch);
        characters.put(startPosition, ch);
    }

    private void validateBoard(Characters ch) {
        // check for overlapping characters
    }
}
