
package com.jenga.sudoku;

/*
 * MIT License
 *
 * Copyright (c) 2026 George Miller
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * SudokuWindow provides the JavaFX graphical interface for the Sudoku solver.
 *
 * The application uses three illustrated backgrounds:
 *
 * main.png  - Normal appearance.
 * solve.png - Solve button highlighted.
 * reset.png - Reset button highlighted.
 *
 * The application also uses icon.png as its window and taskbar icon.
 *
 * All Sudoku cells and buttons use manually measured coordinates based on
 * the original 1199 x 1312 pixel artwork.
 *
 * The interface scales proportionally to occupy approximately 90 percent
 * of the available screen height, including the window decorations.
 *
 * The user can enter numbers using the keyboard, navigate using the arrow
 * keys, solve the puzzle using backtracking and reset the board.
 *
 * @author George Miller
 * @version 1.2
 */
public class SudokuWindow extends Application {

    /**
     * Original dimensions of the Sudoku artwork.
     */
    private static final double IMAGE_WIDTH = 1199;
    private static final double IMAGE_HEIGHT = 1312;

    /**
     * Percentage of the available screen height used by the window.
     */
    private static final double SCREEN_HEIGHT_RATIO = 0.90;

    /**
     * Initial allowance for operating system window decorations.
     */
    private static final double INITIAL_DECORATION_HEIGHT = 40;

    /**
     * Left boundaries of the Sudoku columns.
     */
    private static final int[] LEFT = {
        142, 242, 343, 453, 552, 651, 760, 858, 959
    };

    /**
     * Right boundaries of the Sudoku columns.
     */
    private static final int[] RIGHT = {
        239, 340, 439, 549, 647, 746, 855, 956, 1056
    };

    /**
     * Upper boundaries of the Sudoku rows.
     */
    private static final int[] TOP = {
        165, 267, 365, 473, 570, 670, 777, 874, 974
    };

    /**
     * Lower boundaries of the Sudoku rows.
     */
    private static final int[] BOTTOM = {
        263, 362, 461, 567, 666, 765, 870, 970, 1070
    };

    /**
     * Solve button hitbox.
     */
    private static final Rectangle2D SOLVE_BUTTON =
            new Rectangle2D(208, 1215, 367, 74);

    /**
     * Reset button hitbox.
     */
    private static final Rectangle2D RESET_BUTTON =
            new Rectangle2D(622, 1215, 360, 74);

    /**
     * Colours used by the Sudoku interface.
     */
    private static final Color LEGAL_COLOUR =
            Color.rgb(35, 105, 55);

    private static final Color ILLEGAL_COLOUR =
            Color.rgb(175, 35, 35);

    private static final Color HIGHLIGHT_COLOUR =
            Color.rgb(210, 170, 65, 0.25);

    /**
     * Background images.
     */
    private Image normalImage;
    private Image solveImage;
    private Image resetImage;
    private Image currentImage;

    /**
     * Canvas and graphics context used for rendering.
     */
    private Canvas canvas;
    private GraphicsContext graphics;

    /**
     * Current Sudoku board.
     */
    private SudokuBoard board;

    /**
     * Selected Sudoku cell.
     *
     * A value of -1 indicates no selection.
     */
    private int selectedX = -1;
    private int selectedY = -1;

    /**
     * Scale factor between original artwork coordinates and screen pixels.
     */
    private double scale = 1.0;

    /**
     * Initialises and displays the Sudoku window.
     *
     * @param stage primary JavaFX stage
     */
    @Override
    public void start(Stage stage) {

        board = new SudokuBoard();

        /*
         * Load the illustrated backgrounds.
         */
        normalImage = loadImage("main.png");
        solveImage = loadImage("solve.png");
        resetImage = loadImage("reset.png");

        currentImage = normalImage;

        /*
         * Obtain the usable screen dimensions.
         */
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        double targetWindowHeight =
                screen.getHeight() * SCREEN_HEIGHT_RATIO;

        /*
         * Calculate an initial scale that fits within the screen.
         */
        double availableCanvasHeight =
                targetWindowHeight - INITIAL_DECORATION_HEIGHT;

        scale = Math.min(
                availableCanvasHeight / IMAGE_HEIGHT,
                screen.getWidth() * 0.95 / IMAGE_WIDTH
        );

        /*
         * Create the scaled canvas.
         */
        canvas = new Canvas(
                IMAGE_WIDTH * scale,
                IMAGE_HEIGHT * scale
        );

        graphics = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);

        Scene scene = new Scene(
                root,
                canvas.getWidth(),
                canvas.getHeight()
        );

        /*
         * Register mouse interaction.
         */
        canvas.setOnMouseMoved(this::handleMouseMoved);
        canvas.setOnMouseClicked(this::handleMouseClicked);

