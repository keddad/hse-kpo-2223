package utils;

public class OptionsMapper {
    public static Enemy userOptionsToEnemyType(String userOption) {
        return switch (userOption) {
            case "ai" -> Enemy.BasicMachine;
            case "smartai" -> Enemy.AdvancedMachine;
            case "player" -> Enemy.Player;
            default -> throw new IllegalArgumentException();
        };
    }

    public static boolean userOptionsToBoolean(String userOption) {
        return switch (userOption) {
            case "y" -> true;
            case "n" -> false;
            default -> throw new IllegalArgumentException();
        };
    }
}
