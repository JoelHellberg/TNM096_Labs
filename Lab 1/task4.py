import random
import heapq
import time

initstate : list[int] = [8, 6, 7, 
             2, 5, 4,
             3, 0, 1]

goal : list[int] = [1, 2, 3,
        4, 5, 6,
        7, 8, 0]

class Puzzle:
    def __init__(self, state=[], gWeight=0, hWeight=0):
        self.state = state.copy()
        self.gWeight = gWeight
        self.fWeight = gWeight + hWeight
        self.parent = None
    
    # Define the < operator based on fWeight
    def __lt__(self, other):
      return self.fWeight < other.fWeight

def main():
    print("\nAlgorythm Starting...")
    startTime = time.time()

    puzzle = Puzzle(initstate, gWeight=0, hWeight=h2(initstate))  # Use h2 for better heuristic
    print("Initial Puzzle: " + str(puzzle.state))

    possibleMoves = []
    performedMoves = set()  # Use a set for faster lookup
    heapq.heappush(possibleMoves, puzzle)

    puzzleNumber = 0

    while possibleMoves:
        puzzle = heapq.heappop(possibleMoves)

        if tuple(puzzle.state) in performedMoves:  # Skip already visited states
            continue

        performedMoves.add(tuple(puzzle.state))  # Add state to visited
        puzzleNumber += 1

        if puzzle.state == goal:

            bestPath = []
            updateBestPath(bestPath, puzzle)

            print("\nAlgorythm Complete!")

            print("-----------------------------------")
            print("Best Path: ")
            for i, step in enumerate(bestPath[::-1]):
                print(f"Step {i}: {step.state}")
            print("-----------------------------------")

            print("Total branches traversed: " + str(puzzleNumber))
            print(f"Elapsed time: {(time.time() - startTime):.4f}\n")

            return

        calculateLegalMoves(possibleMoves, performedMoves, puzzle)

    print("No solution found.")

def calculateLegalMoves(possibleMovesList, performedMovesSet, oldPuzzle):
    emptySpaceIndex = oldPuzzle.state.index(0)
    state = oldPuzzle.state

    moves = [
        (-1, emptySpaceIndex % 3 != 0),  # Left
        (1, emptySpaceIndex % 3 != 2),   # Right
        (-3, emptySpaceIndex >= 3),      # Up
        (3, emptySpaceIndex < 6)         # Down
    ]

    for offset, condition in moves:
        if condition:
            newState = state[:]
            newState[emptySpaceIndex], newState[emptySpaceIndex + offset] = (
                newState[emptySpaceIndex + offset], newState[emptySpaceIndex]
            )

            gWeight = oldPuzzle.gWeight + 1
            hWeight = h1(newState)  # Use h2 for better heuristic
            newPuzzle = Puzzle(newState, gWeight, hWeight)
            newPuzzle.parent = oldPuzzle  # Track the parent puzzle

            if tuple(newPuzzle.state) not in performedMovesSet:  # Check using set
                heapq.heappush(possibleMovesList, newPuzzle)

def updateBestPath(bestPath : list, currentPuzzle : Puzzle):
    while True:
        bestPath.append(currentPuzzle)
        if currentPuzzle.parent == None:
            break
        else:
            currentPuzzle = currentPuzzle.parent

def h1(state):
    # Mängden felplacerade brickor är antalet brickor som inte är på sin rätta plats.
    return sum(1 for i in range(9) if state[i] != goal[i] and state[i] != 0) # 0 aldrig felplacerad

def h2(state):
    goal_positions = {value: (i % 3, i // 3) for i, value in enumerate(goal)}  # Precompute goal positions
    distance = 0
    for i, value in enumerate(state):
        if value != 0:  # Ignore 0
            current_x, current_y = i % 3, i // 3
            goal_x, goal_y = goal_positions[value]
            distance += abs(goal_x - current_x) + abs(goal_y - current_y)
    return distance

if __name__ == "__main__":  # Ensures the script is run directly
    main()