package org.example;
import org.example.controller.GameController;

public class Main {
    public static void main(String[] args) {

        GameController game = new GameController();
        game.setUpGameConfig("src/main/resources/config.json");
        game.playGame();
    }
}