package org.example.controller;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.example.entities.Board;
import org.example.entities.Crocodile;
import org.example.entities.Dice;
import org.example.entities.Ladder;
import org.example.entities.MaxMovementStrategy;
import org.example.entities.MinMovementStrategy;
import org.example.entities.Mine;
import org.example.entities.MovementStrategy;
import org.example.entities.Player;
import org.example.entities.Snake;
import org.example.entities.SumMovementStrategy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
public class GameController {

    private int numberOfPlayers;
    private List<Player> players;
    private List<Snake> snakes;
    private List<Ladder> ladders;

    private List<Crocodile> crocodiles;

    private List<Mine> mines;

    private Board board;
    private List<Dice> dies;

    private MovementStrategy movementStrategy;

    public GameController() {
        this.numberOfPlayers = 2;
        this.players = new ArrayList<>();
        this.snakes = new ArrayList<>();
        this.ladders = new ArrayList<>();
        this.dies = new ArrayList<>();
        this.crocodiles = new ArrayList<>();
        this.mines = new ArrayList<>();
    }
    public void setUpGameConfig(String filePath) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject config = (JSONObject) parser.parse(new FileReader(filePath));

            String movementStrategy = (String) config.get("movementStrategy");
            switch (movementStrategy) {
                case "SUM":
                    this.movementStrategy = new SumMovementStrategy();
                    break;
                case "MAX":
                    this.movementStrategy = new MaxMovementStrategy();
                    break;
                case "MIN":
                    this.movementStrategy = new MinMovementStrategy();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid movement strategy");
            }

            this.numberOfPlayers = Integer.parseInt(config.get("numPlayers").toString());
            int boardSize = Integer.parseInt(config.get("boardSize").toString());
            this.board = new Board(boardSize*boardSize);


            JSONArray playersArray = (JSONArray) config.get("players");
            for (Object obj : playersArray) {
                JSONObject playerObj = (JSONObject) obj;
                String name = (String) playerObj.get("name");
                int position = playerObj.get("position") != null ? Integer.parseInt(playerObj.get("position").toString()) : 1;
                if(position<1 || position>boardSize*boardSize){
                    throw new IllegalArgumentException("Player position should be between 1 and " + boardSize*boardSize);
                }
                players.add(new Player(name, position));
            }

            Set<Integer> invalidStarts = new HashSet<>();

            JSONArray snakesArray = (JSONArray) config.get("snakes");
            for (Object obj : snakesArray) {
                JSONObject snakeObj = (JSONObject) obj;
                int start = Integer.parseInt(snakeObj.get("start").toString());
                int end = Integer.parseInt(snakeObj.get("end").toString());
                if(start<1 || start>boardSize*boardSize || end<1 || end>boardSize*boardSize){
                    throw new IllegalArgumentException("Snake start and end should be between 1 and " + boardSize*boardSize);
                }
                validateStarts(start, end, invalidStarts);
                snakes.add(new Snake(start, end));
            }

            JSONArray laddersArray = (JSONArray) config.get("ladders");
            for (Object obj : laddersArray) {
                JSONObject ladderObj = (JSONObject) obj;
                int start = Integer.parseInt(ladderObj.get("start").toString());
                int end = Integer.parseInt(ladderObj.get("end").toString());
                if(start<1 || start>boardSize*boardSize || end<1 || end>boardSize*boardSize){
                    throw new IllegalArgumentException("Ladder start and end should be between 1 and " + boardSize*boardSize);
                }
                validateStarts(start, end, invalidStarts);
                ladders.add(new Ladder(start, end));
            }

            JSONArray crocodilesArray = (JSONArray) config.get("crocodiles");
            for (Object obj : crocodilesArray) {
                JSONObject crocodileObj = (JSONObject) obj;
                int position = Integer.parseInt(crocodileObj.get("position").toString());
                if(position<1 || position>boardSize*boardSize){
                    throw new IllegalArgumentException("Crocodile position should be between 1 and " + boardSize*boardSize);
                }
                validateStarts(position, (position-5)>0?position-5:1, invalidStarts);
                crocodiles.add(new Crocodile(position));
            }

            JSONArray diesArray = (JSONArray) config.get("dies");
            for (Object obj : diesArray) {
                JSONObject diesObj = (JSONObject) obj;
                int sides = Integer.parseInt(diesObj.get("sides").toString());
                if(sides>boardSize*boardSize){
                    throw new IllegalArgumentException("Dice sides should be less than or equal to " + boardSize*boardSize);
                }
                dies.add(new Dice(sides));
            }

            JSONArray minesArray = (JSONArray) config.get("mines");
            for (Object obj : minesArray) {
                JSONObject minesObj = (JSONObject) obj;
                int position = Integer.parseInt(minesObj.get("position").toString());
                if(position<1 || position>boardSize*boardSize){
                    throw new IllegalArgumentException("Mine position should be between 1 and " + boardSize*boardSize);
                }
                validateStarts(position, position, invalidStarts);

                mines.add(new Mine(position));
            }

        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    private void validateStarts(int start, int end, Set<Integer> invalidStarts) {
        if (invalidStarts.contains(start)){
            throw new IllegalArgumentException("Character start cannot be same as another character end or position");
        }
        invalidStarts.add(end);
    }

    private int calculateNewPosition(int newPosition, int currentPosition) {
        if (newPosition > this.board.getSize()) {
            return currentPosition;
        }
        // Check for snakes
        for (Snake snake : snakes) {
            if (snake.getStart() == newPosition) {
                System.out.println("Bitten by snake at position " + newPosition + " and moved back to position " + snake.getEnd());
                return snake.getEnd();
            }
        }
        // Check for ladders
        for (Ladder ladder : ladders) {
            if (ladder.getStart() == newPosition) {
                System.out.println("Climbed ladder at position " + newPosition + " and moved to position " + ladder.getEnd());
                return ladder.getEnd();
            }
        }

        for (Crocodile crocodile: crocodiles){
            if(crocodile.getPosition() == newPosition){
                newPosition = (newPosition-5)>0? newPosition-5: 1;
                System.out.println("Bumped into crocodile at position " + newPosition + " and moved back to position " + newPosition);
                return newPosition;
            }
        }
        return newPosition;
    }

    public void playGame() {
        // Main game loop
        int playerNum = 0;
        Player player;
        int cumulativeDiceValue;
        while (true) {
                player = players.get(playerNum);
                if(player.getSkipTurns()>0){
                    player.setSkipTurns(player.getSkipTurns()-1);
                    System.out.println(player.getName() + " is skipping turn and will have to skip " + player.getSkipTurns() + " more turns");
                    playerNum = (playerNum + 1) % numberOfPlayers;
                    continue;
                }
                int newPosition;
                cumulativeDiceValue = this.movementStrategy.calculateMovement(dies);
                newPosition = player.getPosition() + cumulativeDiceValue;
                newPosition = calculateNewPosition(newPosition, player.getPosition());
                for(Mine mine: mines){
                    if(mine.getPosition() == newPosition){
                        System.out.println(player.getName() + " stepped on a mine at position " + newPosition + " and misses next 2 turns");
                        player.setSkipTurns(2);
                    }
                }
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
