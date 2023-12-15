package agents.Group888.eval;

import agents.Group888.dtypes.DisjointSet;
import agents.Group888.dtypes.Point;
import static agents.Group888.BestAgent.boardSize;

public class CenterEvaluation extends Evaluation{

    public CenterEvaluation(DisjointSet maximising, DisjointSet minimising) { super(maximising, minimising); }

    public float getEvaluation() {

        float eval = 0;
        Point center = new Point(boardSize / 2, boardSize / 2);
        if (maximising.hasPoint(center)) {
            eval += 10;
        }
        else if (minimising.hasPoint(center)) {
            eval -= 10;
        }

        int neighbourRatio = 0;
        for (Point neighbour : center.getNeighbours()) {
            if (maximising.hasPoint(neighbour)) {
                eval += 1;
                neighbourRatio += 1;
            }

            if (minimising.hasPoint(neighbour)) {
                eval -= 1;
                neighbourRatio -= 1;
            }
        }

        if (neighbourRatio > 3) {
            eval -= 4;
        }
        else if (neighbourRatio < -3) {
            eval += 4;
        }

        return eval;
    }
}
