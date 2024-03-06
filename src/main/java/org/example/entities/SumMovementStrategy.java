package org.example.entities;

import java.util.List;

public class SumMovementStrategy implements MovementStrategy {

    @Override
    public int calculateMovement(List<Dice> dies) {
        return dies.stream().mapToInt(Dice::rollDice).sum();
    }

}
