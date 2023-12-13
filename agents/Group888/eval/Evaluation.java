package eval;

import dtypes.DisjointSet;
import dtypes.Point;

public class Evaluation {

    protected DisjointSet maximising;
    protected DisjointSet minimising;
    protected int boardSize;

    /**
     * Superclass for all evaluation classes
     * @param maximising Disjoint set containing all points controlled by the maximising player
     * @param minimising Disjoint set containing all points controlled by the minimising player
     * @param boardSize Number of columns on the Hex board
     */
    protected Evaluation(DisjointSet maximising, DisjointSet minimising, int boardSize) {

        this.maximising = maximising;
        this.minimising = minimising;
        this.boardSize = boardSize;
        
    }


    /**
     * Converts a key used to represent a point in a HashMap to a Point object
     * @param key The key representing the point
     * @return Point
     */
    protected Point keyToPoint(int key) {

        int x = key % boardSize;
        int y = (key - x) / boardSize;

        return new Point(x, y);
    }

    /**
     * Obtains an evaluation of the board using the evaluation metric
     * @return an evaluation score
     */
    protected float getEvaluation() { return 0; }

}
