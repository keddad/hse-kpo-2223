import utils.Enemy;

import java.util.Arrays;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        Enemy enemyType = Enemy.Player;

        if (Arrays.stream(args).anyMatch(Predicate.isEqual("--ai"))) {
            enemyType = Enemy.BasicMachine;
        }

        if (Arrays.stream(args).anyMatch(Predicate.isEqual("--smartai"))) {
            enemyType = Enemy.AdvancedMachine;
        }

        Game g = new Game(enemyType);
        g.run();
    }
}