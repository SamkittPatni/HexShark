import java.util.ArrayList;
import java.util.Arrays;

public record Point(int x, int y) {

    public int toKey(int columns) {
        return x + y * columns;
    }

    public boolean isValid(int minLimit, int maxLimit) {

        boolean belowUpperBound = x < maxLimit && y < maxLimit;
        boolean aboveLowerBound = x > minLimit && y > minLimit;
        return aboveLowerBound && belowUpperBound;

    }

    // (x-1,y) (x+1,y), (x,y-1), (x,y+1), (x-1,y+1), (x+1,y-1)
    public ArrayList<Point> getNeighbours(int boardSize) {

        ArrayList<Point> neighbours = new ArrayList<>(
                Arrays.asList(
                        new Point(x - 1, y),
                        new Point(x + 1, y),
                        new Point(x, y - 1),
                        new Point(x, y + 1),
                        new Point(x - 1, y + 1),
                        new Point(x + 1, y - 1)
                )
        );

        neighbours.removeIf(neighbour -> !neighbour.isValid(0, boardSize));
        return neighbours;

    }
}
