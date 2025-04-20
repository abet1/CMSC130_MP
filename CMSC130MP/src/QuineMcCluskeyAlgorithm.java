import java.util.List;
import java.util.*;

public class QuineMcCluskeyAlgorithm {

    private List<Integer> mintermsDecimal;
    private String variablesLetter;
    private int numberOfVariables;
    private List<Minterm> mintermList;
    private List<List<List<Minterm>>> simplification;
    private List<Minterm> primeImplicants;
    private List<Minterm> essentialPrimeImplicants;
    private StringBuilder simplificationDisplay;
    private StringBuilder primeImplicantTableDisplay;
    private StringBuilder essentialPrimeImplicantsDisplay;

    //constructor for the algo
    public QuineMcCluskeyAlgorithm(List<Integer>mintermsDecimal, String variablesLetter) {
        this.mintermsDecimal = new ArrayList<>(mintermsDecimal);
        this.variablesLetter = variablesLetter;
        this.numberOfVariables = variablesLetter.length();
        this.mintermList = new ArrayList<>();
        this.simplification = new ArrayList<>();
        this.primeImplicants = new ArrayList<>();
        this.essentialPrimeImplicants = new ArrayList<>();
        this.simplificationDisplay = new StringBuilder();
        this.primeImplicantTableDisplay = new StringBuilder();
        this.essentialPrimeImplicantsDisplay = new StringBuilder();

        //convert decimal minterms to binary representation
        for (int m:mintermsDecimal) {
            mintermList.add(new Minterm(m,numberOfVariables));
        }
    }

    //execution of the algorithm
    public void solve() {
        //step1
        List<List<Minterm>> groups = groupByOnes();

        //step2
        PrimeImplicants(groups);

        //step3
        PrimeImplicantTable();

        //step4
        essentialPrimeImplicant();
    }

    //group minterms based on number of 1's
    private List<List<Minterm>> groupByOnes() {
        List<List<Minterm>> groups = new ArrayList<>();

        for (int i=0;i<=numberOfVariables;i++) {
            groups.add(new ArrayList<>());
        }

        for(Minterm m : mintermList) {
            int groupNumber = m.countNumberOfOnes();
            groups.get(groupNumber).add(m);
        }

        return groups;
    }

    //find prime implicants by matched pairs
    private void PrimeImplicants(List<List<Minterm>> groups) {

        List<List<Minterm>> currentGroups = groups;
        simplification.add(new ArrayList<>(currentGroups));

        while(true){
            List<List<Minterm>> newGroups = new ArrayList<>();
            boolean areTherePossibleCombinations = false;

            for(int i=0;i<currentGroups.size()-1;i++) {
                List<Minterm> currentGroup = currentGroups.get(i);
                List<Minterm> nextGroup = currentGroups.get(i+1);

                if (currentGroup.isEmpty() || nextGroup.isEmpty()) {
                    continue;
                }

                List<Minterm> newGroup = new ArrayList<>();
                Set<Minterm> combinedMinterms = new HashSet<>();

                for (Minterm minterm1 : currentGroup) {
                    for (Minterm minterm2 : nextGroup) {
                        Optional<Minterm> combined = minterm1.combineMinterms(minterm2);
                        if (combined.isPresent()) {

                            areTherePossibleCombinations = true;
                            Minterm newMinterm = combined.get();
                            combinedMinterms.add(minterm1);
                            combinedMinterms.add(minterm2);

                            //check if term is already in the new groupings
                            boolean alreadyExist = false;
                            for (Minterm minterm : newGroup) {

                                if(minterm.equals(newMinterm)) {

                                    alreadyExist = true;
                                    minterm.getSetOfMinterms().addAll(newMinterm.getSetOfMinterms());

                                    break;
                                }
                            }


                            if(!alreadyExist) {
                                newGroup.add(newMinterm);
                            }

                        }

                    }

                }

                //all uncombined minterms will be considered as primce implicant
                for(Minterm minterm : currentGroup) {

                    if (!combinedMinterms.contains(minterm)) {
                        primeImplicants.add(minterm);
                    }

                }

                //check the last group in the iteration

                if (i == currentGroups.size()-2) {
                    for (Minterm minterm : nextGroup) {
                        if (!combinedMinterms.contains(minterm)) {
                            primeImplicants.add(minterm);
                        }
                    }
                }

                newGroups.add(newGroup);


            }

            //end if no combinations are found
            if (!areTherePossibleCombinations) {
                break;
            }

            currentGroups = newGroups;
            simplification.add(new ArrayList<>(newGroups));

        }
    }