        /*
         * Register keyboard interaction.
         */
        scene.setOnKeyPressed(event -> {

            KeyCode key = event.getCode();

            switch (key) {

                case LEFT -> moveSelection(-1, 0);
                case RIGHT -> moveSelection(1, 0);
                case UP -> moveSelection(0, -1);
                case DOWN -> moveSelection(0, 1);

                case BACK_SPACE, DELETE, DIGIT0, NUMPAD0 ->
                    enterNumber(0);

                default -> {

                    int number = getNumberFromKey(key);

                    if (number >= 1 && number <= 9) {
                        enterNumber(number);
                    }
                }
            }

            event.consume();
        });

        /*
         * Configure the application window.
         */
        stage.setTitle("Sudoku Solver");

        /*
         * Load the custom Sudoku application icon.
         *
         * This replaces the default Java icon in the title bar
         * and, where supported, the operating system taskbar.
         */
        stage.getIcons().add(loadImage("icon.png"));

        stage.setScene(scene);
        stage.setResizable(false);

        /*
         * Show the stage to determine actual window decoration height.
         */
        stage.show();

        double decorationHeight =
                stage.getHeight() - scene.getHeight();

        /*
         * Recalculate the scale using the actual decoration height.
         */
        scale = Math.min(
                (targetWindowHeight - decorationHeight) / IMAGE_HEIGHT,
                screen.getWidth() * 0.95 / IMAGE_WIDTH
        );

        canvas.setWidth(IMAGE_WIDTH * scale);
        canvas.setHeight(IMAGE_HEIGHT * scale);

        stage.sizeToScene();

        /*
         * Centre the application on the usable screen.
         */
        stage.setX(
                screen.getMinX()
                + (screen.getWidth() - stage.getWidth()) / 2.0
        );

        stage.setY(
                screen.getMinY()
                + (screen.getHeight() - stage.getHeight()) / 2.0
        );

        draw();

