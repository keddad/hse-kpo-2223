import utils.Enemy;

import java.util.Arrays;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        Enemy enemyType = Enemy.Player;
        Boolean printBest = false;

        if (Arrays.stream(args).anyMatch(Predicate.isEqual("--ai"))) {
            enemyType = Enemy.BasicMachine;
        }

        if (Arrays.stream(args).anyMatch(Predicate.isEqual("--smartai"))) {
            enemyType = Enemy.AdvancedMachine;
        }

        if (Arrays.stream(args).anyMatch(Predicate.isEqual("--printbest"))) {
            printBest = true;
        }

        Game g = new Game(enemyType, printBest);
        g.run();
    }
}