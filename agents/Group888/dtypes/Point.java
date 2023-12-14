package dtypes;

import java.util.ArrayList;
import java.util.Arrays;

public record Point(int r, int c) {

    public int toKey(int columns) {
        return c + r * columns;
    }

    public boolean isValid(int minLimit, int maxLimit) {

        boolean belowUpperBound = r < maxLimit && c < maxLimit;
        boolean aboveLowerBound = r > minLimit && c > minLimit;
        return aboveLowerBound && belowUpperBound;

    }

    // (x-1,y) (x+1,y), (x,y-1), (x,y+1), (x-1,y+1), (x+1,y-1)
    public ArrayList<Point> getNeighbours(int boardSize) {
        if (r == boardSize || c == boardSize) {
            return new ArrayList<>();
        }
        ArrayList<Point> neighbours = new ArrayList<>(
                Arrays.asList(
                        new Point(r - 1, c),
                        new Point(r + 1, c),
                        new Point(r, c - 1),
                        new Point(r, c + 1),
                        new Point(r - 1, c + 1),
                        new Point(r + 1, c - 1)
                )
        );

        neighbours.removeIf(neighbour -> !neighbour.isValid(-1, boardSize));
        return neighbours;

    }
}
