package domain.model.dijkstra;

import java.util.HashMap;

public class DijkstraTable {
    private HashMap<Long, HashMap<Long, CellInfo>> table;

    public DijkstraTable() {
        this.table = new HashMap<>();
    }

    // Add or replace a cell with individual values
    public void put(long row, long col, double distance, long predecessor, boolean visited) {
        table.computeIfAbsent(row, r -> new HashMap<>())
                .put(col, new CellInfo(distance, predecessor, visited));
    }

    // Add or replace a cell with a CellInfo object
    public void put(long row, long col, CellInfo cellInfo) {
        table.computeIfAbsent(row, r -> new HashMap<>())
                .put(col, cellInfo);
    }

    // Retrieve a cell
    public CellInfo get(long row, long col) {
        HashMap<Long, CellInfo> rowMap = table.get(row);
        if (rowMap == null) return null;
        return rowMap.get(col);
    }

    // Check if a cell exists
    public boolean contains(long row, long col) {
        HashMap<Long, CellInfo> rowMap = table.get(row);
        return rowMap != null && rowMap.containsKey(col);
    }

    // Remove a cell
    public void remove(long row, long col) {
        HashMap<Long, CellInfo> rowMap = table.get(row);
        if (rowMap != null) {
            rowMap.remove(col);
            if (rowMap.isEmpty()) table.remove(row);
        }
    }
}