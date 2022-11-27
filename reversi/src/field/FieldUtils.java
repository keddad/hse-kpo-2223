package field;

import utils.Pair;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class FieldUtils {
    private static List<Coordinates> expandPoint(Coordinates crd) {
        HashSet<Coordinates> answ = new HashSet<>();

        for (int diff = -8; diff <= 8; diff++) {
            if (diff == 0) continue;

            List<Coordinates> possiblePoints = Arrays.asList(
                    new Coordinates(crd.x() + diff, crd.y()),
                    new Coordinates(crd.x(), crd.y() + diff),
                    new Coordinates(crd.x() + diff, crd.y() + diff),
                    new Coordinates(crd.x() + diff, crd.y() - diff),
                    new Coordinates(crd.x() - diff, crd.y() + diff),
                    new Coordinates(crd.x() - diff, crd.y() - diff)
            ); // god I hope this works

            possiblePoints.stream().filter(FieldUtils::isValidCoordinates).forEach(answ::add);
        }

        return answ.stream().toList();
    }

    private static Boolean isPointBordered(Coordinates crd, Field f, PointColor targetColor) {
        for (int x_diff = -1; x_diff <= 1; x_diff++) {
            for (int y_diff = -1; y_diff <= 1; y_diff++) {
                if (x_diff == 0 && y_diff == 0) continue;

                Coordinates newcrd = new Coordinates(crd.x() + x_diff, crd.y() + y_diff);

                if (!isValidCoordinates(newcrd)) {
                    continue;
                }

                if (f.getPointColor(newcrd) == targetColor) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Boolean isValidCoordinates(Coordinates crd) {
        return crd.x() >= 1 && crd.x() <= 8 && crd.y() >= 1 && crd.y() <= 8;
    }

    public static Integer pointDistance(Coordinates a, Coordinates b) {
        // Only works if they are in one line
        if (a.x().equals(b.x())) {
            return abs(a.y() - b.y());
        } else {
            return abs(a.x() - b.x());
        }
    }

    public static List<Coordinates> flippedPoints(Coordinates a, Coordinates b, PointColor p, Field f) {
        // Points flipped between a and b (one of them is new)
        assert p != PointColor.Empty; // Asserts are always useful
        assert (f.getPointColor(a) == PointColor.Empty && f.getPointColor(b) == p) || (f.getPointColor(b) == PointColor.Empty && f.getPointColor(a) == p);

        PointColor toFlip = p == PointColor.White ? PointColor.Black : PointColor.White;
        List<Coordinates> answer = new ArrayList<>();

        // i hate this
        if (a.x().equals(b.x())) {
            for (int y = min(a.y(), b.y()); y < max(a.y(), b.y()); y++) {
                Coordinates crds = new Coordinates(a.x(), y);

                if (f.getPointColor(crds) == toFlip) {
                    answer.add(crds);
                }
            }
        } else if (a.y().equals(b.y())) {
            for (int x = min(a.x(), b.x()); x < max(a.x(), b.x()); x++) {
                Coordinates crds = new Coordinates(x, a.y());

                if (f.getPointColor(crds) == toFlip) {
                    answer.add(crds);
                }
            }
        } else {
            if (b.x() < a.x()) {
                Coordinates tmp = a;
                a = b;
                b = tmp; // why no generic swap??
            }

            for (int diff = 1; diff < abs(a.y() - b.y()); diff++) {
                Coordinates crds;

                if (a.y() < b.y()) {
                    crds = new Coordinates(a.x() + diff, a.y() + diff);
                } else {
                    crds = new Coordinates(a.x() + diff, a.y() - diff);
                }

                if (f.getPointColor(crds) == toFlip) {
                    answer.add(crds);
                }
            }
        }

        return answer;
    }

    public static List<Coordinates> flippedPoint(Coordinates crd, PointColor p, Field f) {
        // Points flipped when placing a point of certain color

        return expandPoint(crd)
                .stream()
                .filter(it -> f.getPointColor(it) == p)
                .flatMap(it -> flippedPoints(crd, it, p, f).stream())
                .toList();
    }

    private static Boolean verifyFlipped(Coordinates a, Coordinates b, PointColor p, Field f) {
        List<Coordinates> flipped = flippedPoints(a, b, p, f);

        if (flipped.isEmpty()) return false;
        return flipped.size() == pointDistance(a, b) - 1;
    }

    public static List<Coordinates> possibleMoves(PointColor p, Field f) throws IllegalArgumentException {
        if (p == PointColor.Empty) {
            throw new IllegalArgumentException("Can't place Point without a color!");
        }

        PointColor enemyColor = p == PointColor.White ? PointColor.Black : PointColor.White;

        // functional programming was a mistake
        return f.getColoredPoints(p)
                .stream()
                .collect(Collectors.toMap(it -> it, FieldUtils::expandPoint)) // Get all possible endpoints for lines starting at points of target color
                .entrySet() // Leave unique
                .stream()
                .map(it -> new Pair<>(it.getKey(), it.getValue().stream().filter(y -> f.getPointColor(y) == PointColor.Empty).toList())) // Remove already filled
                .map(it -> new Pair<>(it.getKey(), it.getValue().stream().filter(y -> isPointBordered(y, f, enemyColor)).toList())) // Remove without neighbour of enemy color
                .map(it -> new Pair<>(it.getKey(), it.getValue().stream().filter(y -> verifyFlipped(it.getKey(), y, p, f)).toList())) // Remove ones which don't flip "the line"
                .map(Pair::getValue)
                .flatMap(Collection::stream).distinct().toList();
    }

 }
