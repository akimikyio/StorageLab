package ru.akkyne13.storage.view.component;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import ru.akkyne13.storage.structure.MapSnapshot;

public class HashTableView extends Canvas {
    private static final int CELL_SIZE = 60;
    private static final int COLUMNS = 20;
    private static final int MAX_CELLS_TO_DRAW = 4000;

    public static final String DELETED_MARKER = MapSnapshot.DELETED_MARKER;

    private static final Font INDEX_FONT = new Font(10);
    private static final Font TEXT_FONT = new Font(11);

    public HashTableView() {
        setWidth(COLUMNS * CELL_SIZE);
        setHeight(2 * CELL_SIZE);
    }

    public void drawHashTable(String[] keysSnapshot, boolean[] highlightedCells) {
        if (keysSnapshot == null) {
            return;
        }

        int cellsToDraw = Math.min(keysSnapshot.length, MAX_CELLS_TO_DRAW);
        boolean hasOverflow = keysSnapshot.length > MAX_CELLS_TO_DRAW;

        prepareCanvas(cellsToDraw, hasOverflow);
        GraphicsContext gc = getGraphicsContext2D();

        for (int i = 0; i < keysSnapshot.length; i++) {
            int x = (i % COLUMNS) * CELL_SIZE;
            int y = (i / COLUMNS) * CELL_SIZE;

            boolean isHighlighted = (highlightedCells != null && i < highlightedCells.length && highlightedCells[i]);
            String cellValue = keysSnapshot[i];

            drawCellBackground(gc, x, y, cellValue, isHighlighted);
            drawCellIndex(gc, x, y, i);
            drawCellContent(gc, x, y, cellValue);
        }

        if (hasOverflow) {
            gc.setFill(Color.RED);
            gc.setFont(new Font(14));
            String warningText = String.format("Показаны первые %d ячеек из %d. Остальные скрыты из-за лимита видеопамяти.", MAX_CELLS_TO_DRAW, keysSnapshot.length);
            gc.fillText(warningText, 10, getHeight() - 15);
        }
    }


    private void prepareCanvas(int cellsToDraw, boolean hasOverflow) {
        int rows = (int) Math.ceil((double) cellsToDraw / COLUMNS);
        double newHeight = rows * CELL_SIZE;

        setHeight(newHeight);

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
    }

    private void drawCellBackground(GraphicsContext gc, int x, int y, String cellValue, boolean isHighlighted) {
        if (isHighlighted) {
            gc.setFill(Color.YELLOW);
        } else if (cellValue == null) {
            gc.setFill(Color.WHITE);
        } else if (cellValue.equals(DELETED_MARKER)) {
            gc.setFill(Color.LIGHTGRAY);
        } else {
            gc.setFill(Color.LIGHTSKYBLUE);
        }

        gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, CELL_SIZE, CELL_SIZE);
    }

    private void drawCellIndex(GraphicsContext gc, int x, int y, int index) {
        gc.setFont(INDEX_FONT);
        gc.setFill(Color.GRAY);
        gc.fillText(String.valueOf(index), x + 4, y + 12);
    }

    private void drawCellContent(GraphicsContext gc, int x, int y, String cellValue) {
        if (cellValue == null) {
            return;
        }

        gc.setFill(Color.BLACK);
        gc.setFont(TEXT_FONT);

        if (cellValue.equals(DELETED_MARKER)) {
            gc.fillText("X", x + (CELL_SIZE / 2.0) - 4, y + (CELL_SIZE / 2.0) + 4);
        } else {
            String shortText = getTruncatedText(cellValue);
            gc.fillText(shortText, x + 6, y + (CELL_SIZE / 2.0) + 4);
        }
    }

    private String getTruncatedText(String text) {
        final int maxCharacters = 8;
        if (text.length() > maxCharacters) {
            return text.substring(0, maxCharacters) + "..";
        }
        return text;
    }
}