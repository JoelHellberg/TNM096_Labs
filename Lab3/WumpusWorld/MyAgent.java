import java.util.*;

public class MyAgent implements Agent {
    private World w;
    Random rand = new Random();
    List<String> visitedPositions = new ArrayList<>();
    Set<Clause> KB = new LinkedHashSet<>();

    public MyAgent(World world) {
        w = world;
        KB.add(new Clause(Set.of("(1,1)")));
    }

    private List<String> getAdjacentPositions(int x, int y) {
        List<String> positions = new ArrayList<>();
        if (x > 1) positions.add("(" + (x - 1) + "," + y + ")");
        if (x < 4) positions.add("(" + (x + 1) + "," + y + ")");
        if (y > 1) positions.add("(" + x + "," + (y - 1) + ")");
        if (y < 4) positions.add("(" + x + "," + (y + 1) + ")");
        return positions;
    }
    
    private void handlePercept(String pos, String fact, boolean negate) {
        if (negate) fact = "~" + fact;
        KB.add(new Clause(Set.of(pos, fact)));
    }

    private List<String> findSafePositions() {
        KB = CNFResolver.runResolution(KB);
        List<String> safePositions = new ArrayList<>();
        for (Clause clause : KB) {
            if (clause.literals.size() == 1) {
                safePositions.add(clause.literals.iterator().next());
            }
        }
        return safePositions;
    }
    
    public void doAction() {
        int cX = w.getPlayerX(), cY = w.getPlayerY();
        String currentPos = "(" + cX + "," + cY + ")";
        System.out.println("Current position: " + currentPos);
    
        if (w.hasGlitter(cX, cY)) { w.doAction(World.A_GRAB); return; }
        if (w.isInPit()) { w.doAction(World.A_CLIMB); return; }
    
        boolean breeze = w.hasBreeze(cX, cY);
        boolean stench = w.hasStench(cX, cY);
        List<String> neighbors = getAdjacentPositions(cX, cY);
    
        if (breeze && stench) {
            System.out.println("I am in a Breeze and Stench");
            for (String pos : neighbors) {
                shootWumpusIfPossible(pos, currentPos);
                KB.add(new Clause(Set.of(pos, "pit", "wumpus")));
            }
        } else if (!breeze && !stench) {
            System.out.println("I am in a clear area");
            for (String pos : neighbors) {
                KB.add(new Clause(Set.of(pos)));
            }
        } else {
            if (breeze) {
                System.out.println("I am in a Breeze");
                for (String pos : neighbors) handlePercept(pos, "pit", false);
            } else {
                System.out.println("I am not in a Breeze");
                for (String pos : neighbors) handlePercept(pos, "pit", true);
            }
    
            if (stench) {
                System.out.println("I am in a Stench");
                for (String pos : neighbors) {
                    shootWumpusIfPossible(pos, currentPos);
                    handlePercept(pos, "wumpus", false);
                }
            } else {
                System.out.println("I am not in a Stench");
                for (String pos : neighbors) handlePercept(pos, "wumpus", true);
            }
        }
    
        visitedPositions.add(currentPos);
        KB = CNFResolver.runResolution(KB);
    
        List<String> safePositions = findSafePositions();

        List<String> adjacentSafeMoves = new ArrayList<>(getAdjacentPositions(cX, cY));
        adjacentSafeMoves.retainAll(safePositions);

        List<String> unvisitedAdjacentSafeMoves = new ArrayList<>(adjacentSafeMoves);
        unvisitedAdjacentSafeMoves.removeAll(visitedPositions);
    
        String bestMove = unvisitedAdjacentSafeMoves.isEmpty() 
        ? adjacentSafeMoves.get(rand.nextInt(adjacentSafeMoves.size())) 
        : unvisitedAdjacentSafeMoves.get(0);

        String[] parts = bestMove.replaceAll("[()]", "").split(",");
        int targetX = Integer.parseInt(parts[0]), targetY = Integer.parseInt(parts[1]);
    
        if (targetX == cX && targetY == cY + 1) w.doAction(World.A_MOVE_UP);
        else if (targetX == cX && targetY == cY - 1) w.doAction(World.A_MOVE_DOWN);
        else if (targetX == cX - 1 && targetY == cY) w.doAction(World.A_MOVE_LEFT);
        else if (targetX == cX + 1 && targetY == cY) w.doAction(World.A_MOVE_RIGHT);
        else System.out.println("Target is not adjacent!");
    }

    public void shootWumpusIfPossible(String possibleWumpusPos, String currentPos) {
        if (visitedPositions.contains(currentPos)) return;
    
        List<String> safePositions = findSafePositions();
        List<String> wumpusCandidates = findWumpusCandidates(possibleWumpusPos, safePositions);
    
        if (!wumpusCandidates.isEmpty()) {
            handleWumpusShot(wumpusCandidates.get(0));
        }
    }
    
    private List<String> findWumpusCandidates(String possibleWumpusPos, List<String> safePositions) {
        Set<Clause> testKB = new LinkedHashSet<>(KB);
        testKB.add(new Clause(Set.of(possibleWumpusPos, "~wumpus")));
        testKB = CNFResolver.runResolution(testKB);
    
        List<String> candidates = new ArrayList<>();
        for (Clause clause : testKB) {
            if (clause.literals.size() == 1) {
                candidates.add(clause.literals.iterator().next());
            }
        }
        candidates.removeAll(safePositions);
        return candidates;
    }
    
    private void handleWumpusShot(String candidate) {
        System.out.println("Wumpus candidate: " + candidate);
    
        String[] parts = candidate.replaceAll("[()]", "").split(",");
        int targetX = Integer.parseInt(parts[0]);
        int targetY = Integer.parseInt(parts[1]);
    
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();
    
        if (targetX == cX && targetY == cY + 1) {
            System.out.println("SHOOT UP");
            w.doAction(World.A_SHOOT_UP);
        } else if (targetX == cX && targetY == cY - 1) {
            System.out.println("SHOOT DOWN");
            w.doAction(World.A_SHOOT_DOWN);
        } else if (targetX == cX - 1 && targetY == cY) {
            System.out.println("SHOOT LEFT");
            w.doAction(World.A_SHOOT_LEFT);
        } else if (targetX == cX + 1 && targetY == cY) {
            System.out.println("SHOOT RIGHT");
            w.doAction(World.A_SHOOT_RIGHT);
        } else {
            System.out.println("Shooting target is not adjacent!");
        }
    
        if (!w.wumpusAlive()) {
            KB.add(new Clause(Set.of("~wumpus")));
        }
    }
    
}

// ACTIONS AVAILABLE
// --------------------------------
// w.doAction(World.A_MOVE_RIGHT);
// w.doAction(World.A_MOVE_LEFT);
// w.doAction(World.A_MOVE_UP);
// w.doAction(World.A_MOVE_DOWN);

// w.doAction(World.A_SHOOT_UP);
// w.doAction(World.A_SHOOT_DOWN);
// w.doAction(World.A_SHOOT_LEFT);
// w.doAction(World.A_SHOOT_RIGHT);

// w.doAction(World.A_GRAB);
// w.doAction(World.A_CLIMB)

// SENSING ACTIONS (return true/false)
// ------------------------------------
// w.hasGlitter(cX,cY)
// w.hasBreeze(cX,cY)
// w.hasStench(cX, cY)
// w.hasPit(cX, cY)
// w.hasWumpus(cX, cY)
// w.isInPit()
// w.wumpusAlive()
// w.hasArrow()
