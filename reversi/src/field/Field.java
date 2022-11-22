package field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final public class Field {
    private PointColor[][] field = new PointColor[8][8];

    public PointColor getPointColor(Coordinates crd) throws IllegalArgumentException {
        if (!FieldUtils.isValidCoordinates(crd)) {
            throw new IllegalArgumentException("Point is out of Field!");
        }

        return field[crd.x() - 1][crd.y() - 1];
    }

    private void setPointColor(Coordinates crd, PointColor p) {
        field[crd.x() - 1][crd.y() - 1] = p;
    }

    public List<Coordinates> getColoredPoints(PointColor p) {
        List<Coordinates> answ = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (field[i][j] == p) {
                    answ.add(new Coordinates(i + 1, j + 1));
                }
            }
        }

        return answ;
    }

    public void printBoard() {
        System.out.print("%\t");
        System.out.println(
                IntStream.rangeClosed(1, 8).boxed().map(String::valueOf).collect(Collectors.joining("\t"))
        );

        for (int i = 0; i < 8; i++) {
            System.out.printf("%d\t", i+1);
            System.out.println(
                    Arrays.stream(field[i]).map(PointColorHelper::pointToString).collect(Collectors.joining("\t"))
            );
        }
    }

    public Integer countPoints(PointColor p) {
        return Math.toIntExact(Arrays.stream(field).flatMap(Arrays::stream).filter(it -> it == p).count());
    }

    public void placePoint(Coordinates crd, PointColor p) throws IllegalArgumentException {
        if (!FieldUtils.isValidCoordinates(crd)) {
            throw new IllegalArgumentException("Point is out of Field!");
        }

        if (field[crd.x() - 1][crd.y() - 1] != PointColor.Empty) {
            throw new IllegalArgumentException("Point is already taken!");
        }

        if (!FieldUtils.possibleMoves(p, this).contains(crd)) {
            throw new IllegalArgumentException("Move is invalid!");
        }

        for (Coordinates toFlip : FieldUtils.flippedPoints(crd, p, this)
        ) {
            setPointColor(toFlip, p);
        }
    }

    public void reversePointPlacement() throws IllegalStateException {
        // TODO fixme
        return;
    }
}
