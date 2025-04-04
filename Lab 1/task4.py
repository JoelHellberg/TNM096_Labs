import random
import heapq

initstate = [1, 0, 2, 
             4, 5, 3,
             7, 8, 6]

goal = [1, 2, 3,
        4, 5, 6,
        7, 8, 0]

class Puzzle:
    def __init__(self, state=[], gWeight=0, hWeight=0):
        self.state = state.copy()
        self.gWeight = gWeight
        self.fWeight = gWeight + hWeight
        # Define the < operator based on fWeight
    def __lt__(self, other):
      return self.fWeight < other.fWeight

def main():
    print("Algorythm Starting")

    # Randomize the order of the list
    random.shuffle(initstate)
    while not is_solvable(initstate):
        random.shuffle(initstate)

    puzzle = Puzzle(initstate, gWeight=0, hWeight=0)
    print("Initial Puzzle: " + str(puzzle.state))

    possibleMoves = []
    performedMoves = []
    puzzleNumber = 0

    while puzzle.state != goal:
        # Add all new possible moves to the list based on the current puzzle
        calculateLegalMoves(possibleMoves, performedMoves, puzzle)

        # Pop the puzzle with the lowest fWeight
        puzzle = heapq.heappop(possibleMoves)
        performedMoves.append(puzzle)

        puzzleNumber += 1
        print("New puzzle Calculated, current puzzle: " + str(puzzle.state) + ", Puzzle number: " + str(puzzleNumber))
    
    print("Algorythm Complete!")
    print("Result: " + str(puzzle.state) + ", Puzzles processed: " + str(puzzleNumber))

def is_solvable(state):
    inv_count = sum(
        1 for i in range(len(state)) for j in range(i+1, len(state))
        if state[i] != 0 and state[j] != 0 and state[i] > state[j]
    )
    return inv_count % 2 == 0

def calculateLegalMoves(possibleMovesList, performedMovesList, oldPuzzle):
    emptySpaceIndex = oldPuzzle.state.index(0)
    state = oldPuzzle.state

    # Riktningar: (offset, boundary condition)
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
            hWeight = h1(newState)
            newPuzzle = Puzzle(newState, gWeight, hWeight)
            if not any(puzzle.state == newPuzzle.state for puzzle in performedMovesList):
                heapq.heappush(possibleMovesList, newPuzzle)

def h1(state):
    # Mängden felplacerade brickor är antalet brickor som inte är på sin rätta plats.
    return sum(1 for i in range(9) if state[i] != goal[i] and state[i] != 0) # 0 aldrig felplacerad


def h2(state):
    # manhattan avståndet är summan av avstånden för varje bricka till sin rätta plats.
    distance = 0
    for i in range(9):
        if state[i] != 0 and state[i] != goal[i]:  # ignorera 0
            goal_x = (state[i] - 1) % 3
            goal_y = (state[i] - 1) // 3
            current_x = i % 3
            current_y = i // 3
            distance += abs(goal_x - current_x) + abs(goal_y - current_y)
    return distance

if __name__ == "__main__":  # Ensures the script is run directly
    main()