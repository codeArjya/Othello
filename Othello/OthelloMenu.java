import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OthelloMenu extends JPanel {
    private JFrame frame;
    private JLabel statusBar;
    private Othello gamePanel;

    public OthelloMenu(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(800, 800));
        setLayout(null);

        // Prompt the user for AI depth (integer >= 2)
        Integer depth = null;
        while (depth == null) {
            String input = JOptionPane.showInputDialog(
                frame,
                "Enter AI search depth (integer ≥ 1):",
                "Choose Depth",
                JOptionPane.QUESTION_MESSAGE
            );
            if (input == null) {
                // User cancelled, exit application
                System.exit(0);
            }
            try {
                int d = Integer.parseInt(input.trim());
                if (d >= 1) {
                    depth = d;
                } else {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Please enter an integer greater than or equal to 1.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please enter a valid integer.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }

        // Create the status bar
        statusBar = new JLabel("Player: 2   AI: 2   Player's turn", SwingConstants.CENTER);
        statusBar.setFont(new Font("Arial", Font.BOLD, 16));
        statusBar.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // Create the game panel with chosen depth
        gamePanel = new Othello(depth);
        gamePanel.setStatusBar(statusBar);
        gamePanel.setOnPass(() -> JOptionPane.showMessageDialog(
            frame,
            "No moves available for ⚫. AI will move automatically.",
            "Pass",
            JOptionPane.INFORMATION_MESSAGE
        ));
        gamePanel.setOnWin(() -> {
            int pCount = 0, aCount = 0;
            for (int r = 0; r < gamePanel.GRID_SIZE; r++)
                for (int c = 0; c < gamePanel.GRID_SIZE; c++) {
                    if (gamePanel.board[r][c] == gamePanel.PLAYER) pCount++;
                    else if (gamePanel.board[r][c] == gamePanel.AI) aCount++;
                }
            String msg;
            if (aCount > pCount)
                msg = "AI (⚪) wins!  Score: ⚪ " + aCount + " – ⚫ " + pCount;
            else if (pCount > aCount)
                msg = "Player (⚫) wins!  Score: ⚫ " + pCount + " – ⚪ " + aCount;
            else
                msg = "Draw!  Score: ⚫ " + pCount + " – ⚪ " + aCount;
            JOptionPane.showMessageDialog(frame, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        });

        // Create in-game controls: Undo / Redo / Restart
        JPanel controls = new JPanel(new FlowLayout());
        JButton undoBtn = new JButton("Undo");
        JButton redoBtn = new JButton("Redo");
        JButton restartBtn = new JButton("Restart");

        undoBtn.addActionListener(e -> gamePanel.undoMove());
        redoBtn.addActionListener(e -> gamePanel.redoMove());
        restartBtn.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new OthelloMenu(frame));
            frame.revalidate();
            frame.repaint();
        });

        controls.add(undoBtn);
        controls.add(redoBtn);
        controls.add(restartBtn);

        // Lay out everything in the frame
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(controls, BorderLayout.NORTH);
        frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
        frame.getContentPane().add(statusBar, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // (Optional: draw a background image here)
    }
}
