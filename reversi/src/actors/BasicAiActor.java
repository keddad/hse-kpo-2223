package actors;

import field.Coordinates;
import field.Field;
import field.FieldUtils;
import field.PointColor;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class BasicAiActor implements IActor {
    private PointColor color;

    public BasicAiActor(PointColor p) {
        color = p;
    }

    @Override
    public Boolean requestAction(Field f) {
        List<Coordinates> possibleTurns = FieldUtils.possibleMoves(color, f);
        List<ScoredCoordinate> scoredTurns = possibleTurns.stream().map(it -> new ScoredCoordinate(Utils.endPointValue(it) + field.FieldUtils.flippedPoint(it, color, f).stream().map(Utils::intermidiatePointValue).reduce(0.0, Double::sum), it)).sorted().toList();
        Double maxScore = scoredTurns.get(scoredTurns.size() - 1).score();

        List<ScoredCoordinate> preferedMoves = scoredTurns.stream().filter(it -> it.score().equals(maxScore)).toList();

        Coordinates move = preferedMoves.get(ThreadLocalRandom.current().nextInt(0, preferedMoves.size()) % preferedMoves.size()).point();

        System.out.printf("Computer places it's point to %d %d\n", move.x(), move.y());

        f.placePoint(move, color);

        return false;
    }
}
