package org.example;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.example.controller.GameController;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class Main {
    public static void main(String[] args) {

        GameController game = new GameController();
        game.setUpGameConfig("src/main/resources/config.json");
        game.playGame();
    }
}