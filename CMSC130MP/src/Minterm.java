import java.util.*;



//class for representation of a minterm in Quine-McCluskey
public class Minterm {

    private int value;
    private String binaryRepresentation;
    private Set<Integer> setOfMinterms;

    //constructor for a single minterm
    //value = decimal value of the minterm
    //numberOfVariables =  number of variables in the boolean function
    public Minterm(int value, int numberOfVariables) {
        this.value = value;
        this.binaryRepresentation = toBinaryString(value, numberOfVariables);
        this.setOfMinterms = new HashSet<>();
        this.setOfMinterms.add(value);

    }

    //constructor for combined minterms
    //binaryRepresentation = binary representation
    //setOfMinterms = set of original minterms
    public Minterm(String binaryRepresentation, Set<Integer> setOfMinterms){
        this.binaryRepresentation = binaryRepresentation;
        this.value = -1;
        this.setOfMinterms = new HashSet<>(setOfMinterms);
    }

    public int getValue() {
        return value;
    }

    public String getBinaryRepresentation() {
        return binaryRepresentation;
    }

    public Set<Integer> getSetOfMinterms() {
        return setOfMinterms;
    }

    //count the number of ones in the binary representation
    public int countNumberOfOnes(){
        int count = 0;

        for (char c : binaryRepresentation.toCharArray()) {
            if (c == '1') {
                count++;
            }
        }

        return count;
    }


    //converts decimal value to binary representation
    private String toBinaryString(int value, int numberOfVariables) {
        String binaryRepresentation = Integer.toBinaryString(value);

        while(binaryRepresentation.length() < numberOfVariables) {
            binaryRepresentation = "0" + binaryRepresentation;
        }

        return binaryRepresentation;
    }

    //combine minterms if they differ by only one variable
    //will return empty if it's not possible to combine the minterms
    public Optional<Minterm> combineMinterms(Minterm otherMinterm){
        int differences = 0;
        int differencePosition = -1;

        for(int i=0; i<binaryRepresentation.length(); i++){
            if(binaryRepresentation.charAt(i) != otherMinterm.getBinaryRepresentation().charAt(i)){
                differences++;
                differencePosition = i;
            }
        }


        if(differences == 1){
            //will create a new binary representation with a dash at the place of difference
            StringBuilder newBinaryRepresentation = new StringBuilder(binaryRepresentation);
            newBinaryRepresentation.setCharAt(differencePosition, '-');

            Set<Integer> newSetOfMinterms = new HashSet<>(setOfMinterms);
            newSetOfMinterms.addAll(otherMinterm.setOfMinterms);

            return Optional.of(new Minterm(newBinaryRepresentation.toString(), newSetOfMinterms));
        }

        return Optional.empty();
    }

    //check if a minterm matches a specific minterm value
    public boolean doesItMatch(int mintermValue){
        return setOfMinterms.contains(mintermValue);
    }

    //convert minterm into an expression
    public String mintermToExpression(String variables){
        StringBuilder expression = new StringBuilder();


        for (int i = 0; i < binaryRepresentation.length(); i++) {

            char bit = binaryRepresentation.charAt(i);

            if (bit != '-') {
                if (expression.length() > 0) {
                    expression.append("");
                }

                char variable = variables.charAt(i);
                if (bit == '0') {
                    expression.append(variable).append("'");

                } else {
                    expression.append(variable);
                }
            }

        }

        return expression.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Minterm minterm = (Minterm) o;
        return binaryRepresentation.equals(minterm.binaryRepresentation);
    }


    @Override
    public int hashCode() {
        return Objects.hash(binaryRepresentation);
    }

    @Override
    public String toString() {
        return binaryRepresentation;
    }
}
