package edu.iastate.cs472.proj1;

import java.io.FileNotFoundException;

/**
 *  
 * @author Jackson Mears
 *
 */

public class EightPuzzle 
{
	/**
	 * This static method solves an 8-puzzle with a given initial state using three heuristics. The 
	 * first two, allowing single moves only, compare the board configuration with the goal configuration 
	 * by the number of mismatched tiles, and by the Manhattan distance, respectively.  The third 
	 * heuristic, designed by yourself, allows double moves and must be also admissible.  The goal 
	 * configuration set for all puzzles is
	 * 
	 * 			1 2 3
	 * 			8   4
	 * 			7 6 5
	 * 
	 * @param s0
	 * @return a string specified in the javadoc below
	 */
    public static String solve8Puzzle(State s0)
    {
        if (!s0.solvable())
        {
            return "No solution exists for the following initial state:\n\n" + s0.toString();
        }

        Heuristic h[]   = {Heuristic.tile_mismatch, Heuristic.manhattan_distance, Heuristic.double_move};

        String[] moves  = new String[3];

        for (int i = 0; i < 3; i++)
        {
            moves[i] = AStar(s0, h[i]);
        }

        return moves[0] + "\n" + moves[1] + "\n" + moves[2];
    }


	
	/**
	 * This method implements the A* algorithm to solve the 8-puzzle with an input initial state s0. 
	 * The algorithm implementation is described in Section 3 of the project description. 
	 * 
	 * Precondition: the puzzle is solvable with the initial state s0.
	 * 
	 * @param s0  initial state
	 * @param h   heuristic 
	 * @return    solution string 
	 */
    public static String AStar(State s0, Heuristic heuristic)
    {
        State.heuristic         = heuristic;

        OrderedStateList OPEN   = new OrderedStateList(heuristic, true);
        OrderedStateList CLOSE  = new OrderedStateList(heuristic, false);

        State initial           = (State) s0.clone();

        OPEN.addState(initial);

        Move[] moves;

        if (heuristic == Heuristic.double_move)
        {
            moves = Move.values();
        }
        else
        {
            moves = new Move[]
            {
                Move.LEFT,
                Move.RIGHT,
                Move.UP,
                Move.DOWN
            };
        }


        while (OPEN.size() > 0)
        {
            State current = OPEN.pop();

            if (current.isGoalState())
            {
                return solutionPath(current, heuristic);
            }

            CLOSE.addState(current);

            for (Move m : moves)
            {
                State successor;

                try
                {
                    successor = current.successorState(m);
                }
                catch (IllegalArgumentException e)
                {
                    continue;
                }

                if (successor == null)
                    continue;

                if (CLOSE.findState(successor) != null)
                    continue;

                State existing = OPEN.findState(successor);

                if (existing == null)
                {
                    OPEN.addState(successor);
                }
                else if (successor.moves_count < existing.moves_count)
                {
                    OPEN.removeState(existing);
                    OPEN.addState(successor);
                }
            }    
        }
        return null;
    }	
	

    private static String createSolutionTitle(Heuristic heuristic, int count)
    {
        String _result = "";
        if (heuristic == Heuristic.tile_mismatch)
        {
            _result = (count - 1) + " moves in total (heuristic: number of mismatched tiles)\n";
        }
        else if (heuristic == Heuristic.manhattan_distance)
        {
            _result = (count - 1) + " moves in total (heuristic: the Manhattan distance)\n";
        }
        else if (heuristic == Heuristic.double_move)
        {
            _result = (count - 1) + " moves in total (heuristic: double moves allowed)\n";
        }
        
        return _result;
    }


    private static String createMoves(int count, State[] path)
    {
        String _result = "";

        for (int i = 0; i < count; i++)
        {
            _result += path[i].toString();

            if (i < count - 1)
            {
                _result += path[i + 1].move;
                _result += "\n";
            }
        }

        return _result;
    }



	
	/**
	 * From a goal state, follow the predecessor link to trace all the way back to the initial state. 
	 * Meanwhile, generate a string to represent board configurations in the reverse order, with 
	 * the initial configuration appearing first. Between every two consecutive configurations 
	 * is the move that causes their transition. A blank line separates a move and a configuration.  
	 * In the string, the sequence is preceded by the total number of moves and a blank line. 
	 * 
	 * See Section 6 in the projection description for an example. 
	 * 
	 * Call the toString() method of the State class. 
	 * 
	 * @param goal
	 * @return
	 */
    private static String solutionPath(State goal, Heuristic heuristic)
    {
        int count       = 0;
        State current   = goal;

        while (current != null)
        {
            count++;
            current = current.predecessor;
        }

        State[] path    = new State[count];
        current         = goal;

        for (int i = count - 1; i >= 0; i--)
        {
            path[i] = current;
            current = current.predecessor;
        }

        String result = "";

        result += createSolutionTitle(heuristic, count);
        result += createMoves(count, path);
        
        return result;
    }	
	
	
}
