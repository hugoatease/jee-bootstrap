package me.caille.draughts.model;

import me.caille.draughts.exceptions.BoardBoundsException;
import me.caille.draughts.exceptions.CellEmptyException;
import me.caille.draughts.exceptions.IllegalMoveException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Board {
    private List<List<Cell>> board;
    private List<Turn> turns;
    private int ROWS = 10;
    private int COLS = 10;


    public Board() {
        this.board = new ArrayList<>(ROWS);
        this.turns = new LinkedList<>();
        for (int rowIndex=0; rowIndex < ROWS; rowIndex++) {
            ArrayList<Cell> row = new ArrayList<>(COLS);
            for (int colIndex = 0; colIndex < COLS; colIndex++) {
                row.add(new Cell(rowIndex + 1, colIndex + 1));
            }
            this.board.add(row);
        }
    }

    private void initializeColor(int rowStart, int rowEnd, Pawn.PawnColor color) {
        for (int rowIndex=rowStart; rowIndex <= rowEnd; rowIndex++) {
            for (int colIndex=1; colIndex <= COLS; colIndex++) {
                if (rowIndex % 2 == 1) {
                    if (colIndex % 2 == 1) {
                        try {
                            setPawn(rowIndex, colIndex, new Pawn(Pawn.PawnType.PAWN, color));
                        } catch (BoardBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (colIndex % 2 == 0) {
                        try {
                            setPawn(rowIndex, colIndex, new Pawn(Pawn.PawnType.PAWN, color));
                        } catch (BoardBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void initialize() {
        initializeColor(1, 4, Pawn.PawnColor.WHITE);
        initializeColor(7, 10, Pawn.PawnColor.BLACK);
    }

    private void checkBoundaries(int row, int col) throws BoardBoundsException {
        if (row <= 0 || col <= 0 || row > ROWS || col > COLS) {
            throw new BoardBoundsException();
        }
    }

    public Cell getCell(int row, int col) throws BoardBoundsException {
        checkBoundaries(row, col);
        return this.board.get(row - 1).get(col - 1);
    }

    public void setCell(int row, int col, Cell cell) throws BoardBoundsException {
        checkBoundaries(row, col);
        this.board.get(row - 1).set(col - 1, cell);
    }

    public boolean hasPawn(int row, int col) throws BoardBoundsException {
        return getCell(row, col).hasPawn();
    }

    public Pawn getPawn(int row, int col) throws BoardBoundsException, CellEmptyException {
        return getCell(row, col).getPawn();
    }

    public void setPawn(int row, int col, Pawn pawn) throws BoardBoundsException {
        getCell(row, col).setPawn(pawn);
    }

    public Pawn.PawnColor getCurrentTurn() {
        if (this.turns.size() % 2 == 0) {
            return Pawn.PawnColor.WHITE;
        } else {
            return Pawn.PawnColor.BLACK;
        }
    }

    public void playTurn(int srcRow, int srcCol, int dstRow, int dstCol) throws IllegalMoveException {
        if (dstRow % 2 == 1) {
            if (dstCol % 2 == 0) {
                throw new IllegalMoveException();
            }
        } else {
            if (dstCol % 2 == 1) {
                throw new IllegalMoveException();
            }
        }

        try {
            Pawn pawn = getPawn(srcRow, srcCol);
            if (pawn.getPawnColor() == Pawn.PawnColor.WHITE) {
                if (dstRow <= srcRow && pawn.getPawnType() == Pawn.PawnType.PAWN) {
                    throw new IllegalMoveException();
                }
            } else {
                if (dstRow >= srcRow && pawn.getPawnType() == Pawn.PawnType.PAWN) {
                    throw new IllegalMoveException();
                }
            }
            if (hasPawn(dstRow, dstCol)) {
                throw new IllegalMoveException();
            }
            this.turns.add(new Turn(srcRow, srcCol, dstRow, dstCol));
            setPawn(dstRow, dstCol, pawn);
            promoteToQueens();
        } catch (BoardBoundsException e) {
            throw new IllegalMoveException();
        } catch (CellEmptyException e) {
            throw new IllegalMoveException();
        }
    }

    private void promoteToQueens() throws CellEmptyException, BoardBoundsException {
        for (int col=1; col< COLS; col++) {
            if (hasPawn(ROWS, col)) {
                if (getPawn(ROWS, col).getPawnType() == Pawn.PawnType.PAWN) {
                    getPawn(ROWS, col).setPawnType(Pawn.PawnType.QUEEN);
                }
            }

            if (hasPawn(1, col)) {
                if (getPawn(1, col).getPawnType() == Pawn.PawnType.PAWN) {
                    getPawn(1, col).setPawnType(Pawn.PawnType.QUEEN);
                }
            }
        }
    }

    public int pawnCount(Pawn.PawnColor color) {
        int result = 0;
        for (int row=1; row <= ROWS; row++) {
            for (int col=1; col <= COLS; col++) {
                try {
                    if (hasPawn(row, col)) {
                        if (getPawn(row, col).getPawnColor() == color) {
                            result++;
                        }
                    }
                } catch (BoardBoundsException e) {
                    e.printStackTrace();
                } catch (CellEmptyException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
