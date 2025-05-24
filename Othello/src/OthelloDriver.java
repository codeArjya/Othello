import javax.swing.*;

public class OthelloDriver extends JFrame {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Othello");
        Othello game = new Othello();
        frame.add(game);
        frame.setSize(game.panelSize + 16, game.panelSize + 39);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
