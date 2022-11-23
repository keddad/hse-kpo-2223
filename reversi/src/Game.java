import actors.AdvancedAiActor;
import actors.BasicAiActor;
import actors.IActor;
import actors.PlayerActor;
import field.Coordinates;
import field.Field;
import field.FieldUtils;
import field.PointColor;
import utils.Cmd;
import utils.Enemy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {
    private final IActor opponent;
    private final IActor player = new PlayerActor(PointColor.White);
    private int bestScore = 0;
    private Boolean printBest = false;

    private static final PointColor playerColor = PointColor.White;
    private static final PointColor enemyColor = PointColor.Black;

    public Game(Enemy enemyType, boolean printBest) {
        opponent = switch (enemyType) {
            case Player -> {
                System.out.println("Playing in PVP mode");
                yield new PlayerActor(PointColor.Black);
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
        PointColor currentPlayer = playerColor;

        while (true) {
            System.out.printf("Current turn is: %s\n", currentPlayer == PointColor.White ? "White" : "Black");
            List<Coordinates> possibleTurns = FieldUtils.possibleMoves(currentPlayer, f);

            if (possibleTurns.isEmpty()) {
                if (FieldUtils.possibleMoves(currentPlayer == PointColor.Black ? PointColor.White : PointColor.Black, f).isEmpty()) {
                    System.out.println("No more turns available!");
                    return;
                }

                System.out.println("No more turns for current player. Skipping turn.");
                currentPlayer = currentPlayer == PointColor.White ? PointColor.Black : PointColor.White;
                continue;
            }

            f.printBoard(currentPlayer);

            Boolean skipEnemy = false;

            if (currentPlayer == playerColor) {
                skipEnemy = player.requestAction(f);
            } else {
                skipEnemy = opponent.requestAction(f);
            }

            if (!skipEnemy) {
                currentPlayer = currentPlayer == PointColor.White ? PointColor.Black : PointColor.White;
            }
        }

    }

    private void printResults(Field f) {
        Integer playerPoints = f.countPoints(PointColor.White);
        Integer enemyPoints = f.countPoints(PointColor.Black);

        bestScore = Math.max(bestScore, playerPoints);

        System.out.printf("Player score: %d\n", playerPoints);
        System.out.printf("Enemy score: %d\n", enemyPoints);

        if (playerPoints > enemyPoints) {
            System.out.println("Player won!");
        } else if (playerPoints < enemyPoints) {
            System.out.println("Machines are better as usual.");
        } else {
            System.out.println("Tie.");
        }

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
