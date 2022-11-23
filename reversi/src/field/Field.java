package field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final public class Field {
    private PointColor[][] field = new PointColor[8][8];

    public Field() {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j)  {
                field[i][j] = PointColor.Empty;
            }
        }


        field[3][3] = PointColor.White;
        field[4][4] = PointColor.White;

        field[3][4] = PointColor.Black;
        field[4][3] = PointColor.Black;

        fieldHisory.add(field);
    }

    private List<PointColor[][]> fieldHisory = new ArrayList<>();

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

    public void printBoard(PointColor player) {
        System.out.print("%\t");
        System.out.print(
                IntStream.rangeClosed(1, 8).boxed().map(String::valueOf).collect(Collectors.joining("\t"))
        );

        List<Coordinates> c = FieldUtils.possibleMoves(player, this);

        for (int i = 0; i < 8; i++) {
            System.out.printf("\n%d\t", i+1);

            for (int j = 0; j < 8; ++j) {
                Coordinates current = new Coordinates(i+1, j+1);
                String point = c.contains(current) ? "." : PointColorHelper.pointToString(field[i][j]);
                System.out.printf("%s\t", point);
            }
        }

        System.out.println();
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

        fieldHisory.add(field);

        for (Coordinates toFlip : FieldUtils.flippedPoint(crd, p, this)
        ) {
            setPointColor(toFlip, p);
        }

        setPointColor(crd, p);
    }

    public void reversePointPlacement(Integer steps) throws IllegalStateException {
        if (steps >= fieldHisory.size()) {
            throw new IllegalStateException("Can't go back that far");
        }

        field = fieldHisory.get(fieldHisory.size() - steps);
        fieldHisory = fieldHisory.subList(0, fieldHisory.size() - steps);
    }
}
