package org.example.entities;

import java.util.List;

public class MinMovementStrategy implements MovementStrategy{

        @Override
        public int calculateMovement(List<Dice> dies) {
            return dies.stream().mapToInt(Dice::rollDice).min().getAsInt();
        }
}
