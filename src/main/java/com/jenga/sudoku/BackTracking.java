package com.jenga.sudoku;

import java.util.Objects;

/*
 * ============================================================================
 * SUDOKU SOLVER - RECURSIVE BACKTRACKING ALGORITHM
 * ============================================================================
 *
 * Copyright (c) 2026 George Miller
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * ============================================================================
 * PROJECT OVERVIEW
 * ============================================================================
 *
 * This class implements a recursive backtracking algorithm for solving
 * standard 9x9 Sudoku puzzles.
 *
 * The solver operates on an existing SudokuBoard instance and attempts
 * to populate every empty cell with a valid digit between 1 and 9.
 *
 * A Sudoku solution must satisfy three fundamental constraints:
 *
 *     1. Every row contains the digits 1 through 9 exactly once.
 *     2. Every column contains the digits 1 through 9 exactly once.
 *     3. Every 3x3 box contains the digits 1 through 9 exactly once.
 *
 * The algorithm systematically explores candidate values and abandons
 * branches of the search whenever they violate Sudoku constraints.
 *
 * ============================================================================
 * RECURSIVE BACKTRACKING
 * ============================================================================
 *
 * Backtracking is a depth-first search technique used to solve
 * constraint-satisfaction problems.
 *
 * The algorithm constructs a candidate solution incrementally,
 * checking whether each decision remains consistent with the
 * problem's constraints.
 *
 * If a candidate leads to a contradiction, the algorithm returns
 * to an earlier decision and attempts an alternative.
 *
 * The Sudoku solver follows these stages:
 *
 *     1. Begin at the first cell of the board.
 *
 *     2. Convert the current one-dimensional cell index into
 *        its corresponding x and y coordinates.
 *
 *     3. If the cell already contains a number, preserve that
 *        value and continue to the next cell.
 *
 *     4. Otherwise, attempt digits 1 through 9 in ascending order.
 *
 *     5. After inserting a candidate, check whether the board
 *        remains legal.
 *
 *     6. If the board is legal, recursively attempt to solve
 *        the remaining cells.
 *
 *     7. If the recursive search succeeds, propagate success
 *        through the previous recursive calls.
 *
 *     8. If every candidate fails, reset the current cell to
 *        zero and return failure to the previous call.
 *
 *     9. Continue until a valid solution is discovered or all
 *        possible candidate branches have been exhausted.
 *
 * ============================================================================
 * BOARD REPRESENTATION
 * ============================================================================
 *
 * The SudokuBoard class stores its 81 cells in a one-dimensional array.
 *
 * This solver traverses those cells sequentially using indices 0 to 80.
 *
 * The corresponding two-dimensional coordinates are calculated using:
 *
 *     x = coordinate % 9
 *     y = coordinate / 9
 *
 * Both coordinates use zero-based indexing.
 *
 * The solver uses the public SudokuBoard interface to read and
 * modify cells rather than directly accessing the underlying array.
 *
 * ============================================================================
 * SEARCH TERMINATION
 * ============================================================================
 *
 * The recursive search terminates successfully when all 81 cells
 * have been processed and the board satisfies the SOLVED condition.
 *
 * A failed branch returns false, allowing the previous recursive
 * call to attempt another candidate.
 *
 * The solver stops after discovering the first valid solution.
 *
 * It does not enumerate all solutions or determine whether the
 * puzzle has a unique solution.
 *
 * ============================================================================
 * COMPUTATIONAL COMPLEXITY
 * ============================================================================
 *
 * In the worst case, each empty cell may have up to nine possible
 * candidate digits.
 *
 * If E represents the number of empty cells, a loose upper bound
 * on the search complexity is:
 *
 *     O(9^E)
 *
 * The legality checks reduce the number of candidate branches
 * explored in practice by rejecting invalid assignments early.
 *
 * Each recursive call requires a bounded amount of additional
 * memory for a standard 9x9 Sudoku board.
 *
 * The maximum recursion depth is 81 calls, giving O(81) auxiliary
 * stack space, equivalent to O(1) for a fixed-size Sudoku board.
 *
 * ============================================================================
 * DESIGN NOTES AND LIMITATIONS
 * ============================================================================
 *
 * The solver modifies the supplied SudokuBoard instance directly.
 *
 * Existing nonzero cells are treated as fixed clues and are not
 * overwritten during the search.
 *
 * The initial board should be legal before solving begins.
 * An illegal starting board cannot produce a valid Sudoku solution.
 *
 * Candidate values are attempted in ascending numerical order,
 * so the first solution found depends on this search ordering.
 *
 * This implementation prioritises readability and educational
 * clarity over advanced Sudoku solving optimisations.
 *
 * Possible future improvements include:
 *
 *     - Checking only the affected row, column and box
 *     - Selecting the empty cell with the fewest candidates
 *     - Candidate elimination and constraint propagation
 *     - Counting all possible solutions
 *     - Measuring recursive calls and backtracking operations
 *     - Visualising the search process
 *
 * ============================================================================
 */
