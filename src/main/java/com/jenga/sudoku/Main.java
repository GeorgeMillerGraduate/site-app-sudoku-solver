
package com.jenga.sudoku;

import javafx.application.Application;

/**
 * Main entry point for the Sudoku Solver application.
 *
 * @author George Miller
 * @version 1.0
 */
public class Main {

    /**
     * Launches the JavaFX Sudoku window.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Application.launch(SudokuWindow.class, args);
    }
}
