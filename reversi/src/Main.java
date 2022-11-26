import utils.Cmd;
import utils.Enemy;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) { // Потому что если сделать флажки как нормальные люди то у кого-то что-то отвалится блин бесит
        Enemy enemyType;
        Boolean printBest;

        System.out.println("What type of enemy? [ai/smartai/player]");
        enemyType = switch (Cmd.getUserOption(Arrays.asList("ai", "smartai", "player"))) {
            case "ai" -> Enemy.BasicMachine;
            case "smartai" -> Enemy.AdvancedMachine;
            default -> Enemy.Player;
        };

        System.out.println("Print best results after each round? [y/n]");
        printBest = switch (Cmd.getUserOption(Arrays.asList("y", "n"))) {
            case "y" -> true;
            default -> false;
        };

        Game g = new Game(enemyType, printBest);
        g.run();
    }
}