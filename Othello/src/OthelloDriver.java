import javax.swing.*;

public class OthelloDriver extends JFrame {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Othello");
        int depth = 4;
        Othello game = new Othello(depth);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new OthelloMenu(frame));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
