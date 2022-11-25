package actors;

import field.Coordinates;

public record ScoredCoordinate(Double score, Coordinates point) implements Comparable {
    @Override
    public int compareTo(Object o) {
        return score.compareTo(((ScoredCoordinate) o).score());
    }
}
