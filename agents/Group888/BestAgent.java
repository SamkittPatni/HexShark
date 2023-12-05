package COMP34111_p86963sp.agents.Group888;

import java.net.*;
import java.util.ArrayList;
import java.io.*;
// import COMP34111_p86963sp.agents.Group888.DisjointSet;

public class BestAgent{
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

    private void Connect() throws UnknownHostException, IOException{
        s = new Socket(HOST, PORT);
        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    private String getMessage() throws IOException{
        return in.readLine();
    }

    private void sendMessage(String msg){
        out.print(msg); out.flush();
    }

    private void closeConnection() throws IOException{
        s.close();
        out.close();
        in.close();
    }

    public void run(){
        // connect to the engine
        try{
            Connect();
        } catch (UnknownHostException e){
            System.out.println("ERROR: Host not found.");
            return;
        } catch (IOException e){
            System.out.println("ERROR: Could not establish I/O.");
            return;
        }

        while (true){
            // receive messages
            try{
                String msg = getMessage();
                boolean res = interpretMessage(msg);
                if (res == false) break;
            } catch (IOException e){
                System.out.println("ERROR: Could not establish I/O.");
                return;
            }
        }

        try{
            closeConnection();
        } catch (IOException e){
            System.out.println("ERROR: Connection was already closed.");
        }
    }

    private boolean interpretMessage(String s){
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
                    // so sad ):
                    // String board = "";
                    // for (int i = 0; i < boardSize; i++){
                    //     String line = "";
                    //     for (int j = 0; j < boardSize; j++)
                    //         line += "0";
                    //     board += line;
                    //     if (i < boardSize - 1) board += ",";
                    // }
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

    private float minimax(int depth, boolean isMaximizingPlayer, int[] choice, DisjointSet red, DisjointSet blue, float alpha, float beta){
        DisjointSet r = new DisjointSet(red);
        DisjointSet b = new DisjointSet(blue);
        if (depth == 0){
            if (choice[0] != -1) {
                if (isMaximizingPlayer) {
                    r.add(choice[0], choice[1]);
                }
                else {
                    b.add(choice[0], choice[1]);
                }
            }
            return evaluation(r, b);
        }

        // int bestScore;
        // int[] bestMove = {-10, -10};

        if (choice[0] == -1) {
            isMaximizingPlayer = !isMaximizingPlayer;
        }

        if (isMaximizingPlayer){
            if (choice[0] != -1) {
                r.add(choice[0], choice[1]);
            }
            if (checkWinCondition(r, b)) {
                // System.out.println("Max");
                return Integer.MAX_VALUE;
            }
            if (r.size() + r.size() == boardSize*boardSize) {
                return evaluation(r, b);
            }
            int [] newMove = {};
            alpha = Integer.MIN_VALUE; 
            if (isFirstMove && choice[0] != -1) {
                newMove = new int[]{-1, -1};
                float score = minimax(depth - 1, !isMaximizingPlayer, newMove, r, b, alpha, beta);
                //blue.remove(newMove[0], newMove[1]);
                alpha = Math.max(alpha, score);
            }
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (r.find(i*boardSize + j) == -1 && b.find(i*boardSize + j) == -1) {
                        newMove = new int[]{i,j};
                        if (alpha >= beta) {
                            break;
                        }
                        float score = minimax(depth - 1, !isMaximizingPlayer, newMove, r, b, alpha, beta);
                        // blue.remove(newMove[0], newMove[1]);
                        alpha = Math.max(alpha, score);
                    }
                }
                if (alpha > beta) {
                    break;
                }
            }
            // System.out.println(alpha);
            return alpha;
            // for (int i = 0; i < boardSize; i++){
            //     for (int j = 0; j < boardSize; j++){
            //         if (board[i][j] == '0'){
            //             board[i][j] = 'R';
            //             int score = minimax(board, depth - 1, false, alpha, beta)[0];
            //             board[i][j] = '0';
            //             if (score > bestScore){
            //                 bestScore = score;
            //                 bestMove = new int[]{i, j};
            //             }
            //             alpha = Math.max(alpha, score);
            //             if (beta <= alpha) break;
            //         }
            //     }
            // }
        } else {
            if (choice[0] != -1) {
                b.add(choice[0], choice[1]);
            }
            if (checkWinCondition(r, b)) {
                // System.out.println("MIN");
                return Integer.MIN_VALUE;
            }
            if (r.size() + b.size() == boardSize*boardSize) {
                return evaluation(r, b);
            }
            int [] newMove = {};
            beta = Integer.MAX_VALUE; 
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (r.find(i*boardSize + j) == -1 && b.find(i*boardSize + j) == -1) {
                        newMove = new int[]{i,j};
                        if (alpha >= beta) {
                            break;
                        }
                        float score = minimax(depth - 1, !isMaximizingPlayer, newMove, r, b, alpha, beta);
                        // red.remove(newMove[0], newMove[1]);
                        beta = Math.min(beta, score);
                    }
                }
                if (alpha > beta) {
                    break;
                }
            }
            // System.out.println(beta);
            return beta;

