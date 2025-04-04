initstate = [1, 0, 2, 
             4, 5, 3,
             7, 8, 6]

goal = [1, 2, 3,
        4, 5, 6,
        7, 8, 0]

class Puzzle:
    def __init__(self, state=[], gWeight=0, hWeight=0):
        self.state = state
        self.gWeight = gWeight
        self.fWeight = gWeight + hWeight
        
    state = []
    fWeight = 0
    gWeight = 0

def main():
    print("Algorythm Starting")
    puzzle = Puzzle(initstate, gWeight=0, hWeight=0)
    print("Initial Puzzle: " + str(puzzle.state))
    possibleMoves = []

    while puzzle.state != goal:
        # Add all new possible moves to the list based on the current puzzle
        calculateLegalMoves(possibleMoves, puzzle)
        # Sort the list in Ascending order based on the fWeight
        possibleMoves = sorted(possibleMoves, key=lambda p: p.fWeight)
        puzzle = possibleMoves.pop(0) # Removes and returns the first element
        print("New puzzle Calculated, current puzzle: " + str(puzzle.state))
    
    print("Algorythm Complete!")
    print("Result: " + str(puzzle.state))


def calculateLegalMoves(possibleMoves_in, puzzle):
    emptySpaceIndex = puzzle.state.index(0)
    state = puzzle.state

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

            gWeight = puzzle.gWeight + 1
            hWeight = h1(newState)
            newPuzzle = Puzzle(newState, gWeight, hWeight)
            possibleMoves_in.append(newPuzzle)

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