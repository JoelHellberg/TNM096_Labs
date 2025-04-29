package Lab3.TaskA;

import java.util.*;

public class CNFResolver {

    public Set<Clause> resolveAll(Set<Clause> clauses) {
        Set<Clause> newClauses = new LinkedHashSet<>(clauses);
        boolean addedNew;
        List<Clause> clauseList = new ArrayList<>(newClauses);

        System.out.println("Initial Clauses:");
        int index = 1;
        Map<Clause, Integer> clauseNumbers = new HashMap<>();
        for (Clause clause : clauseList) {
            clauseNumbers.put(clause, index);
            System.out.println("Clause " + index + ": " + clause);
            index++;
        }

        System.out.println("\nResolution Steps:");
        do {
            addedNew = false;
            List<Clause> currentClauses = new ArrayList<>(newClauses);

            for (int i = 0; i < currentClauses.size(); i++) {
                for (int j = i + 1; j < currentClauses.size(); j++) {
                    Clause resolvent = currentClauses.get(i).resolveWith(currentClauses.get(j));
                    if (resolvent != null && !newClauses.contains(resolvent)) {
                        System.out.println("-----------------------------------");
                        System.out.println("Resolving Clause " + clauseNumbers.get(currentClauses.get(i)) + " " + currentClauses.get(i));
                        System.out.println("and Clause " + clauseNumbers.get(currentClauses.get(j)) + " " + currentClauses.get(j));
                        System.out.println("Generated new clause: " + resolvent);

                        newClauses.add(resolvent);
                        clauseNumbers.put(resolvent, index++);
                        addedNew = true;
                    }
                }
            }
        } while (addedNew);

        System.out.println("-----------------------------------");
        System.out.println("\nNo more new clauses can be generated.");
        System.out.println("\nFinal Set of Clauses:");
        for (Clause clause : newClauses) {
            System.out.println("Clause " + clauseNumbers.get(clause) + ": " + clause);
        }

        return newClauses;
    }

    public static void main(String[] args) {
        // Robbery puzzle
        Clause c1 = new Clause(Set.of("A", "B", "C"));
        Clause c2 = new Clause(Set.of("~C", "A"));
        Clause c3 = new Clause(Set.of("~B", "A", "C"));

        Set<Clause> clauses = new HashSet<>();
        clauses.add(c1);
        clauses.add(c2);
        clauses.add(c3);

        CNFResolver resolver = new CNFResolver();
        resolver.resolveAll(clauses);
    }
}
