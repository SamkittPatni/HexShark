package agents.Group888.eval;
import agents.Group888.dtypes.DisjointSet;
import agents.Group888.dtypes.Point;
import java.util.*;

import static agents.Group888.BestAgent.boardSize;

public class DistanceEvaluation extends Evaluation {

    public record Boundary(ArrayList<Point> points, Point superPoint) {}

    public static Boundary topBoundary = new Boundary(new ArrayList<>(), new Point(Integer.MIN_VALUE, 0));
    public static Boundary bottomBoundary = new Boundary(new ArrayList<>(), new Point(Integer.MAX_VALUE, 0));
    public static Boundary leftBoundary = new Boundary(new ArrayList<>(), new Point(0, Integer.MIN_VALUE));
    public static Boundary rightBoundary = new Boundary(new ArrayList<>(), new Point(0, Integer.MAX_VALUE));

    static {

        for (int i = 0; i < boardSize; i++) {

            topBoundary.points().add(new Point(0, i));
            bottomBoundary.points().add(new Point(boardSize - 1, i));
            leftBoundary.points().add(new Point(i, 0));
            rightBoundary.points().add(new Point(i, boardSize - 1));

        }

    }

    public DistanceEvaluation(DisjointSet maximising, DisjointSet minimising) {
        super(maximising, minimising);
        
    }

    private int Dijkstra(boolean isMaximising) {

        record Node(int cost, Point point) {}

        PriorityQueue<Node> heap = new PriorityQueue<>(
                Comparator.comparing(Node::cost)
        );

        ArrayList<Point> visited = new ArrayList<>();

        Point start, end;
        if (isMaximising) {
            start = topBoundary.superPoint();
            end = bottomBoundary.superPoint();
        } else {
            start = leftBoundary.superPoint();
            end = rightBoundary.superPoint();
        }

        heap.offer(new Node(0, start));

        while (!heap.isEmpty()) {

            Node currentNode = heap.poll();
            int cost = currentNode.cost();
            Point currentPoint = currentNode.point();

            if (visited.contains(currentPoint)) { continue; }
            visited.add(currentPoint);

            if (Objects.equals(currentPoint, end)) { return cost; }

            ArrayList<Point> neighbours = getNeighbours(currentPoint);

            for (Point neighbour : neighbours) {

                if (isMaximising) {
                    if (!minimising.hasPoint(neighbour)) {
                        if (maximising.hasPoint(neighbour)) {
                            heap.offer(new Node(cost, neighbour));
                        }
                        else {
                            heap.offer(new Node(cost + 1, neighbour));
                        }
                    }
                }

                else {
                    if (!maximising.hasPoint(neighbour)) {
                        if (minimising.hasPoint(neighbour)) {
                            heap.offer(new Node(cost, neighbour));
                        }
                        else {
                            heap.offer(new Node(cost + 1, neighbour));
                        }
                    }
                }

                if (Objects.equals(neighbour, end)) {
                    heap.offer(new Node(cost + 1, neighbour));
                }
                
            }
        }

        return Integer.MAX_VALUE;

    }

    private ArrayList<Point> getNeighbours(Point currentPoint) {

        ArrayList<Point> neighbours;

        // Check whether a node is a superPoint
        if (Objects.equals(currentPoint, topBoundary.superPoint())) { neighbours = topBoundary.points(); }
        else if (Objects.equals(currentPoint, leftBoundary.superPoint())) { neighbours = leftBoundary.points(); }
        else { neighbours = currentPoint.getNeighbours(); }

        // If it's not a superPoint, check if it has any superPoint neighbours
        if (bottomBoundary.points().contains(currentPoint)) { neighbours.add(bottomBoundary.superPoint()); }
        if (rightBoundary.points().contains(currentPoint)) { neighbours.add(rightBoundary.superPoint()); }
        return neighbours;
    }

    public float getEvaluation() {

        int costForMaximisingPlayer = Dijkstra(true);
        int costForMinimisingPlayer = Dijkstra(false);
        return (float) (-costForMaximisingPlayer + costForMinimisingPlayer);
    }

}
