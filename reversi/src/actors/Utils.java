package actors;

import field.Coordinates;

public class Utils {
    public static Double endPointValue(Coordinates crd) {
        if ((crd.x() == 1 && crd.y() == 1) || (crd.x() == 1 && crd.y() == 8) || (crd.x() == 8 && crd.y() == 8) || (crd.x() == 8 && crd.y() == 1)) {
            return 0.8;
        }

        if (crd.x() == 1 || crd.x() == 8 || crd.y() == 1 || crd.y() == 8) {
            return 0.4;
        }

        return 0.0;
    }

    public static Double intermidiatePointValue(Coordinates crd) {
        if (crd.x() == 1 || crd.x() == 8 || crd.y() == 1 || crd.y() == 8) {
            return 2.0;
        }

        return 1.0;
    }
}
