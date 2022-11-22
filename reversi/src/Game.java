import actors.AdvancedAiActor;
import actors.BasicAiActor;
import actors.IActor;
import actors.PlayerActor;
import field.Field;
import field.PointColor;
import utils.Cmd;
import utils.Enemy;

import java.util.ArrayList;
import java.util.Arrays;

public class Game {
    private final IActor opponent;
    private int bestScore = 0;
    private Boolean printBest = false;

    public Game(Enemy enemyType, boolean printBest) {
        opponent = switch (enemyType) {
            case Player -> {
                System.out.println("Playing in PVP mode");
                yield new PlayerActor();
            }
            case BasicMachine -> {
                System.out.println("Playing in PVE mode, basic computer");
                yield new BasicAiActor();
            }
            case AdvancedMachine -> {
                System.out.println("Playing in PVP mode, advanced computer");
                yield new AdvancedAiActor();
            }
        };

        this.printBest = printBest;
    }

    private void printHello() {
        System.out.println("You are playing as whites");
    }

    private void processGame(Field f) {
        Boolean dudeTurn = true;
    }

    private void printResults(Field f) {
        Integer playerPoints = f.countPoints(PointColor.White);
        Integer enemyPoints = f.countPoints(PointColor.Black);

        bestScore = Math.max(bestScore, playerPoints);

        System.out.printf("Player score: %d\n", playerPoints);
        System.out.printf("Enemy score: %d\n", enemyPoints);

        if (printBest) {
            System.out.printf("Current player best: %d\n", bestScore);
        }
    }

    public void run() {
        printHello();

        while (true) {
            Field f = new Field();
            processGame(f);
            printResults(f);

            System.out.println("One more match? y/n");
            String answ = Cmd.getUserOption(Arrays.asList("y", "n"));

            if (answ.equals("n")) {
                break;
            }
        }

    }
}
