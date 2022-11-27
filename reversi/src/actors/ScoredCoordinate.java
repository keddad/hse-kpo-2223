package actors;

import field.Coordinates;

public record ScoredCoordinate(Double score, Coordinates point) implements Comparable<ScoredCoordinate> {
    @Override
    public int compareTo(ScoredCoordinate o) {
        return score.compareTo(o.score());
    }
}