            // bestScore = Integer.MAX_VALUE;
            // for (int i = 0; i < boardSize; i++){
            //     for (int j = 0; j < boardSize; j++){
            //         if (board[i][j] == '0'){
            //             board[i][j] = 'B';
            //             int score = minimax(board, depth - 1, true, alpha, beta)[0];
            //             board[i][j] = '0';
            //             if (score < bestScore){
            //                 bestScore = score;
            //                 bestMove = new int[]{i, j};
            //             }
            //             beta = Math.min(beta, score);
            //             if (beta <= alpha) break;
            //         }
            //     }
            // }
        }

        // return new int[]{bestScore, bestMove[0], bestMove[1]};
    }


    private void makeMove(String board){

        String[] lines = board.split(",");
        ArrayList<int[]> choices = new ArrayList<int[]>();

        if (board.equals("")){                  // Only called during START, adds all board positions to choices
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    int[] newElement = {i, j};
                    choices.add(newElement);
                }
            }
        }
        else {                                          // If not start
            if (turn == 2) {
                int [] newelement = {-1, -1};            // Set (-1, -1) as SWAP
                // System.out.println(choices.get(0)[0] + " " + choices.get(0)[0]);
                choices.add(newelement);
                // System.out.println(choices.get(0)[0] + " " + choices.get(0)[0]);
            }
            boolean oppMoveFlag = false;                // Stops us from unnecessary checking of moves
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    char pin = lines[i].charAt(j);
                    if (pin == '0'){                    // If move has not been played, add it to choices
                        int[] newElement = {i, j};
                        choices.add(newElement);
                    }
                    else if (Character.toString(pin) != colour) {   // If it is a played move, check if it is opponents move
                        if (!oppMoveFlag) {
                            if (colour == "R") {                    // If we have not seen the move yet and we are red, 
                                if(blueMoves.find(i*boardSize+j) == -1) { // we add it to blue's move as it is the new move
                                    blueMoves.add(i, j);                    // Used during evaluation
                                    oppMoveFlag = true;
                                }
                            }
                            else {                                  // Same thing for blue
                                if(redMoves.find(i*boardSize+j) == -1) {
                                    redMoves.add(i, j);
                                    oppMoveFlag = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (choices.size() > 0){    // Checks if no moves are left
            int [] bestMove = {-1, -1};     // Sets base best move to random value;
            float bestEval = 0;
            if (colour.equals("R")) {
                bestEval = Integer.MIN_VALUE;    // Sets base best evaluation to minimum value;
            }
            else {
                bestEval = Integer.MAX_VALUE;    // Sets base best evaluation to minimum value;
            }
            int depth = 4;                      // Setting depth to 1 for testing
            float alpha = Integer.MIN_VALUE;      // Sets base alpha value
            float beta = Integer.MAX_VALUE;       // base beta value
            boolean isMaximizing = false;       
            if (colour.equals("R")) {   // Sets us as a maximizing or minimizing player based on colour
                isMaximizing = true;
            }
            for (int i = 0; i < choices.size(); i++) {  // Goes through all the moves and sets chooses the best
                // System.out.println(choices.get(i)[0] + " " + choices.get(i)[0]);
                float eval = minimax(depth, isMaximizing, choices.get(i), redMoves, blueMoves, alpha, beta);
                if (isMaximizing) {
                    // redMoves.remove(choices.get(i)[0], choices.get(i)[1]);
                }
                else {
                    // blueMoves.remove(choices.get(i)[0], choices.get(i)[1]);
                }
                // System.err.println(eval);
                if (colour.equals("R")) {
                    if (eval > bestEval) { // IF current move is better than best move so far
                        bestEval = eval;
                        bestMove = choices.get(i);
                    }
                }
                else {
                    if (eval < bestEval) { // IF current move is better than best move so far
                        bestEval = eval;
                        bestMove = choices.get(i);
                    }
                }
            }
            if (colour.equals("R")) {   // Add move to red if we are red
                redMoves.add(bestMove[0], bestMove[1]);
            }
            else {                              // else add to blue
                blueMoves.add(bestMove[0], bestMove[1]);
            }
            String msg = "" + bestMove[0] + "," + bestMove[1] + "\n";
            if (bestMove[0] == -1) {
                msg = "SWAP\n";                 // If swap is best move, set it to that
            }
            sendMessage(msg);   // Make move
        }
    }

    public boolean checkWinCondition(DisjointSet red, DisjointSet blue) {
        for (int sets : red.getAllSets()) {
            // System.out.println(red.setSize(sets));
            boolean inSet = false;
            if (red.setSize(sets) >= boardSize) {

                for (int i = 0; i < boardSize; i++) {
                    if (red.find(i) == sets) {
                        inSet = true;
                    }
                }
                for (int i = boardSize * (boardSize - 1); i < boardSize * boardSize; i++) {
                    if (red.find(i) == sets && inSet) {
                        return true;
                    }
                }
            }
        }

        for (int sets : blue.getAllSets()) {
            boolean inSet = false;
            if (blue.setSize(sets) >= boardSize) {
                for (int i = 0; i < boardSize * boardSize; i+=boardSize) {
                    if (blue.find(i) == sets) {
                        inSet = true;
                    }
                }
                for (int i = boardSize - 1; i < boardSize * boardSize; i+=boardSize) {
                    if (blue.find(i) == sets && inSet) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public float evaluation(DisjointSet red, DisjointSet blue) {
        return 0;
    }

    public static String opp(String c){
        if (c.equals("R")) return "B";
        if (c.equals("B")) return "R";
        return "None";
    }


    public static void main(String args[]){
        BestAgent agent = new BestAgent();
        agent.run();
    }
}


