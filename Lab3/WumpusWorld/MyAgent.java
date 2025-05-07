import java.util.*;

public class MyAgent implements Agent
{
    private World w;
    List<String> visitedPositions = new ArrayList<>();
    Set<Clause> KB = new LinkedHashSet<>();
    
    public MyAgent(World world)
    {
        for (int i = 1; i <=4; i++) {
            for (int j = 1; j <=4; j++) {
                String position = "(" + i + ","  + j +")";
                Clause c = new Clause(Set.of(position, "pit", "wumpus"));
                KB.add(c);
            }
        }
        w = world;
    }

 
    public void shootWumpusIfPossible(String possibleWumpusPos) {
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();
        String currentPos = "(" + cX + "," + cY + ")";
    
        // Only proceed if this is a new position
        if (!Arrays.asList(visitedPositions).contains(currentPos)) {
            List<String> safePositions = new ArrayList<>(List.of("(1,1)"));
    
            // Resolve known facts
            KB = CNFResolver.runResolution(KB);
    
            for (Clause clause : KB) {
                if (clause.literals.size() == 1) {
                    safePositions.add(clause.literals.iterator().next());
                }
            }
    
            // Try to deduce Wumpus position
            Set<Clause> testKB = new LinkedHashSet<>(KB);
            Clause wumpusHint = new Clause(Set.of(possibleWumpusPos, "~wumpus"));
            testKB.add(wumpusHint);
            testKB = CNFResolver.runResolution(testKB);
    
            List<String> wumpusCandidates = new ArrayList<>();
            for (Clause clause : testKB) {
                if (clause.literals.size() == 1) {
                    wumpusCandidates.add(clause.literals.iterator().next());
                }
            }
    
            // Filter out known safe positions
            wumpusCandidates.removeAll(safePositions);
    
            if (!wumpusCandidates.isEmpty()) {
                String candidate = wumpusCandidates.get(0);
                System.out.println("Wumpus candidate: " + candidate);
    
                String[] parts = candidate.replaceAll("[()]", "").split(",");
                int targetX = Integer.parseInt(parts[0]);
                int targetY = Integer.parseInt(parts[1]);
    
                // Check adjacency
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
    }
    

// Ask your solver to do an action

    public void doAction()
    {
        //Location of the player
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();
        System.out.println("Current position: (" + cX + ", " + cY + ")");
        
        //Basic action:
        //Grab Gold if we can.
        if (w.hasGlitter(cX, cY))
        {
            w.doAction(World.A_GRAB);
            return;
        }
        
        //Basic action:
        //We are in a pit. Climb up.
        if (w.isInPit())
        {
            w.doAction(World.A_CLIMB);
            return;
        }

        
        
        //Test the environment
        if (w.hasBreeze(cX, cY) && w.hasStench(cX, cY))
        {
            System.out.println("I am in a Breeze and Stench");
            if(cX > 1) {
                String neighbourPos = "(" + (cX - 1) + "," + cY +  ")";
                shootWumpusIfPossible(neighbourPos);
                Clause c = new Clause(Set.of(neighbourPos, "pit", "wumpus"));
                KB.add(c);
            }
            if(cX < 4) {
                String neighbourPos = "(" + (cX + 1) + "," + cY +  ")";
                shootWumpusIfPossible(neighbourPos);
                Clause c = new Clause(Set.of(neighbourPos, "pit", "wumpus"));
                KB.add(c);
            }
            if(cY > 1) {
                String neighbourPos = "(" + (cX) + "," + (cY - 1) +  ")";
                shootWumpusIfPossible(neighbourPos);
                Clause c = new Clause(Set.of(neighbourPos, "pit", "wumpus"));
                KB.add(c);
            }
            if(cY < 4) {
                String neighbourPos = "(" + (cX) + "," + (cY + 1) +  ")";
                shootWumpusIfPossible(neighbourPos);
                Clause c = new Clause(Set.of(neighbourPos, "pit", "wumpus"));
                KB.add(c);
            }
        }
        
        if (w.hasBreeze(cX, cY))
        {
            if(cX > 1) {
                Clause c = new Clause(Set.of("(" + (cX - 1) + "," + cY +  ")", "pit"));
                KB.add(c);
            }
            if(cX < 4) {
                Clause c = new Clause(Set.of("(" + (cX + 1) + "," + cY +  ")", "pit"));
                KB.add(c);
            }
            if(cY > 1) {
                Clause c = new Clause(Set.of("(" + (cX) + "," + (cY - 1) +  ")", "pit"));
                KB.add(c);
            }
            if(cY < 4) {
                Clause c = new Clause(Set.of("(" + (cX) + "," + (cY + 1) +  ")", "pit"));
                KB.add(c);
            }

            System.out.println("I am in a Breeze");
        }
        if (!(w.hasBreeze(cX, cY)))
        {
            if(cX > 1) {
                Clause c = new Clause(Set.of("(" + (cX - 1) + "," + cY +  ")", "~pit"));
                KB.add(c);
            }
            if(cX < 4) {
                Clause c = new Clause(Set.of("(" + (cX + 1) + "," + cY +  ")", "~pit"));
                KB.add(c);
            }
            if(cY > 1) {
                Clause c = new Clause(Set.of("(" + (cX) + "," + (cY - 1) +  ")", "~pit"));
                KB.add(c);
            }
            if(cY < 4) {
                Clause c = new Clause(Set.of("(" + (cX) + "," + (cY + 1) +  ")", "~pit"));
                KB.add(c);
            }

            System.out.println("I am not in a Breeze");
        }
        if (w.hasStench(cX, cY))
        {
            if(cX > 1) {
                String neighbourPos = "(" + (cX - 1) + "," + cY +  ")";
                shootWumpusIfPossible(neighbourPos);
                Clause c = new Clause(Set.of(neighbourPos, "pit", "wumpus"));
                KB.add(c);
            }
            if(cX < 4) {
                String neighbourPos = "(" + (cX + 1) + "," + cY +  ")";
                shootWumpusIfPossible(neighbourPos);
                Clause c = new Clause(Set.of(neighbourPos, "pit", "wumpus"));
                KB.add(c);
            }
            if(cY > 1) {
                String neighbourPos = "(" + (cX) + "," + (cY - 1) +  ")";
                shootWumpusIfPossible(neighbourPos);
                Clause c = new Clause(Set.of(neighbourPos, "pit", "wumpus"));
                KB.add(c);
            }
            if(cY < 4) {
                String neighbourPos = "(" + (cX) + "," + (cY + 1) +  ")";
                shootWumpusIfPossible(neighbourPos);
                Clause c = new Clause(Set.of(neighbourPos, "pit", "wumpus"));
                KB.add(c);
            }
            System.out.println("I am in a Stench");
        }
        if (!(w.hasStench(cX, cY)))
        {
            if(cX > 1) {
                Clause c = new Clause(Set.of("(" + (cX - 1) + "," + cY +  ")", "~wumpus"));
                KB.add(c);
            }
            if(cX < 4) {
                Clause c = new Clause(Set.of("(" + (cX + 1) + "," + cY +  ")", "~wumpus"));
                KB.add(c);
            }
            if(cY > 1) {
                Clause c = new Clause(Set.of("(" + (cX) + "," + (cY - 1) +  ")", "~wumpus"));
                KB.add(c);
            }
            if(cY < 4) {
                Clause c = new Clause(Set.of("(" + (cX) + "," + (cY + 1) +  ")", "~wumpus"));
                KB.add(c);
            }
            System.out.println("I am not in a Stench");
        }
        if (w.hasPit(cX, cY))
        {
            System.out.println("I am in a Pit");
        }

        visitedPositions.add("(" + cX + "," + cY + ")");
        KB = CNFResolver.runResolution(KB);

         
        for (Clause c : KB) {
            System.out.println(c);
        }

        System.out.println("\nConclusion:");
        List<String> safePositions = new ArrayList<>();
        safePositions.add("(1,1)");
        for (Clause c : KB) {
            if (c.literals.size() == 1) {
                safePositions.add(c.literals.iterator().next());
                System.out.println(" - " + c.literals.iterator().next());
            }
        }

        List<String> possibleMoves = new ArrayList<>();

        if(cX > 1) {
            possibleMoves.add("(" + (cX - 1) + "," + cY +  ")");
        }
        if(cX < 4) {
            possibleMoves.add("(" + (cX + 1) + "," + cY +  ")");
        }
        if(cY > 1) {
            possibleMoves.add("(" + cX + "," + (cY - 1) +  ")");
        }
        if(cY < 4) {
            possibleMoves.add("(" + cX + "," + (cY + 1) +  ")");
        }

        List<String> safePossibleMoves = new ArrayList<>(safePositions);
        safePossibleMoves.retainAll(possibleMoves);
        String bestMove = "";

        List<String> unvisitedSafeMoves = new ArrayList<>(safePossibleMoves);
        unvisitedSafeMoves.removeAll(visitedPositions);

        for(String move : visitedPositions) {
            System.out.println("Visited pos: " + move);
        }

        for(String move : unvisitedSafeMoves) {
            System.out.println("unvisited safe move: " + move);
        }

        if(unvisitedSafeMoves.size() > 0) {
            bestMove = unvisitedSafeMoves.get(0);
        }
        else {
            bestMove = safePossibleMoves.get(0);
        }
        

        String[] parts = bestMove.replaceAll("[()]", "").split(",");

        // Parse integers
        int targetX = Integer.parseInt(parts[0]);
        int targetY = Integer.parseInt(parts[1]);

        // Decide movement direction
        if (targetX == cX && targetY == cY + 1) {
            System.out.println("Move UP");
            w.doAction(World.A_MOVE_UP);
        } else if (targetX == cX && targetY == cY - 1) {
            System.out.println("Move DOWN");
            w.doAction(World.A_MOVE_DOWN);
        } else if (targetX == cX - 1 && targetY == cY) {
            System.out.println("Move LEFT");
            w.doAction(World.A_MOVE_LEFT);
        } else if (targetX == cX + 1 && targetY == cY) {
            System.out.println("Move RIGHT");
            w.doAction(World.A_MOVE_RIGHT);
        } else {
            System.out.println("Target is not adjacent!");
}
        
        //Random move actions
        //w.doAction(World.A_MOVE_UP);

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
			
			
