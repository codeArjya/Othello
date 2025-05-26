import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OthelloMenu extends JPanel {
    int depth = 4;
    JFrame frame;
    JComboBox<Integer> depthDropdown;
    JButton startButton;
    Image background;

    public OthelloMenu(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(800, 800));
        setLayout(null);
        background = new ImageIcon("reversi2.jpg").getImage();
        // Title label
        JLabel title = new JLabel("Othello", SwingConstants.CENTER);
        title.setFont(new Font("TimesNewRoman", Font.BOLD, 50));
        title.setBounds(200, 100, 400, 50);
        add(title);

        // Dropdown
        Integer[] depths = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        depthDropdown = new JComboBox<>(depths);
        depthDropdown.setSelectedItem(depth);
        depthDropdown.setBounds(300, 250, 200, 40);
        depthDropdown.setFont(new Font("TimesNewRoman", Font.PLAIN, 20));
        depthDropdown.addActionListener(e -> depth = (int) depthDropdown.getSelectedItem());
        add(depthDropdown);

        // Label for dropdown
        JLabel dropdownLabel = new JLabel("Select AI Depth:", SwingConstants.CENTER);
        dropdownLabel.setBounds(300, 210, 200, 30);
        dropdownLabel.setFont(new Font("TimesNewRoman", Font.PLAIN, 18));
        add(dropdownLabel);

        // Start button
        startButton = new JButton("Start Game");
        startButton.setBounds(300, 350, 200, 50);
        startButton.setFont(new Font("TimesNewRoman", Font.BOLD, 24));
        startButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new Othello(depth));
            frame.revalidate();
            frame.repaint();
        });
        add(startButton);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }


}