/**
 * Solves a standard 9x9 Sudoku puzzle using recursive backtracking.
 *
 * <p>
 * The solver performs a depth-first search through candidate assignments,
 * rejecting illegal configurations and restoring cells when a search branch
 * fails.
 * </p>
 *
 * <p>
 * The supplied SudokuBoard instance is modified directly. Existing nonzero
 * cells are preserved throughout the solving process.
 * </p>
 *
 * @author George Miller
 * @version 1.1
 * @since 2026
 */
public class BackTracking {

    /**
     * The Sudoku board to be solved.
     *
     * The reference cannot be reassigned after construction, although the
     * board's cell values remain mutable.
     */
    private final SudokuBoard board;

    /**
     * Constructs a recursive backtracking solver for the supplied board.
     *
     * <p>
     * The solver retains a reference to the original SudokuBoard rather than
     * creating a copy.
     * </p>
     *
     * <p>
     * Consequently, successful candidate assignments are reflected directly in
     * the supplied board.
     * </p>
     *
     * @param board the SudokuBoard instance to solve
     * @throws NullPointerException if board is null
     */
    public BackTracking(SudokuBoard board) {
        this.board = Objects.requireNonNull(
                board, "Sudoku board cannot be null");
    }

    /**
     * Attempts to solve the Sudoku puzzle using recursive backtracking.
     *
     * <p>
     * The search begins at coordinate zero, corresponding to the top-left cell
     * of the board.
     * </p>
     *
     * <p>
     * The method delegates the recursive search to the private solve(int
     * coordinate) method.
     * </p>
     *
     * <p>
     * A successful search leaves the board in a solved state. An unsuccessful
     * search restores the empty cells explored by the algorithm to zero.
     * </p>
     *
     * @return true if a valid Sudoku solution is found; false if no solution
     * exists
     */
    public boolean solve() {
        return solve(0);
    }

    /**
     * Recursively attempts to solve the Sudoku board from a given cell.
     *
     * <p>
     * The coordinate represents a position in the one-dimensional board,
     * ranging from 0 to 81.
     * </p>
     *
     * <p>
     * When coordinate reaches 81, all cells have been processed. The board is
     * then checked against the SOLVED condition.
     * </p>
     *
     * <p>
     * Already populated cells are skipped because they represent fixed values
     * in the original puzzle.
     * </p>
     *
     * <p>
     * Empty cells are assigned candidate digits from 1 through 9. Each
     * candidate is validated before the search continues.
     * </p>
     *
     * <p>
     * If a recursive branch fails, the algorithm attempts the next candidate.
     * If all candidates fail, the current cell is reset to zero and failure is
     * returned to the previous call.
     * </p>
     *
     * @param coordinate the current one-dimensional cell index (0-81)
     * @return true if a solution is found from the current position; false
     * otherwise
     */
    private boolean solve(int coordinate) {
        // All 81 cells have been processed
        if (coordinate == 81) {
            return this.board.boardCheck(SudokuBoard.CheckMode.SOLVED);
        } //i

        // Convert the 1D coordinate into x and y coordinates
        int x = coordinate % 9;
        int y = coordinate / 9;

        // Skip cells that already contain a number
        if (this.board.getNumber(x, y) != 0) {
            return solve(coordinate + 1);
        } //i

        // Try each possible Sudoku digit
        for (int i = 1; i <= 9; i++) {
            this.board.addNumber(x, y, i);

            // Continue recursively if the board remains legal
            if (this.board.boardCheck(SudokuBoard.CheckMode.LEGAL)) {
                if (solve(coordinate + 1)) {
                    return true;
                } //i
            } //i
        } //f

        // No candidate worked, so reset this cell
        this.board.addNumber(x, y, 0);

        return false;
    }
}
