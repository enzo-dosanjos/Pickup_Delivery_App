package domain.model.dijkstra;

import java.util.HashMap;

/**
 * Represents a table structure for storing and managing {@link CellInfo} objects
 * in a Dijkstra algorithm. The table is organized as a map of rows, where each
 * row contains a map of columns associated with {@link CellInfo} objects.
 */
public class DijkstraTable {
    private HashMap<Long, HashMap<Long, CellInfo>> table; // The underlying data structure for the table

    /**
     * Constructs a new, empty DijkstraTable.
     */
    public DijkstraTable() {
        this.table = new HashMap<>();
    }

    /**
     * Adds or replaces a cell in the table using individual values.
     *
     * @param row         the row index of the cell
     * @param col         the column index of the cell
     * @param duration    the duration or cost associated with the cell
     * @param predecessor the predecessor node ID in the path
     * @param visited     whether the cell has been visited
     */
    public void put(long row, long col, double duration, long predecessor, boolean visited) {
        table.computeIfAbsent(row, r -> new HashMap<>())
                .put(col, new CellInfo(duration, predecessor, visited));
    }

    /**
     * Adds or replaces a cell in the table using a {@link CellInfo} object.
     *
     * @param row      the row index of the cell
     * @param col      the column index of the cell
     * @param cellInfo the {@link CellInfo} object to store
     */
    public void put(long row, long col, CellInfo cellInfo) {
        table.computeIfAbsent(row, r -> new HashMap<>())
                .put(col, cellInfo);
    }

    /**
     * Retrieves a cell from the table.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @return the {@link CellInfo} object at the specified row and column,
     *         or null if no such cell exists
     */
    public CellInfo get(long row, long col) {
        HashMap<Long, CellInfo> rowMap = table.get(row);
        if (rowMap == null) return null;
        return rowMap.get(col);
    }

    /**
     * Checks if a cell exists in the table.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @return true if the cell exists, false otherwise
     */
    public boolean contains(long row, long col) {
        HashMap<Long, CellInfo> rowMap = table.get(row);
        return rowMap != null && rowMap.containsKey(col);
    }

    /**
     * Removes a cell from the table.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     */
    public void remove(long row, long col) {
        HashMap<Long, CellInfo> rowMap = table.get(row);
        if (rowMap != null) {
            rowMap.remove(col);
            if (rowMap.isEmpty()) table.remove(row);
        }
    }
}