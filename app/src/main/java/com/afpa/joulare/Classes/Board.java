package com.afpa.joulare.Classes;

import java.util.Random;

public class Board {
    private Cell[][] cells;
    private int rows;
    private int cols;
    private int totalMines;
    private boolean firstClick;

    public Board(int rows, int cols, int totalMines) {
        this.rows = rows;
        this.cols = cols;
        this.totalMines = totalMines;
        this.firstClick = true;
        cells = new Cell[rows][cols];
        initializeCells();
    }

    private void initializeCells() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    private void placeMines(int firstRow, int firstCol) {
        Random rand = new Random();
        int minesPlaced = 0;
        while (minesPlaced < totalMines) {
            int row = rand.nextInt(rows);
            int col = rand.nextInt(cols);

            // Ensure the first click position is not a mine
            if ((row == firstRow && col == firstCol) || cells[row][col].isMine()) {
                continue;
            }

            cells[row][col].setMine(true);
            minesPlaced++;
        }
        calculateAdjacentMines();
    }

    private void calculateAdjacentMines() {
        int[] directions = {-1, 0, 1};
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (cells[row][col].isMine()) continue;
                int mineCount = 0;
                for (int dr : directions) {
                    for (int dc : directions) {
                        if (dr == 0 && dc == 0) continue;
                        int newRow = row + dr;
                        int newCol = col + dc;
                        if (isValidCell(newRow, newCol) && cells[newRow][newCol].isMine()) {
                            mineCount++;
                        }
                    }
                }
                cells[row][col].setAdjacentMines(mineCount);
            }
        }
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void handleFirstClick(int row, int col) {
        if (firstClick) {
            placeMines(row, col);
            firstClick = false;
        }
        revealCell(row, col);
    }

    private void revealCell(int row, int col) {
        if (!isValidCell(row, col) || cells[row][col].isRevealed() || cells[row][col].isFlagged()) {
            return;
        }
        cells[row][col].reveal();
        if (cells[row][col].getAdjacentMines() == 0) {
            // Recursively reveal adjacent cells if there are no adjacent mines
            int[] directions = {-1, 0, 1};
            for (int dr : directions) {
                for (int dc : directions) {
                    if (dr == 0 && dc == 0) continue;
                    revealCell(row + dr, col + dc);
                }
            }
        }
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public boolean isWin() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];
                if (!cell.isRevealed() && !cell.isMine()) {
                    return false;
                }
            }
        }
        return true;
    }
}
