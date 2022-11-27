package actors;

import field.Coordinates;
import field.Field;
import field.FieldUtils;
import field.PointColor;

import java.util.List;

import static actors.BasicAiActor.performMove;

public class AdvancedAiActor implements IActor {

    private final PointColor color;

    public AdvancedAiActor(PointColor p) {
        color = p;
    }

    private Double getBestForField(Field f) {
        // Calculate best enemy turn
        // Код так форматирует idea, я не готов с ней бороться
        List<Coordinates> possibleTurns = FieldUtils.possibleMoves(color == PointColor.Black ? PointColor.White : PointColor.Black, f);
        List<ScoredCoordinate> scoredTurns = possibleTurns.stream().map(it -> new ScoredCoordinate(Utils.endPointValue(it) + field.FieldUtils.flippedPoint(it, color, f).stream().map(Utils::intermidiatePointValue).reduce(0.0, Double::sum), it)).sorted().toList();

        if (scoredTurns.size() == 0) return 0.0;

        return scoredTurns.get(scoredTurns.size() - 1).score();
    }

    @Override
    public Boolean requestAction(Field f) {
        List<Coordinates> possibleTurns = FieldUtils.possibleMoves(color, f);
        List<ScoredCoordinate> scoredTurns = possibleTurns.stream().map(it -> new ScoredCoordinate(Utils.endPointValue(it) + field.FieldUtils.flippedPoint(it, color, f).stream().map(Utils::intermidiatePointValue).reduce(0.0, Double::sum), it)).map(it -> {
            Field copiedField = new Field(f);
            copiedField.placePoint(it.point(), color);
            return new ScoredCoordinate(it.score() - getBestForField(copiedField), it.point());
        }).sorted().toList();

        return performMove(f, scoredTurns, color);
    }
}
