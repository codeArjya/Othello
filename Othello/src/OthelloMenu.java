import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OthelloMenu extends JPanel {
    private JFrame frame;
    private JButton startButton;
    private JTextField depthField;
    private Image background;

    public OthelloMenu(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(800, 800));
        setLayout(null);
        background = new ImageIcon("reversi2.jpg").getImage();
        JLabel title = new JLabel("Othello", SwingConstants.CENTER);
        title.setFont(new Font("TimesNewRoman", Font.BOLD, 50));
        title.setBounds(200, 100, 400, 60);
        add(title);
        // Depth input label
        JLabel depthLabel = new JLabel("Enter AI Search Depth (≥ 1):", SwingConstants.CENTER);
        depthLabel.setFont(new Font("TimesNewRoman", Font.PLAIN, 20));
        depthLabel.setBounds(250, 220, 300, 30);
        add(depthLabel);
        // Depth input field
        depthField = new JTextField("4");
        depthField.setFont(new Font("TimesNewRoman", Font.PLAIN, 20));
        depthField.setBounds(300, 260, 200, 40);
        add(depthField);
        // Start button
        startButton = new JButton("Start Game");
        startButton.setFont(new Font("TimesNewRoman", Font.BOLD, 24));
        startButton.setBounds(300, 340, 200, 50);
        startButton.addActionListener(e -> {
            String input = depthField.getText().trim();
            try {
                int depth = Integer.parseInt(input);
                if (depth < 1) throw new NumberFormatException();
                startGame(depth);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter a valid integer greater than or equal to 1.",
                        "Invalid Depth",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        add(startButton);
    }

    private void startGame(int depth) {
        JLabel statusBar = new JLabel("Player: 2   AI: 2   Player's turn", SwingConstants.CENTER);
        statusBar.setFont(new Font("Arial", Font.BOLD, 16));
        statusBar.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        Othello gamePanel = new Othello(depth);
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
        frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(controls, BorderLayout.NORTH);
        frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
        frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
