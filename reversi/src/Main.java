import utils.Cmd;
import utils.Enemy;
import utils.OptionsMapper;

import java.util.Arrays;

public class Main {
    // Осознанно оставляю парсинг флагов тут потому что считаю что писать еще одну обертку это безумие, а в самой Game ее быть не должно
    public static void main(String[] args) { // Потому что если сделать флажки как нормальные люди то у кого-то что-то отвалится блин бесит
        Enemy enemyType;
        boolean printBest;

        System.out.println("What type of enemy? [ai/smartai/player]");
        enemyType = OptionsMapper.userOptionsToEnemyType(Cmd.getUserOption(Arrays.asList("ai", "smartai", "player")));

        System.out.println("Print best results after each round? [y/n]");
        printBest = OptionsMapper.userOptionsToBoolean(Cmd.getUserOption(Arrays.asList("y", "n")));

        Game g = new Game(enemyType, printBest);
        g.run();
    }
}