        canvas.requestFocus();
    }

    /**
     * Loads an image from the Maven resources directory.
     *
     * @param filename image filename
     * @return loaded image
     */
    private Image loadImage(String filename) {

        var resource = getClass().getResource("/" + filename);

        if (resource == null) {

            throw new IllegalStateException(
                    "Unable to locate image: " + filename
            );
        }

        return new Image(resource.toExternalForm());
    }

    /**
     * Converts displayed mouse coordinates into original image coordinates.
     *
     * @param value displayed coordinate
     * @return original image coordinate
     */
    private double toOriginal(double value) {
        return value / scale;
    }

    /**
     * Returns the Sudoku column containing the specified coordinate.
     *
     * @param x original image X coordinate
     * @return column index or -1
     */
    private int getColumn(double x) {

        for (int column = 0; column < 9; column++) {

            if (x >= LEFT[column] && x <= RIGHT[column]) {
                return column;
            }
        }

        return -1;
    }

    /**
     * Returns the Sudoku row containing the specified coordinate.
     *
     * @param y original image Y coordinate
     * @return row index or -1
     */
    private int getRow(double y) {

        for (int row = 0; row < 9; row++) {

            if (y >= TOP[row] && y <= BOTTOM[row]) {
                return row;
            }
        }

        return -1;
    }

    /**
     * Handles mouse movement over the board and buttons.
     *
     * @param event mouse movement event
     */
    private void handleMouseMoved(MouseEvent event) {

        double x = toOriginal(event.getX());
        double y = toOriginal(event.getY());

        /*
         * Change the background when hovering over a button.
         */
        if (SOLVE_BUTTON.contains(x, y)) {

            currentImage = solveImage;

        } else if (RESET_BUTTON.contains(x, y)) {

            currentImage = resetImage;

        } else {

            currentImage = normalImage;
        }

        /*
         * Select a Sudoku cell when hovering over it.
         */
        int column = getColumn(x);
        int row = getRow(y);

        if (column != -1 && row != -1) {

            selectedX = column;
            selectedY = row;
        }

        draw();
    }

    /**
     * Handles mouse clicks.
     *
     * @param event mouse click event
     */
    private void handleMouseClicked(MouseEvent event) {

        double x = toOriginal(event.getX());
        double y = toOriginal(event.getY());

        if (SOLVE_BUTTON.contains(x, y)) {

            solveBoard();
            return;
        }

        if (RESET_BUTTON.contains(x, y)) {

            resetBoard();
            return;
        }

        int column = getColumn(x);
        int row = getRow(y);

        if (column != -1 && row != -1) {

            selectedX = column;
            selectedY = row;

            draw();
        }
    }

    /**
     * Moves the selected cell using the arrow keys.
     *
     * @param dx horizontal movement
     * @param dy vertical movement
     */
    private void moveSelection(int dx, int dy) {

        if (selectedX == -1 || selectedY == -1) {

            selectedX = 0;
            selectedY = 0;

        } else {

            selectedX = Math.max(
                    0,
                    Math.min(8, selectedX + dx)
            );

            selectedY = Math.max(
                    0,
                    Math.min(8, selectedY + dy)
            );
        }

        draw();
    }

    /**
     * Converts a keyboard key into a Sudoku number.
     *
     * @param key JavaFX key code
     * @return number between 1 and 9, or -1
     */
    private int getNumberFromKey(KeyCode key) {

        return switch (key) {

            case DIGIT1, NUMPAD1 -> 1;
            case DIGIT2, NUMPAD2 -> 2;
            case DIGIT3, NUMPAD3 -> 3;
            case DIGIT4, NUMPAD4 -> 4;
            case DIGIT5, NUMPAD5 -> 5;
            case DIGIT6, NUMPAD6 -> 6;
            case DIGIT7, NUMPAD7 -> 7;
            case DIGIT8, NUMPAD8 -> 8;
            case DIGIT9, NUMPAD9 -> 9;

            default -> -1;
        };
    }

    /**
     * Enters a number into the selected Sudoku cell.
     *
     * A value of zero clears the cell.
     *
     * @param number number to enter
     */
    private void enterNumber(int number) {

        if (selectedX == -1 || selectedY == -1) {
            return;
        }

        board.addNumber(
                selectedX,
                selectedY,
                number
        );

        draw();
    }

    /**
     * Checks whether a Sudoku number is legal.
     *
     * @param x Sudoku column
     * @param y Sudoku row
     * @return true if legal
     */
    private boolean isLegal(int x, int y) {

        int box = (y / 3) * 3 + (x / 3);

        return board.rowCheck(
                y,
                SudokuBoard.CheckMode.LEGAL
        ) && board.columnCheck(
                x,
                SudokuBoard.CheckMode.LEGAL
        ) && board.boxCheck(
                box,
                SudokuBoard.CheckMode.LEGAL
        );
    }

    /**
     * Attempts to solve the current Sudoku puzzle.
     */
    private void solveBoard() {

        if (!board.boardCheck(SudokuBoard.CheckMode.LEGAL)) {
            draw();
            return;
        }

        BackTracking solver = new BackTracking(board);

        solver.solve();

        draw();
    }

    /**
     * Clears the board and current selection.
     */
    private void resetBoard() {

        board = new SudokuBoard();

        selectedX = -1;
        selectedY = -1;

        draw();
    }

    /**
     * Redraws the background, selection and Sudoku numbers.
     */
    private void draw() {

        graphics.clearRect(
                0,
                0,
                canvas.getWidth(),
                canvas.getHeight()
        );

        graphics.save();

        /*
         * Scale the original artwork coordinate system.
         */
        graphics.scale(scale, scale);

        graphics.drawImage(
                currentImage,
                0,
                0,
                IMAGE_WIDTH,
                IMAGE_HEIGHT
        );

        drawSelection();
        drawNumbers();

        graphics.restore();
    }

    /**
     * Draws the translucent selected-cell highlight.
     */
    private void drawSelection() {

        if (selectedX == -1 || selectedY == -1) {
            return;
        }

        double inset = 3;

        double x = LEFT[selectedX] + inset;
        double y = TOP[selectedY] + inset;

        double width =
                RIGHT[selectedX] - LEFT[selectedX] - 2 * inset;

        double height =
                BOTTOM[selectedY] - TOP[selectedY] - 2 * inset;

        graphics.setFill(HIGHLIGHT_COLOUR);

        graphics.fillRect(
                x,
                y,
                width,
                height
        );
    }

    /**
     * Draws Sudoku numbers using a serif font.
     *
     * Legal numbers are green.
     * Conflicting numbers are red.
     */
    private void drawNumbers() {

        graphics.setFont(
                Font.font(
                        "Georgia",
                        FontWeight.BOLD,
                        52
                )
        );

        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.setTextBaseline(VPos.CENTER);

        for (int y = 0; y < 9; y++) {

            for (int x = 0; x < 9; x++) {

                int number = board.getNumber(x, y);

                if (number == 0) {
                    continue;
                }

                graphics.setFill(
                        isLegal(x, y)
                                ? LEGAL_COLOUR
                                : ILLEGAL_COLOUR
                );

                double centerX =
                        (LEFT[x] + RIGHT[x]) / 2.0;

                double centerY =
                        (TOP[y] + BOTTOM[y]) / 2.0;

                graphics.fillText(
                        Integer.toString(number),
                        centerX,
                        centerY
                );
            }
        }
    }
}
