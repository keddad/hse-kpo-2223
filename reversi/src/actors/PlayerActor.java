package actors;

import field.Coordinates;
import field.Field;
import field.PointColor;
import utils.Cmd;

import java.util.Arrays;

public class PlayerActor implements IActor {
    private PointColor color;
    public PlayerActor(PointColor p) {
        color = p;
    }

    @Override
    public Boolean requestAction(Field f) {
        System.out.println("Reverse or make a Turn? [r/t]");

        String choise = Cmd.getUserOption(Arrays.asList("r", "t"));

        if (choise.equals("r")) {
            System.out.println("meow");
            return false;
        } else {
            while (true) {
                System.out.println("Input coordinates as two digits");
                Coordinates crd = Cmd.getCoordinates();

                try {
                    f.placePoint(crd, color);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return false;
    }
}
