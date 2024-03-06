package org.example.entities;

import java.util.List;

public interface MovementStrategy {
    public int calculateMovement(List<Dice> dies);
}
