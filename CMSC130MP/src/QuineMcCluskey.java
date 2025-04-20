import javax.swing.*;


//main class for the Quine-McCluskey Minimization of Boolean Functions
public class QuineMcCluskey {
    public static final int MAX_NO_VARIABLES = 6;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUI().setVisible(true);
        });
    }
}