    //prime implicant table
    private void PrimeImplicantTable() {

        primeImplicantTableDisplay.append(String.format("%-20s | ", "Prime Implicant"));


        for(int minterm : mintermsDecimal){

            primeImplicantTableDisplay.append(String.format("%-4d", minterm));
        }

        primeImplicantTableDisplay.append("\n");

        primeImplicantTableDisplay.append(String.format("%-20s-|-", "-".repeat(20)));
        primeImplicantTableDisplay.append("-".repeat(mintermsDecimal.size() * 4)).append("\n");

        //fill rows
        for(Minterm primeImplicant : primeImplicants) {

            primeImplicantTableDisplay.append(String.format("%-20s | ", primeImplicant.mintermToExpression(variablesLetter)));

            for (int minterm : mintermsDecimal) {
                if (primeImplicant.doesItMatch(minterm)) {

                    primeImplicantTableDisplay.append(" X  ");

                }else {

                    primeImplicantTableDisplay.append("    ");

                }

            }

            primeImplicantTableDisplay.append("\n");
        }
    }


    //get essential prime implicants

    private void essentialPrimeImplicant() {

        Map<Integer, List<Minterm>> essentialPI = new HashMap<>();

        for(int minterm : mintermsDecimal) {
            essentialPI.put(minterm, new ArrayList<>());
        }


        for (Minterm primeImplicant : primeImplicants) {

            for (int minterm : mintermsDecimal) {

                if (primeImplicant.doesItMatch(minterm)) {

                    essentialPI.get(minterm).add(primeImplicant);

                }
            }
        }


        Set<Integer> mintermsCoveredByEPI = new HashSet<>();


        //find columns with one X only
        for (int minterm : mintermsDecimal) {

            List<Minterm> implicants = essentialPI.get(minterm);

            if (implicants.size()==1) {
                Minterm essentialImplicant = implicants.get(0);

                if (!essentialPrimeImplicants.contains(essentialImplicant)) {
                    essentialPrimeImplicants.add(essentialImplicant);

                    for (int coveredMinterm : mintermsDecimal) {
                        if(essentialImplicant.doesItMatch(coveredMinterm)) {
                            mintermsCoveredByEPI.add(coveredMinterm);
                        }

                    }
                }

            }
        }


        //visualization
        essentialPrimeImplicantsDisplay.append("Essential Prime Implicants:\n");
        if (essentialPrimeImplicants.isEmpty()) {
            essentialPrimeImplicantsDisplay.append("TRIPPI TROPPI! no essential prime implicants found\n");
        } else {
            for (Minterm epi : essentialPrimeImplicants) {
                essentialPrimeImplicantsDisplay.append("- ").append(epi.mintermToExpression(variablesLetter)).append("\n");
            }

        }


        if (mintermsCoveredByEPI.size() < mintermsDecimal.size()) {
            essentialPrimeImplicantsDisplay.append("\n LIRILI LARILA! not all minterms are covered by the essential prime implicants");

            //find minterms that are not covered by the essential prime implicants
            List<Integer> uncovered = new ArrayList<>();

            for (int m : mintermsDecimal) {

                if (!mintermsCoveredByEPI.contains(m)) {
                    uncovered.add(m);

                }
            }
            essentialPrimeImplicantsDisplay.append("Uncovered minterms: ").append(uncovered).append("\n");

            //output the prime implicants that cover the most uncovered minterms
            while (!uncovered.isEmpty()) {
                Minterm bestImplicant = null;
                int maxCoverage = 0;

                for (Minterm primeImplicant : primeImplicants) {
                    if (essentialPrimeImplicants.contains(primeImplicant)) {
                        continue;
                    }

                    int coverCount = 0;
                    for (int m : uncovered) {
                        if (primeImplicant.doesItMatch(m)) {
                            coverCount++;
                        }
                    }

                    if (coverCount > maxCoverage) {
                        maxCoverage = coverCount;
                        bestImplicant = primeImplicant;
                    }
                }

                if (bestImplicant != null && maxCoverage > 0) {
                    essentialPrimeImplicants.add(bestImplicant);
                    essentialPrimeImplicantsDisplay.append("Added additional prime implicant: ")
                            .append(bestImplicant.mintermToExpression(variablesLetter)).append("\n");


                    final Minterm finalBestImplicant = bestImplicant;
                    uncovered.removeIf(m -> finalBestImplicant.doesItMatch(m));

                } else {
                    break;
                }

            }

        }


        essentialPrimeImplicantsDisplay.append("\nFinal Prime Implicants:\n");
        for (Minterm primeImplicant : essentialPrimeImplicants) {
            essentialPrimeImplicantsDisplay.append("- ").append(primeImplicant.mintermToExpression(variablesLetter)).append("\n");
        }

    }


