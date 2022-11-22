package field;

class PointColorHelper {
    public static String pointToString(PointColor p) {
        return switch (p) {
            case Empty -> "E";
            case Black -> "B";
            case White -> "W";
        };
    }
}
