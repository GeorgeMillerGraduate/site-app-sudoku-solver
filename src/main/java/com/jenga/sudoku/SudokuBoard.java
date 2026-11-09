
package com.jenga.sudoku;

/*
 * ============================================================================
 * SUDOKU BOARD - REPRESENTATION, VALIDATION AND TEXTUAL DISPLAY
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
 * This class represents a standard 9x9 Sudoku board containing 81 cells.
 *
 * Rather than using a two-dimensional array, the board is represented
 * internally by a one-dimensional integer array.
 *
 * Cells are stored in row-major order, beginning at the top-left corner
 * and progressing from left to right before moving down to the next row.
 *
 * Coordinates use zero-based indexing:
 *
 *     x = column (0-8)
 *     y = row    (0-8)
 *
 * The corresponding array index is calculated using:
 *
 *     index = y * 9 + x
 *
 * Cell values range from 0 to 9:
 *
 *     0   = Empty cell
 *     1-9 = Sudoku digits
 *
 * ============================================================================
 * VALIDATION MODES
 * ============================================================================
 *
 * The class provides two validation modes:
 *
 * LEGAL:
 *     Determines whether a row, column or box contains duplicate
 *     nonzero digits. Empty cells are permitted.
 *
 * SOLVED:
 *     Determines whether a row, column or box contains all nine
 *     Sudoku digits without duplicates or empty cells.
 *
 * The same validation methods support both modes, avoiding the need
 * for separate implementations.
 *
 * ============================================================================
 * PUBLIC FUNCTIONALITY
 * ============================================================================
 *
 * addNumber(x, y, value)
 *     Inserts or replaces a value at the specified coordinates.
 *
 * getNumber(x, y)
 *     Retrieves the value stored at the specified coordinates.
 *
 * rowCheck(row, mode)
 *     Checks a specified row using the selected validation mode.
 *
 * columnCheck(column, mode)
 *     Checks a specified column using the selected validation mode.
 *
 * boxCheck(box, mode)
 *     Checks a specified 3x3 box using the selected validation mode.
 *
 * boardCheck(mode)
 *     Checks all nine rows, columns and boxes.
 *
 * toString()
 *     Produces a formatted textual representation of the board.
 *
 * ============================================================================
 * DESIGN NOTES
 * ============================================================================
 *
 * The class stores its board data privately. External code accesses
 * and modifies cells through public methods with coordinate and value
 * validation.
 *
 * Adding a number does not automatically guarantee that the resulting
 * Sudoku board is legal. The validation methods can be used to check
 * the board after modifications.
 *
 * The implementation provides a foundation for Sudoku solving,
 * puzzle generation, backtracking algorithms and graphical interfaces.
 *
 * ============================================================================
 */

/**
 * Represents a standard 9x9 Sudoku board using a one-dimensional array.
 *
 * <p>
 * Provides cell access, cell modification, row validation, column validation,
 * box validation, whole-board validation and formatted textual output.
 * </p>
 *
 * <p>
 * All coordinates and region indices are zero-based.
 * </p>
 *
 * @author George Miller
 * @version 1.1
 * @since 2026
 */
public class SudokuBoard {

    /**
     * Defines the available Sudoku validation modes.
     *
     * LEGAL permits empty cells but rejects duplicate digits.
     *
     * SOLVED requires all nine digits to be present exactly once.
     */
    public enum CheckMode {
        LEGAL,
        SOLVED
    }

    /**
     * Stores the 81 Sudoku cells in row-major order.
     *
     * Zero represents an empty cell.
     */
    private final int[] BOARD = new int[81];

    /**
     * Inserts or replaces a number at the specified board coordinates.
     *
     * <p>
     * The x coordinate represents the column and the y coordinate
     * represents the row.
     * </p>
     *
     * <p>
     * The one-dimensional array index is calculated as:
     * </p>
     *
     * <pre>
     * index = y * 9 + x
     * </pre>
     *
     * <p>
     * A value of zero clears the selected cell.
     * Values from 1 to 9 represent Sudoku digits.
     * </p>
     *
     * <p>
     * This method does not check whether inserting the value causes
     * a Sudoku rule violation.
     * </p>
     *
     * @param x the column coordinate (0-8)
     * @param y the row coordinate (0-8)
     * @param value the number to insert (0-9)
     * @throws IllegalArgumentException if either coordinate or the
     *         supplied value is outside its permitted range
     */
    public void addNumber(int x, int y, int value) {
        if (x < 0 || x > 8) {
            throw new IllegalArgumentException(
                    "X coordinate must be between 0-8");
        } //i

        if (y < 0 || y > 8) {
            throw new IllegalArgumentException(
                    "Y coordinate must be between 0-8");
        } //i

        if (value < 0 || value > 9) {
            throw new IllegalArgumentException(
                    "Sudoku value must be between 0-9");
        } //i

        BOARD[y * 9 + x] = value;
    }