    //output the grouping of minterms
    public String displayGroupedMinterms(){

        List<List<Minterm>> mintermGroups =  simplification.get(0);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i< mintermGroups.size(); i++) {
            if (!mintermGroups.get(i).isEmpty()) {
                sb.append("Group ").append(i).append(" (").append(i).append(" ones):\n");


                for (Minterm minterm: mintermGroups.get(i)) {
                    sb.append("  ").append(minterm.getValue()).append(" = ");
                    sb.append(minterm.getBinaryRepresentation()).append("\n");
                }

                sb.append("\n");
            }
        }

        return sb.toString();
    }



    //display combining terms
    public String displayCombiningTerms(){

        int iteration = 0;
        StringBuilder sb = new StringBuilder();

        for (List<List<Minterm>> stepGroups : simplification){
            if (iteration>0){
                sb.append("Iteration ").append(iteration).append(":\n");

                boolean anyGroupsUsed = false;
                for (int i=0; i<stepGroups.size(); i++) {

                    List<Minterm> group = stepGroups.get(i);

                    if (!group.isEmpty()) {
                        anyGroupsUsed = true;
                        sb.append("  Group ").append(i).append(":\n");

                        for (Minterm minterm : group) {
                            sb.append("    ").append(minterm.getBinaryRepresentation())
                                    .append(" (from: ").append(minterm.getSetOfMinterms()).append(")\n");
                        }
                        sb.append("\n");
                    }

                }

                if (!anyGroupsUsed) {
                    sb.append("No more possible pairings\n");
                }
            }
            iteration++;
        }

        sb.append("Prime Implicants:\n");
        for (Minterm primeImplicant : primeImplicants) {
            sb.append("  ").append(primeImplicant.getBinaryRepresentation())
                    .append(" = ").append(primeImplicant.mintermToExpression(variablesLetter))
                    .append(" (covers: ").append(primeImplicant.getSetOfMinterms()).append(")\n");
        }

        return sb.toString();
    }


    //display prime implicant table
    public String displayPrimeImplicantsTable(){
        return primeImplicantTableDisplay.toString();
    }

    //display essential prime implicants
    public String displayEssentialPrimeImplicantsTable(){
        return essentialPrimeImplicantsDisplay.toString();
    }

    //pos expression
    public String getPOS(){

        if (essentialPrimeImplicants.isEmpty()) {
            return "CHIMPANZINI BANANINI! no essential prime implicants";
        }

        //maxterms
        int possibleMinterms = 1 << numberOfVariables;
        List<Integer> maxterms = new ArrayList<>();
        for (int i=0;i<possibleMinterms;i++){
            if (!mintermsDecimal.contains(i)){
                maxterms.add(i);
            }
        }

        if (maxterms.isEmpty()) {
            return "GIRAFFA CELESTE! no maxterms found";
        }

        //de morgans
        StringBuilder productOfSums = new StringBuilder("POS Expression: ");

        boolean first = true;
        for (Minterm essentialPrimeImplicant : essentialPrimeImplicants) {
            if (!first) {
                productOfSums.append(" * ");

            }else {
                first = false;
            }

            productOfSums.append("(");

            boolean firstVariable = true;
            String binaryRepresentation = essentialPrimeImplicant.getBinaryRepresentation();


            for (int i=0; i<binaryRepresentation.length(); i++){

                char character = binaryRepresentation.charAt(i);

                if (character != '-'){
                    if (!firstVariable){
                        productOfSums.append(" + ");

                    }else {
                        firstVariable = false;
                    }


                    //1 means prime, 0 means not prime
                    if(character =='1'){
                        productOfSums.append(variablesLetter.charAt(i)).append("'");
                    } else {
                        productOfSums.append(variablesLetter.charAt(i));
                    }
                }





            }


            productOfSums.append(")");
        }

        return productOfSums.toString();

    }

}
