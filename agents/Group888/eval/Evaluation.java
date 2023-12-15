package agents.Group888.eval;

import agents.Group888.dtypes.DisjointSet;
import agents.Group888.dtypes.Point;

import static agents.Group888.BestAgent.boardSize;

public class Evaluation {

    protected DisjointSet maximising;
    protected DisjointSet minimising;

    /**
     * Superclass for all evaluation classes
     * @param maximising Disjoint set containing all points controlled by the maximising player
     * @param minimising Disjoint set containing all points controlled by the minimising player
     */
    protected Evaluation(DisjointSet maximising, DisjointSet minimising) {

        this.maximising = maximising;
        this.minimising = minimising;
        
    }


    /**
     * Converts a key used to represent a point in a HashMap to a Point object
     * @param key The key representing the point
     * @return Point
     */
    protected Point keyToPoint(int key) {

        int c = key % boardSize;
        int r = (key - c) / boardSize;

        return new Point(r, c);
    }

    /**
     * Obtains an evaluation of the board using the evaluation metric
     * @return an evaluation score
     */
    protected float getEvaluation() { return 0; }

}