    /**
     * Retrieves the number stored at the specified board coordinates.
     *
     * <p>
     * Coordinates use zero-based indexing, with (0,0) representing
     * the top-left corner and (8,8) representing the bottom-right corner.
     * </p>
     *
     * <p>
     * The method returns zero if the selected cell is empty.
     * </p>
     *
     * @param x the column coordinate (0-8)
     * @param y the row coordinate (0-8)
     * @return the value stored at the specified cell (0-9)
     * @throws IllegalArgumentException if either coordinate is outside 0-8
     */
    public int getNumber(int x, int y) {
        if (x < 0 || x > 8) {
            throw new IllegalArgumentException(
                    "X coordinate must be between 0-8");
        } //i

        if (y < 0 || y > 8) {
            throw new IllegalArgumentException(
                    "Y coordinate must be between 0-8");
        } //i

        return BOARD[y * 9 + x];
    }

    /**
     * Checks whether a specified row is legal or solved.
     *
     * <p>
     * In LEGAL mode, empty cells are ignored and duplicate nonzero
     * digits cause the method to return false.
     * </p>
     *
     * <p>
     * In SOLVED mode, every cell must contain a digit from 1 to 9,
     * and no digit may appear more than once.
     * </p>
     *
     * @param row the zero-based row index (0-8)
     * @param mode the validation mode (LEGAL or SOLVED)
     * @return true if the row satisfies the selected condition;
     *         false otherwise
     * @throws IllegalArgumentException if the row index is outside 0-8
     * @throws NullPointerException if mode is null
     */
    public boolean rowCheck(int row, CheckMode mode) {
        if (row < 0 || row > 8) {
            throw new IllegalArgumentException(
                    "Row index must be between 0-8");
        } //i

        java.util.Objects.requireNonNull(mode, "Check mode cannot be null");

        int[] numbers = new int[9];

        for (int i = 9 * row; i < (9 * row) + 9; i++) {
            int value = BOARD[i];

            // Handle empty cells according to the selected mode
            if (value == 0) {
                if (mode == CheckMode.SOLVED) {
                    return false;
                }
                continue;
            } //i

            // Check whether this digit has already appeared
            if (numbers[value - 1] != 0) {
                return false;
            } else {
                numbers[value - 1]++;
            } //ie
        } //f

        return true;
    }

    /**
     * Checks whether a specified column is legal or solved.
     *
     * <p>
     * Because the board uses row-major storage, consecutive cells
     * in the same column are separated by nine array positions.
     * </p>
     *
     * <p>
     * LEGAL mode permits empty cells but rejects duplicate digits.
     * SOLVED mode additionally requires every cell to be populated.
     * </p>
     *
     * @param column the zero-based column index (0-8)
     * @param mode the validation mode (LEGAL or SOLVED)
     * @return true if the column satisfies the selected condition;
     *         false otherwise
     * @throws IllegalArgumentException if the column index is outside 0-8
     * @throws NullPointerException if mode is null
     */
    public boolean columnCheck(int column, CheckMode mode) {
        if (column < 0 || column > 8) {
            throw new IllegalArgumentException(
                    "Column index must be between 0-8");
        } //i

        java.util.Objects.requireNonNull(mode, "Check mode cannot be null");

        int index = column;
        int[] numbers = new int[9];

        for (int i = 0; i < 9; i++) {
            int value = BOARD[index];

            // Handle empty cells according to the selected mode
            if (value == 0) {
                if (mode == CheckMode.SOLVED) {
                    return false;
                }
            } else {
                // Check whether this digit has already appeared
                if (numbers[value - 1] != 0) {
                    return false;
                } else {
                    numbers[value - 1]++;
                } //ie
            } //ie

            // Move to the next row in the same column
            index += 9;
        } //f

        return true;
    }

