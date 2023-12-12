package eval;

import dtypes.DisjointSet;
import dtypes.Point;
import java.util.ArrayList;
import java.util.Arrays;

public class BridgeFactorEvaluation extends Evaluation {

    public BridgeFactorEvaluation(DisjointSet maximising, DisjointSet minimising, int boardSize) {
        super(maximising, minimising, boardSize);
    }

    // Determines whether a spot is available or not
    private boolean isBridgeValid(Point point) {

        boolean notOccupied = !maximising.hasPoint(point) && !minimising.hasPoint(point);
        return point.isValid(0, boardSize) && notOccupied;

    }

    // Obtains all bridges that are valid from a position
    private ArrayList<Point> getValidBridges(Point point) {

        int x = point.x();
        int y = point.y();

        ArrayList<Point> bridgePositions = new ArrayList<>(
                Arrays.asList(
                        new Point(x + 2, y - 1),
                        new Point(x + 1, y - 2),
                        new Point(x + 1, y + 1),
                        new Point(x - 1, y - 1),
                        new Point(x - 1, y + 2),
                        new Point(x - 2, y + 1)
                )
        );
        
        bridgePositions.removeIf(bridgePosition -> !isBridgeValid(bridgePosition));
        return bridgePositions;

    }


    // Obtains a score based on the amount of possible bridges that can be made
    public int getEvaluation() {

        int eval = 0;

        for (int key : maximising.getKeys()) {
            Point position = keyToPoint(key);
            eval += getValidBridges(position).size();
        }

        for (int key : minimising.getKeys()) {
            Point position = keyToPoint(key);
            eval -= getValidBridges(position).size();
        }

        return eval;

    }
}
