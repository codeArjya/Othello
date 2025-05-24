import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Othello extends JPanel { 
    final int gridSize = 8;
    final int boxSize = 100;
    final int panelSize = boxSize * gridSize;
    int[][] board = new int[gridSize][gridSize]; // 0=empty, 1=black, 2=white
    int currentPlayer = 1;
    
    public Othello() {
        setPreferredSize(new Dimension(panelSize, panelSize));
        setUpBoard();
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int c = e.getX() / boxSize;
                int r = e.getY() / boxSize;
                if (isValidMove(r, c, currentPlayer)) {
                    getMove(r, c, currentPlayer);
                    currentPlayer = 3 - currentPlayer; 
                    repaint();
                }
            }
        });
    }

    // Drawing stuff

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0,144,103));
        g.fillRect(0, 0, panelSize, panelSize);
        // Grid
        g.setColor(Color.BLACK);
        for (int i = 0; i <= gridSize; i++) {
            g.drawLine(i * boxSize, 0, i * boxSize, panelSize);
            g.drawLine(0, i * boxSize, panelSize, i * boxSize);
        }
        // Draw pieces
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (board[r][c] != 0) {
                    drawPiece(g, r, c, board[r][c]);
                }
            }
        }
    }
    public void drawPiece(Graphics g, int r, int c, int player) {
        int x = c *boxSize + 10;
        int y = r * boxSize +10;
        int d = boxSize- 20;
        g.setColor(player == 1 ? Color.BLACK : Color.WHITE);
        g.fillOval(x, y, d, d);
        g.setColor(Color.BLACK);
        g.drawOval(x, y, d, d);
    }
    public void setUpBoard() {
        board[3][3] = 2;
        board[3][4] = 1;
        board[4][3] = 1;
        board[4][4] = 2;
    }


    // Move stuff

    public void getMove(int r, int c, int player) {
        board[r][c] = player;
        for (int moveR = -1; moveR <= 1; moveR++)
            for (int moveC = -1; moveC <= 1; moveC++)
                if (moveR != 0 || moveC != 0)
                    if (canFlip(r, c, moveR, moveC, player))
                        flip(r, c, moveR, moveC, player);
    }
    public void flip(int r, int c, int moveR, int moveC, int player) {
        int enemy = 3-player;
        r += moveR; c += moveC;
        while (r >= 0 && r < gridSize && c >= 0 && c < gridSize && board[r][c] == enemy) {
            board[r][c] = player;
            r += moveR; c += moveC;
        }
    }

    // Conditionals

    public boolean isValidMove(int r, int c, int player) {
        if (board[r][c] != 0) return false;
        for (int moveR = -1; moveR <= 1; moveR++) 
            for (int moveC = -1; moveC <= 1; moveC++) 
                if (moveR != 0 || moveC != 0)
                    if (canFlip(r, c, moveR, moveC, player)) return true;
        return false;
    }
    public boolean canFlip(int r, int c, int moveR, int moveC, int player) {
        int enemy = 3-player;
        r += moveR; c += moveC;
        boolean foundEnemy = false;
        while (r >= 0 && r < gridSize && c >= 0 && c < gridSize) {
            if (board[r][c] == enemy) {
                foundEnemy = true;
            } else if (board[r][c] == player) {
                return foundEnemy;
            } else {
                break;
            }
            r += moveR; c += moveC;
        }
        return false;
    }

//    public char evaluate() {
//        int playerCount = 0;
//        int enemyCount = 0;
//        for (int r = 0; r < gridSize; r++)
//            for (int c = 0; c < gridSize; c++) {
//                if (board[r][c] == 1)
//                    playerCount++;
//                if (board[r][c] == 2)
//                    enemyCount++;
//            }
//        if (!hasValidMove(1) && !hasValidMove(2) || playerCount+enemyCount==gridSize*gridSize) {
//            if (playerCount > enemyCount)
//                return 'W';
//
//        }
//        // sorry it's kinda inefficient
////        count++;
////        for (int i = 0; i < 3; i++) {// rows and columns
////            if (board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] != EMPTY)
////                return board[i][0];
////            if (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] != EMPTY)
////                return board[0][i];
////        }
////        if (board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] != EMPTY)// diagonals
////            return board[0][0];
////
////        if (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] != EMPTY)
////            return board[0][2];
//
//        for (int i = 0; i < 3; i++)// checking if there's any empty spots
//            for (int j = 0; j < 3; j++)
//                if (board[i][j] == EMPTY)
//                    return 'N';
//        return 'D';
//
//    }

    public boolean hasValidMove(int player) {
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (isValidMove(r, c, player)) return true;
            }
        }
        return false;
    }
    

}