    /**
     * Checks whether a specified 3x3 Sudoku box is legal or solved.
     *
     * <p>
     * Boxes are numbered from 0 to 8 in row-major order:
     * </p>
     *
     * <pre>
     * 0 1 2
     * 3 4 5
     * 6 7 8
     * </pre>
     *
     * <p>
     * The starting position is calculated using integer division
     * and the modulo operator.
     * </p>
     *
     * <p>
     * Two nested loops traverse the three rows and three columns
     * of the selected box.
     * </p>
     *
     * <p>
     * LEGAL mode permits empty cells but rejects duplicate digits.
     * SOLVED mode requires all nine digits to be present exactly once.
     * </p>
     *
     * @param box the zero-based box index (0-8)
     * @param mode the validation mode (LEGAL or SOLVED)
     * @return true if the box satisfies the selected condition;
     *         false otherwise
     * @throws IllegalArgumentException if the box index is outside 0-8
     * @throws NullPointerException if mode is null
     */
    public boolean boxCheck(int box, CheckMode mode) {
        if (box < 0 || box > 8) {
            throw new IllegalArgumentException(
                    "Box index must be between 0-8");
        } //i

        java.util.Objects.requireNonNull(mode, "Check mode cannot be null");

        // Calculate the starting position of the 3x3 box
        int startRow = (box / 3) * 3;
        int startColumn = (box % 3) * 3;
        int index = startRow * 9 + startColumn;

        int[] numbers = new int[9];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int value = BOARD[index];

                // Handle empty cells according to the selected mode
                if (value == 0) {
                    if (mode == CheckMode.SOLVED) {
                        return false;
                    }
                } else {
                    // Check whether this digit has already appeared
                    if (numbers[value - 1] != 0) {
                        return false;
                    } else {
                        numbers[value - 1]++;
                    } //ie
                } //ie

                // Move to the next column
                index++;
            } //f

            // Move to the next row within the same box
            index += 6;
        } //f

        return true;
    }

    /**
     * Checks whether the entire Sudoku board is legal or solved.
     *
     * <p>
     * All nine rows, nine columns and nine 3x3 boxes are checked
     * using the selected validation mode.
     * </p>
     *
     * <p>
     * In LEGAL mode, the board may contain empty cells but must not
     * contain any duplicate digits within a row, column or box.
     * </p>
     *
     * <p>
     * In SOLVED mode, the board must be completely populated and
     * satisfy every Sudoku uniqueness constraint.
     * </p>
     *
     * @param mode the validation mode (LEGAL or SOLVED)
     * @return true if the entire board satisfies the selected condition;
     *         false otherwise
     * @throws NullPointerException if mode is null
     */
    public boolean boardCheck(CheckMode mode) {
        java.util.Objects.requireNonNull(mode, "Check mode cannot be null");

        for (int i = 0; i < 9; i++) {
            if (!rowCheck(i, mode)
                    || !columnCheck(i, mode)
                    || !boxCheck(i, mode)) {
                return false;
            } //i
        } //f

        return true;
    }

    /**
     * Produces a formatted textual representation of the Sudoku board.
     *
     * <p>
     * The board is displayed as nine rows containing nine cells each.
     * Populated cells display their numerical value, while empty cells
     * are represented by an asterisk (*).
     * </p>
     *
     * <p>
     * Vertical separators are inserted after the third and sixth columns.
     * Horizontal separators are inserted after the third and sixth rows.
     * These separators visually distinguish the nine 3x3 Sudoku boxes.
     * </p>
     *
     * <p>
     * A StringBuilder is used to construct the representation efficiently
     * without repeatedly creating intermediate String objects.
     * </p>
     *
     * <p>
     * This method does not modify the board or perform Sudoku validation.
     * </p>
     *
     * @return a formatted, multiline String representing the board
     */
    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();

        int index = 0;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (BOARD[index] != 0) {
                    build.append(BOARD[index]);
                } else {
                    build.append("*");
                } //ie
                index++;

                if (x != 8) {
                    build.append(" ");
                } //i

                if ((x + 1) % 3 == 0 && x != 8) {
                    build.append("| ");
                } //i
            } //f

            if ((y + 1) % 3 == 0 && y != 8) {
                build.append("\n---------------------");
            } //i

            if (y != 8) {
                build.append("\n");
            } //i
        } //f

        return build.toString();
    }
}
