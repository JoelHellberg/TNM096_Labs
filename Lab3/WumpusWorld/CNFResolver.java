import java.util.*;

class Clause {
    Set<String> literals;

    public Clause(Set<String> literals) {
        this.literals = new HashSet<>(literals);
    }

    public Clause resolveWith(Clause other) {
        for (String lit : literals) {
            String negated = negate(lit);
            if (other.literals.contains(negated)) {
                Set<String> newLits = new HashSet<>(literals);
                newLits.addAll(other.literals);
                newLits.remove(lit);
                newLits.remove(negated);

                // Tautology check
                for (String l : newLits) {
                    if (newLits.contains(negate(l))) {
                        return null;
                    }
                }
                return new Clause(newLits);
            }
        }
        return null;
    }

    public String negate(String literal) {
        return literal.startsWith("~") ? literal.substring(1) : "~" + literal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clause)) return false;
        Clause clause = (Clause) o;
        return Objects.equals(literals, clause.literals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literals);
    }

    @Override
    public String toString() {
        return literals.toString();
    }
}

public class CNFResolver {

    public static Set<Clause> runResolution(Set<Clause> KB) {
        boolean changed;
        Set<Clause> result = new LinkedHashSet<>(KB);

        do {
            changed = false;
            Set<Clause> newClauses = new LinkedHashSet<>();
            List<Clause> list = new ArrayList<>(result);

            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    Clause resolvent = list.get(i).resolveWith(list.get(j));
                    if (resolvent != null && !result.contains(resolvent) && !newClauses.contains(resolvent)) {
                        newClauses.add(resolvent);
                        changed = true;
                    }
                }
            }
            result.addAll(newClauses);
        } while (changed);

        return result;
    }

    public static void main(String[] args) {
        // Initial KB from the image
        Clause c1 = new Clause(Set.of("~sun", "~money", "ice"));
        Clause c2 = new Clause(Set.of("~money", "ice", "movie"));
        Clause c3 = new Clause(Set.of("~movie", "money"));
        Clause c4 = new Clause(Set.of("~movie", "~ice"));
        Clause c5 = new Clause(Set.of("movie"));
        Clause c6 = new Clause(Set.of("sun", "money", "cry"));

        // Clause c1 = new Clause(Set.of("A", "B", "C"));
        // Clause c2 = new Clause(Set.of("~C", "A"));
        // Clause c3 = new Clause(Set.of("~B", "A", "C"));

        Set<Clause> KB = new LinkedHashSet<>();
        KB.add(c1);
        KB.add(c2);
        KB.add(c3);
        KB.add(c4);
        KB.add(c5);
        KB.add(c6);

        // Clause c7 = new Clause(Set.of("(1,1)", "wumpus", "pit"));

        Set<Clause> resolvedKB = runResolution(KB);

        System.out.println("Final KB:");
        for (Clause c : resolvedKB) {
            System.out.println(c);
        }

        System.out.println("\nConclusion:");
        for (Clause c : resolvedKB) {
            if (c.literals.size() == 1) {
                System.out.println(" - " + c.literals.iterator().next());
            }
        }
    }
}