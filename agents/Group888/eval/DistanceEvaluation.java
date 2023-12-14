package eval;
import dtypes.DisjointSet;
import dtypes.Point;
import java.util.*;

public class DistanceEvaluation extends Evaluation {

    record BoardEdge(ArrayList<Point> onBoardEdge, Point superNode) {}
    BoardEdge top, bottom, left, right;

    public DistanceEvaluation(DisjointSet maximising, DisjointSet minimising, int boardSize) {
        super(maximising, minimising, boardSize);

        // Define board boundaries
        this.top = new BoardEdge(new ArrayList<>(), new Point(0, -1));
        for (int i = 0; i < boardSize; i++) { top.onBoardEdge().add(new Point(0, i)); }

        this.bottom = new BoardEdge(new ArrayList<>(), new Point(0, boardSize));
        for (int i = 0; i < boardSize; i++) { bottom.onBoardEdge().add(new Point(boardSize - 1, i)); }

        this.left = new BoardEdge(new ArrayList<>(), new Point(-1, 0));
        for (int i = 0; i < boardSize; i++) { left.onBoardEdge().add(new Point(i, 0)); }

        this.right = new BoardEdge(new ArrayList<>(), new Point(boardSize, 0));
        for (int i = 0; i < boardSize; i++) { right.onBoardEdge().add(new Point(i, boardSize - 1)); }
    }

    private int Dijkstra(boolean isMaximising) {

        record Node(int cost, Point point) {}

        PriorityQueue<Node> heap = new PriorityQueue<>(
                Comparator.comparing(Node::cost)
        );

        ArrayList<Point> visited = new ArrayList<>();

        Point start, end;
        if (isMaximising) {
            start = top.superNode();
            end = bottom.superNode();
        } else {
            start = left.superNode();
            end = right.superNode();
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
            }
        }

        return Integer.MAX_VALUE;

    }

    private ArrayList<Point> getNeighbours(Point currentPoint) {

        ArrayList<Point> neighbours;

        // Check whether a node is a supernode
        if (Objects.equals(currentPoint, top.superNode())) { neighbours = top.onBoardEdge(); }
        else if (Objects.equals(currentPoint, left.superNode())) { neighbours = left.onBoardEdge(); }
        else { neighbours = currentPoint.getNeighbours(boardSize); }

        // If it's not a supernode, check if it has any supernode neighbours
        if (bottom.onBoardEdge().contains(currentPoint)) { neighbours.add(bottom.superNode()); }
        if (right.onBoardEdge().contains(currentPoint)) { neighbours.add(right.superNode()); }
        return neighbours;
    }

    public float getEvaluation() {

        int costForMaximisingPlayer = Dijkstra(true);
        int costForMinimisingPlayer = Dijkstra(false);
        return (float) (-costForMaximisingPlayer + costForMinimisingPlayer);
    }

//    private Point getLeftmostPoint(ArrayList<Integer> connectedPoints) {
//
//        Point leftmostPoint = new Point(10, 0);
//        for (int key : connectedPoints) {
//            Point currentPoint = keyToPoint(key);
//            if (currentPoint.x() < leftmostPoint.x()) {
//                leftmostPoint = currentPoint;
//            }
//        }
//
//        return leftmostPoint;
//    }
//
//    private Point getRightmostPoint(ArrayList<Integer> connectedPoints) {
//
//        Point rightmostPoint = new Point(0, 0);
//        for (int key : connectedPoints) {
//            Point currentPoint = keyToPoint(key);
//            if (currentPoint.x() > rightmostPoint.x()) {
//                rightmostPoint = currentPoint;
//            }
//        }
//
//        return rightmostPoint;
//    }
//
//    public float getEvaluation() {
//        float eval = 0;
//
//        for (ArrayList<Integer> connectedPoints : maximising.getAllElements()) {
//            Point leftmostPoint = getLeftmostPoint(connectedPoints);
//            Point rightmostPoint = getRightmostPoint(connectedPoints);
//
//
//
//        }
//
//        return eval;
//    }


}
