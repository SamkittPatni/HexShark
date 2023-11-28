package COMP34111_p86963sp.agents.Group888;

import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import COMP34111_p86963sp.agents.Group888.DisjointSet;

class BestAgent{
    public static String HOST = "127.0.0.1";
    public static int PORT = 1234;

    private Socket s;
    private PrintWriter out;
    private BufferedReader in;

    private String colour = "R";
    private int turn = 0;
    private int boardSize = 11;

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
                colour = msg[2];
                if (colour.equals("R")){
                    // so sad ):
                    String board = "";
                    for (int i = 0; i < boardSize; i++){
                        String line = "";
                        for (int j = 0; j < boardSize; j++)
                            line += "0";
                        board += line;
                        if (i < boardSize - 1) board += ",";
                    }
                    makeMove(board);
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

    private int[] minimax(String board, int depth, boolean isMaximizingPlayer, int alpha, int beta){
        if (depth == 0 || gameIsOver(board)){
            return new int[]{heuristic(board), -1, -1};
        }

        int bestScore;
        int[] bestMove = {-1, -1};

        if (isMaximizingPlayer){
            bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < boardSize; i++){
                for (int j = 0; j < boardSize; j++){
                    if (board[i][j] == '0'){
                        board[i][j] = 'R';
                        int score = minimax(board, depth - 1, false, alpha, beta)[0];
                        board[i][j] = '0';
                        if (score > bestScore){
                            bestScore = score;
                            bestMove = new int[]{i, j};
                        }
                        alpha = Math.max(alpha, score);
                        if (beta <= alpha) break;
                    }
                }
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < boardSize; i++){
                for (int j = 0; j < boardSize; j++){
                    if (board[i][j] == '0'){
                        board[i][j] = 'B';
                        int score = minimax(board, depth - 1, true, alpha, beta)[0];
                        board[i][j] = '0';
                        if (score < bestScore){
                            bestScore = score;
                            bestMove = new int[]{i, j};
                        }
                        beta = Math.min(beta, score);
                        if (beta <= alpha) break;
                    }
                }
            }
        }

        return new int[]{bestScore, bestMove[0], bestMove[1]};
    }


    private void makeMove(String board){
        if (turn == 2 && new Random().nextInt(2) == 1){
            sendMessage("SWAP\n");
            return;
        }

        String[] lines = board.split(",");
        ArrayList<int[]> choices = new ArrayList<int[]>();

        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                if (lines[i].charAt(j) == '0'){
                    int[] newElement = {i, j};
                    choices.add(newElement);
                }

        if (choices.size() > 0){
            int[] choice = choices.get(new Random().nextInt(choices.size()));
            String msg = "" + choice[0] + "," + choice[1] + "\n";
            sendMessage(msg);
        }
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
