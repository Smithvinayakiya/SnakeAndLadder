package org.example.controller;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.example.constants.MovementStrategy;
import org.example.entities.Board;
import org.example.entities.Dice;
import org.example.entities.Ladder;
import org.example.entities.Player;
import org.example.entities.Snake;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
public class GameController {

    private int numberOfPlayers;
    private int numberOfSnakes;
    private int numberOfLadders;
    private int numberOfDies;
    private List<Player> players;
    private List<Snake> snakes;
    private List<Ladder> ladders;

    private Board board;
    private List<Dice> dies;

    private MovementStrategy movementStrategy;

    public GameController() {
        this.numberOfPlayers = 2;
        this.numberOfSnakes = 0;
        this.numberOfLadders = 0;
        this.numberOfDies = 1;
        this.movementStrategy = MovementStrategy.SUM;
        this.players = new ArrayList<>();
        this.snakes = new ArrayList<>();
        this.ladders = new ArrayList<>();
        this.dies = new ArrayList<>();
    }
    public void setUpGameConfig(String filePath) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject config = (JSONObject) parser.parse(new FileReader(filePath));

            this.numberOfPlayers = Integer.parseInt(config.get("numPlayers").toString());
            this.numberOfSnakes = Integer.parseInt(config.get("numSnakes").toString());
            this.numberOfLadders = Integer.parseInt(config.get("numLadders").toString());
            this.numberOfDies = Integer.parseInt(config.get("numDies").toString());
            int boardSize = Integer.parseInt(config.get("boardSize").toString());
            this.board = new Board(boardSize*boardSize);

            JSONArray playersArray = (JSONArray) config.get("players");
            for (Object obj : playersArray) {
                JSONObject playerObj = (JSONObject) obj;
                String name = (String) playerObj.get("name");
                int position = playerObj.get("position") != null ? Integer.parseInt(playerObj.get("position").toString()) : 1;
                players.add(new Player(name, position));
            }

            JSONArray snakesArray = (JSONArray) config.get("snakes");
            for (Object obj : snakesArray) {
                JSONObject snakeObj = (JSONObject) obj;
                int start = Integer.parseInt(snakeObj.get("start").toString());
                int end = Integer.parseInt(snakeObj.get("end").toString());
                snakes.add(new Snake(start, end));
            }

            JSONArray laddersArray = (JSONArray) config.get("ladders");
            for (Object obj : laddersArray) {
                JSONObject ladderObj = (JSONObject) obj;
                int start = Integer.parseInt(ladderObj.get("start").toString());
                int end = Integer.parseInt(ladderObj.get("end").toString());
                ladders.add(new Ladder(start, end));
            }

            JSONArray diesArray = (JSONArray) config.get("dies");
            for (Object obj : diesArray) {
                JSONObject diesObj = (JSONObject) obj;
                int sides = Integer.parseInt(diesObj.get("sides").toString());
                dies.add(new Dice(sides));
            }

        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    private int calculateNewPosition(int currentPosition, int diceValue) {
        int newPosition = currentPosition + diceValue;
        if (newPosition > this.board.getSize()) {
            return currentPosition;
        }
        // Check for snakes
        for (Snake snake : snakes) {
            if (snake.getStart() == newPosition) {
                return snake.getEnd();
            }
        }
        // Check for ladders
        for (Ladder ladder : ladders) {
            if (ladder.getStart() == newPosition) {
                return ladder.getEnd();
            }
        }
        return newPosition;
    }

    public void playGame() {
        // Main game loop
        int playerNum = 0;
        Player player;
        while (true) {
                player = players.get(playerNum);
                int cumulativeDiceValue = 0;
                int newPosition;
                switch (movementStrategy) {
                    case SUM:
                        for(Dice die: dies){
                            int diceValue = die.rollDice();
                            cumulativeDiceValue+= diceValue;
                        }
                        break;
                    case MAX:
                        for(Dice die: dies){
                            int diceValue = die.rollDice();
                            cumulativeDiceValue = Math.max(cumulativeDiceValue, diceValue);
                        }
                        break;
                    case MIN:
                        for(Dice die: dies){
                            int diceValue = die.rollDice();
                            cumulativeDiceValue = Math.min(cumulativeDiceValue, diceValue);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid movement strategy");
                }
                newPosition = calculateNewPosition(player.getPosition(), cumulativeDiceValue);
                for(Player p: players){
                    if (p.getPosition() == newPosition && p != player){
                        System.out.println(p.getName() + " bumped into " + player.getName() + " and moved back to position " + 1);
                        p.setPosition(1);
                    }
                }
                System.out.println(player.getName() + " rolled a " + cumulativeDiceValue + " and moved to position " + newPosition);
                player.setPosition(newPosition);
                if (newPosition == this.board.getSize()) {
                    System.out.println(player.getName() + " wins!");
                    System.out.println("Game ended!");
                    return;
                }
                playerNum = (playerNum + 1) % numberOfPlayers;
            }
        }
}
