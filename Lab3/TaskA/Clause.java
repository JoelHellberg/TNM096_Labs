package Lab3.TaskA;

import java.util.*;

class Clause {
    Set<String> literals;

    public Clause(Set<String> literals) {
        this.literals = new HashSet<>(literals);
    }

    public Clause resolveWith(Clause other) {
        for (String literal : literals) {
            String negated = negate(literal);
            if (other.literals.contains(negated)) {
                Set<String> newLiterals = new HashSet<>(literals);
                newLiterals.addAll(other.literals);
                newLiterals.remove(literal);
                newLiterals.remove(negated);
                return new Clause(newLiterals);
            }
        }
        return null; // No resolvable literal found
    }

    private String negate(String literal) {
        if (literal.startsWith("~")) {
            return literal.substring(1);
        } else {
            return "~" + literal;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Clause clause = (Clause) obj;
        return literals.equals(clause.literals);
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
