import dtypes.DisjointSet;
import dtypes.Point;
import eval.BridgeFactorEvaluation;
import eval.CenterEvaluation;
import eval.DistanceEvaluation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;

public class BestAgent {

    public static String HOST = "127.0.0.1";
    public static int PORT = 1234;

    private Socket s;
    private PrintWriter out;
    private BufferedReader in;

    private String colour = "R";
    private int turn = 0;
    private int boardSize = 11;
    private DisjointSet redMoves;
    private DisjointSet blueMoves;
    private boolean isFirstMove;

    // Swap constant
    private final Point SWAP = new Point(-1, -1);

    public static void main(String[] args) {

        BestAgent agent = new BestAgent();
        agent.run();

    }

    private void Connect() throws IOException {

        s = new Socket(HOST, PORT);
        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));

    }

    private String getMessage() throws IOException { return in.readLine(); }

    private void sendMessage(String msg) {

        out.print(msg);
        out.flush();

    }

    private void closeConnection() throws IOException {

        s.close();
        out.close();
        in.close();

    }

    private boolean interpretMessage(String s) {

        turn++;

        String[] msg = s.strip().split(";");
        switch (msg[0]){
            case "START":
                boardSize = Integer.parseInt(msg[1]);
                redMoves = new DisjointSet(Integer.parseInt(msg[1]), Integer.parseInt(msg[1]));
                blueMoves = new DisjointSet(Integer.parseInt(msg[1]), Integer.parseInt(msg[1]));
                colour = msg[2];
                isFirstMove = true;
                if (colour.equals("R")){
                    // So sad ):
                    makeMove("");
                }
                break;

            case "CHANGE":
                if (msg[3].equals("END")) return false;
                if (msg[1].equals("SWAP")) colour = opp(colour);
                if (msg[3].equals(colour)) makeMove(msg[2]);
                break;

            default:
                return false;
        }

        return true;

    }

    public void run() {

        // Connect to the engine
        try {
            Connect();
        } catch (UnknownHostException e) {
            System.out.println("ERROR: Host not found.");
            return;
        } catch (IOException e) {
            System.out.println("ERROR: Could not establish I/O.");
            return;
        }

        while (true) {

            // Receive messages
            try {
                String msg = getMessage();
                boolean res = interpretMessage(msg);
                if (!res) break;
            } catch (IOException e) {
                System.out.println("ERROR: Could not establish I/O.");
                return;
            }
        }

        try {
            closeConnection();
        } catch (IOException e) {
            System.out.println("ERROR: Connection was already closed.");
        }
    }

    private float minimax(
            int depth,
            boolean isMaximizingPlayer,
            Point choice,
            DisjointSet red, DisjointSet blue,
            float alpha, float beta
    ) {

        DisjointSet r = new DisjointSet(red);
        DisjointSet b = new DisjointSet(blue);

        boolean swapped = Objects.equals(choice, SWAP);

        // If a player has decided to swap, then change the maximising player
        if (swapped) { isMaximizingPlayer = !isMaximizingPlayer; }

        // Otherwise, add the move to the corresponding set
        else {
            if (isMaximizingPlayer) { r.add(choice); }
            else { b.add(choice); }
        }

        if (checkWinCondition(r, b)) {
            if (isMaximizingPlayer) { return Integer.MAX_VALUE; }
            else { return Integer.MIN_VALUE; }
        }

        if (depth == 0) {

            // Return evaluation score once moves have been played
//            System.out.println(turn);
            return getEvaluationScore(r, b);

        }

        // If the board is full at any depth, return the evaluation
        if (r.size() + b.size() == boardSize * boardSize) { return getEvaluationScore(r, b); }

        Point newMove;
        if (isMaximizingPlayer) { alpha = Integer.MIN_VALUE; }
        else { beta = Integer.MAX_VALUE; }

        // Check if swapping is the best first move to make for the opponent
        if (isFirstMove && !swapped && isMaximizingPlayer) {

            newMove = SWAP;
            isFirstMove = false;

            // Obtain an evaluation, when the minimizing player makes a swap
            float score = minimax(depth - 1, true, newMove, r, b, alpha, beta);
            alpha = Math.max(alpha, score);

        }

        // Play all possible moves available
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {

                // Check if the point is a blank tile
                if (r.find(i * boardSize + j) == -1 && b.find(i * boardSize + j) == -1) {

                    newMove = new Point(i, j);

                    // Prune all bad options
                    if (alpha > beta) { break; }

                    // Continue playing the game until correct depth has been met
                    float score = minimax(depth - 1, !isMaximizingPlayer, newMove, r, b, alpha, beta);

                    if (isMaximizingPlayer) { alpha = Math.max(alpha, score); }
                    else { beta = Math.min(beta, score); }

                }
            }

            if (alpha > beta) { break; }

        }

        if (isMaximizingPlayer) { return alpha; }
        else { return beta; }

    }

    public boolean checkWinCondition(DisjointSet red, DisjointSet blue) {

        // Check win for maximising player
        for (int sets : red.getAllSets()) {

            boolean inSet = false;
            if (red.setSize(sets) >= boardSize) {

                for (int i = 0; i < boardSize; i++) {
                    if (red.find(i) == sets) { inSet = true; }
                }

                for (int i = boardSize * (boardSize - 1); i < boardSize * boardSize; i++) {
                    if (red.find(i) == sets && inSet) { return true; }
                }
            }
        }

        // Check win for minimizing player
        for (int sets : blue.getAllSets()) {

            boolean inSet = false;
            if (blue.setSize(sets) >= boardSize) {

                for (int i = 0; i < boardSize * boardSize; i += boardSize) {
                    if (blue.find(i) == sets) { inSet = true; }
                }

                for (int i = boardSize - 1; i < boardSize * boardSize; i += boardSize) {
                    if (blue.find(i) == sets && inSet) { return true; }
                }
            }
        }

        return false;
    }

    private void makeMove(String board){

        String[] lines = board.split(",");
        ArrayList<Point> choices = new ArrayList<>();

        // Only called during START, adds all board positions to choices
        if (board.isEmpty()) {

            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    choices.add(new Point(i, j));
                }
            }

        }

        // If not starting move
        else {

            if (turn == 2 && colour.equals("B")) { choices.add(SWAP); }

            // Stops us from unnecessary checking of moves
            boolean oppMoveFlag = false;

            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {

                    char pin = lines[i].charAt(j);
                    Point possibleMove = new Point(i, j);

                    if (pin == '0'){

                        // If move has not been played, add it to choices
                        choices.add(possibleMove);

                    }

                    // If it is a played move, check if it is opponents move
                    else if (!Character.toString(pin).equals(colour)) {

                        if (!oppMoveFlag) {

                            // If we have not seen the move yet and we are red,
                            if (Objects.equals(colour, "R")) {

                                // we add it to blue's move as it is the new move
                                if (blueMoves.find(i * boardSize + j) == -1) {
                                    blueMoves.add(possibleMove);
                                    oppMoveFlag = true;
                                }

                            }

                            else {

                                // Same thing for blue
                                if (redMoves.find(i * boardSize + j) == -1) {
                                    redMoves.add(possibleMove);
                                    oppMoveFlag = true;

                                }
                            }
                        }
                    }
                }
            }
        }

        // Checks if no moves are left
        if (!choices.isEmpty()) {

            // Sets base best move to random value;
            Point bestMove = SWAP;
            float bestEval;

            if (colour.equals("R")) {

                // Sets base best evaluation to minimum value;
                bestEval = Integer.MIN_VALUE;

            }

            else {

                // Sets base best evaluation to maximum value;
                bestEval = Integer.MAX_VALUE;

            }

            // Setting depth to 1 for testing
            int depth = 1;

            // Sets base alpha value
            float alpha = Integer.MIN_VALUE;

            // base beta value
            float beta = Integer.MAX_VALUE;

            // Sets us as a maximizing or minimizing player based on colour
            boolean isMaximizing = colour.equals("R");

            // Goes through all the moves and sets chooses the best
            for (Point choice : choices) {

                float eval = minimax(depth, isMaximizing, choice, redMoves, blueMoves, alpha, beta);

                if (isMaximizing) {

                    // If current move is better than best move so far
                    if (eval > bestEval) {
                        bestEval = eval;
                        bestMove = choice;
                    }
                }

                else {

                    // If current move is better than best move so far
                    if (eval < bestEval) {
                        bestEval = eval;
                        bestMove = choice;
                    }
                }
            }

            // Add move to corresponding player's set
            if (colour.equals("R")) { redMoves.add(bestMove); }
            else { blueMoves.add(bestMove); }

            String msg = bestMove.r() + "," + bestMove.c() + "\n";

            // If swap is best move, set it to that
            if (Objects.equals(bestMove, SWAP)) { msg = "SWAP\n"; }

//            Point check = new Point(0,1);
//            for (int i = 0; i < boardSize; i++) {
//                System.out.println(check.getNeighbours(boardSize).get(i));
//            }

            // Make move
            sendMessage(msg);
        }
    }


    // Switches player tag
    public static String opp(String c) {

        if (c.equals("R")) return "B";
        if (c.equals("B")) return "R";
        return "None";

    }

    // TODO: Add more heuristics to this function
    public float getEvaluationScore(DisjointSet red, DisjointSet blue) {

//        BridgeFactorEvaluation bridgeFactorEvaluation = new BridgeFactorEvaluation(red, blue, boardSize);
//        CenterEvaluation centerEvaluation = new CenterEvaluation(red, blue, boardSize);
//        return (bridgeFactorEvaluation.getEvaluation() + centerEvaluation.getEvaluation()) / 2;
//        System.out.println("Here");
        DistanceEvaluation distanceEvaluation = new DistanceEvaluation(red, blue, boardSize);
//        System.out.println(distanceEvaluation.getEvaluation());
        return distanceEvaluation.getEvaluation();

    }
}


