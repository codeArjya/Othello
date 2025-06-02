import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

public class Othello extends JPanel {

    final char PLAYER = '⚫';
    final char AI = '⚪';
    final char EMPTY = ' ';
    final int GRID_SIZE = 8;
    final int BOX_SIZE = 100;
    final int PANEL_SIZE = BOX_SIZE * GRID_SIZE;
    char[][] board = new char[GRID_SIZE][GRID_SIZE];

    private int aiDepth;
    private boolean playerTurn;
    private Stack<char[][]> undoStack = new Stack<>();
    private Stack<char[][]> redoStack = new Stack<>();
    private javax.swing.JLabel statusBar = null;
    private Runnable onPass = null;
    private Runnable onWin = null;
    private Point lastMove = null;
    private List<Point> legalMoves = new ArrayList<>();

   public Othello(int depth) {
        // Store chosen AI depth
        this.aiDepth = depth;
        this.playerTurn = true;
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        setUpBoard();

        // Compute initial legal moves for the human
        legalMoves = computeLegalMovesFor(PLAYER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 1) Ignore clicks if it’s not human's turn
                if (!playerTurn) return;

                int c = e.getX() / BOX_SIZE;
                int r = e.getY() / BOX_SIZE;

                // 2) If human has a valid move at (r, c):
                if (isValidMove(r, c, PLAYER)) {
                    // a) Snapshot for undo + highlight
                    pushUndo();
                    lastMove = new java.awt.Point(r, c);

                    // b) Perform human move
                    getMove(r, c, PLAYER);
                    repaint();

                    // c) Now schedule the AI move with a delay
                    triggerAIMoveWithDelay();
                }
                // 3) Else if human has no moves but AI does → forced pass
                else if (!hasValidMove(PLAYER) && hasValidMove(AI)) {
                    JOptionPane.showMessageDialog(
                        Othello.this.getTopLevelAncestor(),
                        "You have no valid moves. AI will move now.",
                        "Pass",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    // Schedule AI move with delay
                    triggerAIMoveWithDelay();
                }
                // 4) Otherwise: either board spot invalid or neither side can move.
                else if (!hasValidMove(PLAYER) && !hasValidMove(AI)) {
                    // No moves for either side → game over
                    if (onWin != null) onWin.run();
                }
                // Other invalid clicks are ignored
            }
        });
    }

    // Check game state evaluate
    public char evaluate() {
        int playerCount = 0;
        int AICount = 0;
        for (int r = 0; r < GRID_SIZE; r++)// checking if there's any empty spots
            for (int c = 0; c < GRID_SIZE; c++) {
                if (board[r][c] == EMPTY)
                    return 'N';
                if (board[r][c] == PLAYER)
                    playerCount++;
                else if (board[r][c] == AI)
                    AICount++;
            }
        if (!hasValidMove(PLAYER) && !hasValidMove(AI)) {
            if (AICount > playerCount)
                return AI;
            if (AICount < playerCount)
                return PLAYER;
        }
        return 'D';
    }

    // Evaluation Function (to be updated with weights)
    public int evalFunction() {
        int playerCount = 0;
        int AICount = 0;
        for (int r = 0; r < GRID_SIZE; r++)// checking if there's any empty spots
            for (int c = 0; c < GRID_SIZE; c++) {
                if (board[r][c] == PLAYER)
                    playerCount++;
                else if (board[r][c] == AI)
                    AICount++;
            }
        return AICount - playerCount;
    }

    // Minimax
    public int minimax(boolean isMax, int alpha, int beta, int depth) {
        if (depth == 0)
            return evalFunction();
        char eval = evaluate();
        // System.out.println(eval);
        switch (eval) {
            case PLAYER:
                return -100;
            case AI:
                return 100;
            case 'D':
                return evalFunction();
        }
        int best = isMax ? -1000 : 1000;
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (board[r][c] == EMPTY && isValidMove(r, c, isMax ? AI : PLAYER)) {
                    // Need to manually clone each array for some reason??
                    char[][] carboncopy = new char[board.length][];
                    for (int i = 0; i < board.length; i++) {
                        carboncopy[i] = board[i].clone();
                    }
                    getMove(r, c, isMax ? AI : PLAYER);
                    int value = minimax(!isMax, alpha, beta, depth - 1);
                    board = carboncopy;
                    if (isMax) {
                        best = Math.max(best, value);
                        alpha = Math.max(alpha, best);
                    } else {
                        best = Math.min(best, value);
                        beta = Math.min(beta, best);
                    }
                    if (beta <= alpha)
                        return best;
                }
            }
        }
        return best;
    }

    // Move making
    public void getMove(int r, int c, char player) {
        board[r][c] = player;
        for (int moveR = -1; moveR <= 1; moveR++)
            for (int moveC = -1; moveC <= 1; moveC++)
                if (moveR != 0 || moveC != 0)
                    if (canFlip(r, c, moveR, moveC, player))
                        flip(r, c, moveR, moveC, player);
    }

    public boolean getAIMove(int depth) {
        if (hasValidMove(AI)) {
            int[] aiMove = AIMove(depth);
            if (aiMove != null) {
                pushUndo();
                lastMove = new Point(aiMove[0], aiMove[1]);
                getMove(aiMove[0], aiMove[1], AI);
                repaint();
                return true;
            }
        }
        return false;
    }

    public int[] AIMove(int depth) {
        int[] output = new int[2];
        int best = -1000;
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (board[r][c] == EMPTY && isValidMove(r, c, AI)) {
                    char[][] carboncopy = new char[board.length][];
                    for (int i = 0; i < board.length; i++)
                        carboncopy[i] = board[i].clone();
                    getMove(r, c, AI);
                    int current = minimax(false, Integer.MIN_VALUE, Integer.MAX_VALUE, depth - 1);
                    board = carboncopy;
                    if (current > best) {
                        best = current;
                        output[0] = r;
                        output[1] = c;
                    }
                }
            }
        }
        return output;
    }

    public void flip(int r, int c, int moveR, int moveC, char player) {
        char enemy = (player == PLAYER) ? AI : PLAYER;
        r += moveR;
        c += moveC;
        while (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE && board[r][c] == enemy) {
            board[r][c] = player;
            r += moveR;
            c += moveC;
        }
    }

    // Drawing stuff
        public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Background color
        g.setColor(new Color(0, 144, 103));
        g.fillRect(0, 0, PANEL_SIZE, PANEL_SIZE);

        // Draw grid lines
        g.setColor(Color.BLACK);
        for (int i = 0; i <= GRID_SIZE; i++) {
            g.drawLine(i * BOX_SIZE, 0, i * BOX_SIZE, PANEL_SIZE);
            g.drawLine(0, i * BOX_SIZE, PANEL_SIZE, i * BOX_SIZE);
        }

        if (playerTurn) {
            legalMoves = computeLegalMovesFor(PLAYER);
        } else {
            legalMoves.clear();
        }
        for (java.awt.Point p : legalMoves) {
            g.setColor(new java.awt.Color(50, 200, 50, 120));
            int x = p.y * BOX_SIZE;
            int y = p.x * BOX_SIZE;
            g.fillRect(x, y, BOX_SIZE, BOX_SIZE);
        }

        if (lastMove != null) {
            g.setColor(Color.YELLOW);
            int x = lastMove.y * BOX_SIZE;
            int y = lastMove.x * BOX_SIZE;
            g.drawRect(x + 2, y + 2, BOX_SIZE - 4, BOX_SIZE - 4);
        }

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (board[r][c] != EMPTY) {
                    drawPiece(g, r, c, board[r][c]);
                }
            }
        }
    }


    public void drawPiece(Graphics g, int r, int c, char player) {
        int x = c * BOX_SIZE + 10;
        int y = r * BOX_SIZE + 10;
        int d = BOX_SIZE - 20;
        g.setColor(player == PLAYER ? Color.BLACK : Color.WHITE);
        g.fillOval(x, y, d, d);
        g.setColor(Color.BLACK);
        g.drawOval(x, y, d, d);
    }

    public void setUpBoard() {
        for (int r = 0; r < GRID_SIZE; r++)
            for (int c = 0; c < GRID_SIZE; c++)
                board[r][c] = EMPTY;
        board[3][3] = AI;
        board[3][4] = PLAYER;
        board[4][3] = PLAYER;
        board[4][4] = AI;
    }

    // Conditionals
    public boolean isValidMove(int r, int c, char player) {
        if (board[r][c] != EMPTY)
            return false;
        for (int moveR = -1; moveR <= 1; moveR++)
            for (int moveC = -1; moveC <= 1; moveC++)
                if (moveR != 0 || moveC != 0)
                    if (canFlip(r, c, moveR, moveC, player))
                        return true;
        return false;
    }

    public boolean canFlip(int r, int c, int moveR, int moveC, char player) {
        char enemy = (player == PLAYER) ? AI : PLAYER;
        r += moveR;
        c += moveC;
        boolean foundEnemy = false;
        while (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE) {
            if (board[r][c] == enemy) {
                foundEnemy = true;
            } else if (board[r][c] == player) {
                return foundEnemy;
            } else {
                break;
            }
            r += moveR;
            c += moveC;
        }
        return false;
    }

    public boolean hasValidMove(char player) {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (isValidMove(r, c, player))
                    return true;
            }
        }
        return false;
    }

    public void setAIDepth(int depth) {
        this.aiDepth = depth;
    }

    public void setStatusBar(javax.swing.JLabel statusBar) {
        this.statusBar = statusBar;
        updateStatus();
    }

    public void setOnPass(Runnable onPass) {
        this.onPass = onPass;
    }

    public void setOnWin(Runnable onWin) {
        this.onWin = onWin;
    }
    

    private void pushUndo() {
        undoStack.push(copyBoard(board));
        redoStack.clear();
    }

    public void undoMove() {
        if (!undoStack.isEmpty()) {
            redoStack.push(copyBoard(board));
            board = undoStack.pop();
            redoStack.push(copyBoard(board));
            board = undoStack.pop();
            legalMoves = computeLegalMovesFor(PLAYER);
            playerTurn = true;
            repaint();
            updateStatus();
        }
    }

    public void redoMove() {
        if (!redoStack.isEmpty()) {
            undoStack.push(copyBoard(board));
            board = redoStack.pop();
            undoStack.push(copyBoard(board));
            board = redoStack.pop();
            legalMoves = computeLegalMovesFor(PLAYER);
            playerTurn = true;
            repaint();
            updateStatus();
        }
    }

    private char[][] copyBoard(char[][] src) {
        char[][] dst = new char[src.length][src[0].length];
        for (int i = 0; i < src.length; i++) {
            dst[i] = src[i].clone();
        }
        return dst;
    }

    private void updateStatus() {
        if (statusBar == null)
            return;

        int playerCount = 0, aiCount = 0;
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (board[r][c] == PLAYER)
                    playerCount++;
                else if (board[r][c] == AI)
                    aiCount++;
            }
        }

        String turnText = playerTurn ? "Player's turn" : "AI is thinking…";
        statusBar.setText("Player: " + playerCount + "   AI: " + aiCount + "   " + turnText);
    }

    List<Point> computeLegalMovesFor(char playerChar) {
        List<Point> moves = new ArrayList<>();
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (board[r][c] == EMPTY && isValidMove(r, c, playerChar)) {
                    moves.add(new Point(r, c));
                }
            }
        }
        return moves;
    }

    private void triggerAIMoveWithDelay() {
        playerTurn = false;
        updateStatus();

        new Timer(500, e -> {
            ((Timer) e.getSource()).stop();
            if (hasValidMove(AI)) {
                pushUndo();
                int[] best = AIMove(aiDepth);
                if (best != null) {
                    int r = best[0], c = best[1];
                    lastMove = new Point(r, c);
                    getMove(r, c, AI);
                }
                repaint();
            }

            if (!hasValidMove(PLAYER) && !hasValidMove(AI)) {
                if (onWin != null)
                    onWin.run();
            } else {
                playerTurn = true;
                legalMoves = computeLegalMovesFor(PLAYER);
                repaint();
            }

            updateStatus();
        }).start();
    }
}   
