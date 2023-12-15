package agents.Group888.eval;

import agents.Group888.dtypes.DisjointSet;
import agents.Group888.dtypes.Point;
import static agents.Group888.BestAgent.boardSize;
import java.util.ArrayList;
import java.util.Arrays;

public class BridgeFactorEvaluation extends Evaluation {

    public BridgeFactorEvaluation(DisjointSet maximising, DisjointSet minimising) {
        super(maximising, minimising);
    }

    // Determines whether a spot is available or not
    private boolean isBridgeOwnedByPlayer(DisjointSet pointsOwnedByPlayer, Point point) {

        return point.isValid(0, boardSize) && pointsOwnedByPlayer.hasPoint(point);

    }

    // Obtains all bridges that are valid from a position
    private ArrayList<Point> getBridgesOwnedByPlayer(DisjointSet pointsOwnedByPlayer, Point point) {

        int r = point.r();
        int c = point.c();

        ArrayList<Point> bridgePositions = new ArrayList<>(
                Arrays.asList(
                        new Point(r + 2, c - 1),
                        new Point(r + 1, c - 2),
                        new Point(r + 1, c + 1),
                        new Point(r - 1, c - 1),
                        new Point(r - 1, c + 2),
                        new Point(r - 2, c + 1)
                )
        );
        
        bridgePositions.removeIf(bridgePosition -> !isBridgeOwnedByPlayer(pointsOwnedByPlayer, bridgePosition));
        return bridgePositions;

    }

    // Obtains a score based on the amount of possible bridges that can be made
    public float getEvaluation() {

        float eval = 0;

        for (int key : maximising.getKeys()) {
            Point position = keyToPoint(key);
            eval += getBridgesOwnedByPlayer(maximising, position).size();
        }

        for (int key : minimising.getKeys()) {
            Point position = keyToPoint(key);
            eval -= getBridgesOwnedByPlayer(minimising, position).size();
        }

        return eval;

    }
}
