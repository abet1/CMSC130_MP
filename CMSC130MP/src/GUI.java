import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

//gui implementation of Quine-McCluskey
public class GUI extends JFrame {
    //gui components
    private JTextField mintermInput;
    private JTextField variableInput;
    private JTextArea result;
    private JButton minimize;
    private JButton clear;

    public GUI() {
        setTitle("Quine-McCluskey Boolean Function Minimizer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        setUpUI();

        setLocationRelativeTo(null);
    }

    private void setUpUI() {
        //input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Minterms (pls separate by comma):"));
        mintermInput = new JTextField();
        inputPanel.add(mintermInput);

        inputPanel.add(new JLabel("Variables (6 is the max, one letter per variable):"));
        variableInput = new JTextField();
        inputPanel.add(variableInput);

        minimize = new JButton("Minimize");
        clear = new JButton("Clear");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(minimize);
        buttonPanel.add(clear);
        inputPanel.add(new JLabel(""));
        inputPanel.add(buttonPanel);

        add(inputPanel, BorderLayout.NORTH);

        //window for result
        result = new JTextArea();
        result.setEditable(false);
        result.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(result);
        add(scrollPane, BorderLayout.CENTER);

        //action listeners
        minimize.addActionListener(e -> minimizeFunction());
        clear.addActionListener(e -> {
            mintermInput.setText("");
            variableInput.setText("");
            result.setText("");
        });
    }

    private void minimizeFunction() {
        result.setText("");

        try {
            //process user input
            String minTermsInput = mintermInput.getText().trim();
            String variablesInput = variableInput.getText().trim().toUpperCase();

            if (minTermsInput.isEmpty() || variablesInput.isEmpty()) {
                showError("TRALALERO TRALALA! minterms and variables cannot be empty.");
                return;
            }

            //check if variable exceeds limit
            if (variablesInput.length() > QuineMcCluskey.MAX_NO_VARIABLES) {
                showError("TUNG TUNG TUNG SAHUR! variables exceeded the max length");
                return;
            }

            //process minterms
            List<Integer> minterms = Arrays.stream(minTermsInput.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            //check if minterms input is valid
            int numVars = variablesInput.length();
            int maxValue = (1 << numVars) - 1;
            for (int minterm : minterms) {
                if (minterm < 0 || minterm > maxValue) {
                    showError("BOMBARDIRO CROCODILLO! minterms provided is outside of range");
                    return;
                }
            }

            // Validate variables
            Set<Character> uniqueVars = new HashSet<>();
            for (char c : variablesInput.toCharArray()) {
                if (!Character.isLetter(c)) {
                    showError("BOMBOMBINI GUSINI! variables must be letters only");
                    return;
                }
                if (!uniqueVars.add(c)) {
                    showError("BRR BRR PATAPIM! duplicate variables detected");
                    return;
                }
            }

            //Quine-McCluskey algorithm
            QuineMcCluskeyAlgorithm sirRuah = new QuineMcCluskeyAlgorithm(minterms, variablesInput);
            sirRuah.solve();

            //results step by step
            appendText("--- QUINE-MCCLUSKEY MINIMIZATION STEPS ---\n\n");
            appendText("Variables: " + variablesInput + "\n");
            appendText("Minterms: " + minterms + "\n\n");

            //Grouping minterms based on the number of ones.
            appendText("Grouping minterms based on the number of ones.\n");
            appendText(sirRuah.displayGroupedMinterms());

            //Pairwise simplification and formation of prime implicants.
            appendText("\nPairwise simplification and formation of prime implicants.\n");
            appendText(sirRuah.displayCombiningTerms());

            //Prime implicant table creation.
            appendText("\nPrime implicant table creation\n");
            appendText(sirRuah.displayPrimeImplicantsTable());

            //Selection of essential prime implicants.
            appendText("\nSelection of essential prime implicants\n");
            appendText(sirRuah.displayEssentialPrimeImplicantsTable());

            //Final minimized expressions (POS).
            appendText("\nMINIMIZED EXPRESSION (POS)\n");
            appendText(sirRuah.getPOS());

        } catch (NumberFormatException e) {
            showError("Invalid minterm format. Please enter numeric values separated by commas.");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void appendText(String text) {
        result.append(text);
    }
}