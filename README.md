# A* Search for the 8-Puzzle

COMS 4720/5720 Project 1 (A* search with three heuristics: mismatched tiles, Manhattan distance, and a custom one for single + double moves).

## Heads up before you read the code

I've mostly been writing C++ for the last year or two and haven't touched Java much, so apologies if some of this doesn't look like typical Java, or if I missed a handy standard library method. A few things I did differently from the skeleton:

- Board is a flat `int[]` instead of `int[][]`. It's stored row-major, so I index with math (`row = i / 3`, `col = i % 3`). Felt simpler to me than juggling two indices.
- Renamed a bunch of methods and variables for my own clarity and preferences. The required interfaces and behavior are all still there.
- `remove` is called `pop` in `OrderedStateList`. It made more sense to me coming from C++ containers. However, it's still functionally the same thing, removes and returns the first state in the list, which is the min-cost state in OPEN.
- Different tab width. I wrote everything in nvim, so the indentation of my lines may look a little different from the skeleton's.

## Compiling and running

```bash
mkdir bin
javac -d bin src/*.java
java -cp bin edu.iastate.cs472.proj1.PuzzleSolver
```

It reads the starting puzzle from `input/8Puzzle.txt`.




# Writeup: Heuristic $h_3$ for Single and Double Moves

When only single moves are allowed, the Manhattan distance is an admissible heuristic because a single move can reduce the total Manhattan distance by at most one. When double moves are also allowed, Manhattan distance by itself is no longer admissible. A double move shifts two tiles at once, potentially reducing the total Manhattan distance by two in a single move.

So, I chose to use the following heuristic for the double-move version:

$$h_3(s) = \left\lceil \frac{MD(s)}{2} \right\rceil$$

## Why my $h_3$ is admissible

Consider a state whose Manhattan distance from the goal is $D$.

A single move changes the position of one tile by one square, so it can reduce the total Manhattan distance by at most one. A double move changes the position of two tiles by one square each, so it can reduce the total Manhattan distance by at most two.

Therefore, after $k$ moves, the maximum possible reduction in Manhattan distance is $2k$. Any solution must completely eliminate the current Manhattan distance, so

$$2k \geq D.$$

This implies

$$k \geq \frac{D}{2}.$$

Since the number of moves must be an integer,

$$k \geq \left\lceil \frac{D}{2} \right\rceil.$$

Thus $h_3(s) = \lceil MD(s)/2 \rceil$ is a lower bound on the actual minimum number of moves required to reach the goal. It never overestimates the true cost to the goal, so it is admissible.

## Why I chose this heuristic

There was certainly a more accurate solution that would be recommended in a real life setting. As there are no obsticles, only in the context of the manhattan moves of course, you could certainly have computed the real manhattan distance with double moves from each square rather than a simple half of the single move version. 

However, dividing the Manhattan distance by two adjusts the estimate to the new move set while retaining useful information about how far the tiles are from their goal positions. Perhaps even more importantly, taking the ceiling keeps the heuristic an integer lower bound on the number of moves, therefore making it an easier proof as to why it's admissible.

For example, if a state has Manhattan distance 9, then at least

$$\left\lceil \frac{9}{2} \right\rceil = 5$$

moves are required. Even if every move were a double move that reduced the Manhattan distance by the maximum possible amount, four moves could eliminate at most eight units of Manhattan distance.
