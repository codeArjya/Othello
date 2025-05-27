import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Othello extends JPanel {

    final char PLAYER = '⚫';
    final char AI = '⚪';
    final char EMPTY = ' ';
    final int gridSize = 8;
    final int boxSize = 100;
    final int panelSize = boxSize * gridSize;
    char[][] board = new char[gridSize][gridSize];

    // the bad spaces result in positions that allow opponent to take without being
    // able to take back ourselves
    // forces bad moves and forces less resulting material
    // THE POSITION WEIGHTS ONLY WORK FOR 8X8 BOARDS, BUT CAN EASILY BE CHANGED TO
    // ARRAY LIST FOR OTHER SIZES
    // TODO needs functionality though

    /*
     * INITIAL PIECES HAVE NO WEIGHT AS THEY ARE ALREADY PLACED
     * CORNERS ARE THE BEST POSITIONS TO GET: 100
     * SPACES ADJACENT TO CORNERS ARE BAD TO OBTAIN (DIAGONAL SPACES ARE WORSE): -50
     * & -20
     * SPACES ADJACENT TO SIDES ARE BAD AS WELL: -2
     * SPACES FURTHER FROM CENTER ARE BETTER (AS LONG AS THEY AREN'T ADJACENT TO THE
     * SIDES OR DIAGONAL TO CORNER): 2 & 1
     */
    static final int[][] POSITION_WEIGHTS = {
            { 100, -20, 10, 5, 5, 10, -20, 100 },
            { -20, -50, -2, -2, -2, -2, -50, -20 },
            { 10, -2, 2, 1, 1, 2, -2, 10 },
            { 5, -2, 1, 0, 0, 1, -2, 5 },
            { 5, -2, 1, 0, 0, 1, -2, 5 },
            { 10, -2, 2, 1, 1, 2, -2, 10 },
            { -20, -50, -2, -2, -2, -2, -50, -20 },
            { 100, -20, 10, 5, 5, 10, -20, 100 }
    };

    public Othello() {
        setPreferredSize(new Dimension(panelSize, panelSize));
        setUpBoard();
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int c = e.getX() / boxSize;
                int r = e.getY() / boxSize;
                if (isValidMove(r, c, PLAYER)) {
                    getMove(r, c, PLAYER);
                    repaint();
                    if (hasValidMove(AI)) {
                        // Change depth HERE
                        int[] aiMove = AIMove(9);
                        if (aiMove != null) {
                            getMove(aiMove[0], aiMove[1], AI);
                            repaint();
                        }
                    }
                }
            }
        });
    }

    // Check game state evaluate
    public char evaluate() {
        int playerCount = 0;
        int AICount = 0;
        for (int r = 0; r < gridSize; r++) // checking if there's any empty spots
            for (int c = 0; c < gridSize; c++) {
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
        int material = 0, position = 0;
        for (int r = 0; r < gridSize; r++)// checking if there's any empty spots
            for (int c = 0; c < gridSize; c++) {
                if (board[r][c] == PLAYER) {
                    material--;
                    position -= POSITION_WEIGHTS[r][c];
                } else if (board[r][c] == AI) {
                    material++;
                    position += POSITION_WEIGHTS[r][c];
                }
            }
        int mobility = countLegalMoves(AI) - countLegalMoves(PLAYER);
        return (10 * material) + position + (5 * mobility);
    }

    public int countLegalMoves(char player) {
        int count = 0;
        for (int r = 0; r < gridSize; r++)
            for (int c = 0; c < gridSize; c++)
                if (isValidMove(r, c, player))
                    count++;
        return count;
    }

    // Minimax
    public int minimax(boolean isMax, int alpha, int beta, int depth) {
        if (depth == 0)
            return evalFunction();
        char eval = evaluate();
        switch (eval) {
            case PLAYER:
                return -100;
            case AI:
                return 100;
            case 'D':
                return evalFunction();
        }
        int best = isMax ? -1000 : 1000;
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (isValidMove(r, c, isMax ? AI : PLAYER)) {
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

    public int[] AIMove(int depth) {
        int[] output = { 9, 9 };
        int best = -1000;
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (isValidMove(r, c, AI)) {
                    char[][] carboncopy = new char[board.length][];
                    for (int i = 0; i < board.length; i++) {
                        carboncopy[i] = board[i].clone();
                    }
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
        char ai = (player == PLAYER) ? AI : PLAYER;
        r += moveR;
        c += moveC;
        while (r >= 0 && r < gridSize && c >= 0 && c < gridSize && board[r][c] == ai) {
            board[r][c] = player;
            r += moveR;
            c += moveC;
        }
    }

    // Drawing stuff
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 144, 103));
        g.fillRect(0, 0, panelSize, panelSize);
        g.setColor(Color.BLACK);
        for (int i = 0; i <= gridSize; i++) {
            g.drawLine(i * boxSize, 0, i * boxSize, panelSize);
            g.drawLine(0, i * boxSize, panelSize, i * boxSize);
        }
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (board[r][c] != EMPTY) {
                    drawPiece(g, r, c, board[r][c]);
                }
            }
        }
    }

    public void drawPiece(Graphics g, int r, int c, char player) {
        int x = c * boxSize + 10;
        int y = r * boxSize + 10;
        int d = boxSize - 20;
        g.setColor(player == PLAYER ? Color.BLACK : Color.WHITE);
        g.fillOval(x, y, d, d);
        g.setColor(Color.BLACK);
        g.drawOval(x, y, d, d);
    }

    public void setUpBoard() {
        for (int r = 0; r < gridSize; r++)
            for (int c = 0; c < gridSize; c++)
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
        char ai = (player == PLAYER) ? AI : PLAYER;
        r += moveR;
        c += moveC;
        boolean foundEnemy = false;
        while (r >= 0 && r < gridSize && c >= 0 && c < gridSize) {
            if (board[r][c] == ai) {
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
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (isValidMove(r, c, player))
                    return true;
            }
        }
        return false;
    }

}
