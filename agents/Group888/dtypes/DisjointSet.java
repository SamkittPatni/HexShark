package agents.Group888.dtypes;

import java.util.*;

public class DisjointSet {

    private final Map<Integer, Integer> sets;
    private final Map<Integer, Integer> rank;
    private final Map<Integer, Integer> valueToKeys;
    private final int rows;
    private final int columns;
    private int setNumber;

    public DisjointSet(DisjointSet set) {

        this.sets = new HashMap<>();
        this.sets.putAll(set.sets);

        this.rank = new HashMap<>();
        this.rank.putAll(set.rank);

        this.valueToKeys = new HashMap<>();
        this.valueToKeys.putAll(set.valueToKeys);

        this.rows = set.rows;
        this.columns = set.columns;
        this.setNumber = set.setNumber;

    }

    public DisjointSet(int rows, int columns) {

        this.sets = new HashMap<>();
        this.rank = new HashMap<>();
        this.valueToKeys = new HashMap<>();

        this.rows = rows;
        this.columns = columns;
        this.setNumber = 0;

    }

    public int setCount() {
        return setNumber;
    }

    // Set size of set
    public int setSize(int set) {
        return valueToKeys.get(set);
    }

    public List<Integer> getAllSets() {

        Set<Integer> uniqueSets = new HashSet<>();

        for (int key : sets.keySet()) {
            int root = find(key);
            uniqueSets.add(root);
        }

        return new ArrayList<>(uniqueSets);
    }

    // Checks if a coordinate exists in set
    public boolean hasPoint(Point point) { return sets.containsKey(point.toKey(columns)); }

    // Obtains set of keys in set
    public Set<Integer> getKeys() {
        return sets.keySet();
    }

    // Returns size of set
    public int size() {
        return sets.size();
    }

    // Adds an element to the set
    public void add(Point point) {

        int key = point.toKey(columns);
        
        if (!hasPoint(point)) {
            sets.put(key, key);
            rank.put(key, 0);
            valueToKeys.put(key, 1);
            setNumber++;
        }

        // Check neighbours to determine whether there is a connection
        ArrayList<Point> neighbours = point.getNeighbours();
        for (Point neighbour : neighbours) {

            // If there is a connection, combine the sets together
            if (hasPoint(neighbour)) {
                union(key, neighbour.toKey(columns));
            }
        }
    }

    // Remove element from set
    public void remove(Point point){
        
        int key = point.toKey(columns);
        
        valueToKeys.put(find(key), valueToKeys.get(find(key)) - 1);
        sets.remove(key);
        rank.remove(key);
        
    }

    public int find(int x) {

        if (sets.get(x) == null) {
            return -1;
        }

        if (sets.get(x) != x) {
            sets.put(x, find(sets.get(x)));
        }

        return sets.get(x);

    }

    // Calculates the union between two sets by their key
    public void union(int keyA, int keyB) {

        int rootX = find(keyA);
        int rootY = find(keyB);

        if (rootX != rootY) {

            if (rank.get(rootX) < rank.get(rootY)) {

                sets.put(rootX, rootY);
                valueToKeys.put(rootY, valueToKeys.get(rootX) + valueToKeys.get(rootY));

            } else if (rank.get(rootX) > rank.get(rootY)) {

                sets.put(rootY, rootX);
                valueToKeys.put(rootX, valueToKeys.get(rootX) + valueToKeys.get(rootY));

            } else {

                sets.put(rootY, rootX);
                rank.put(rootX, rank.get(rootX) + 1);
                valueToKeys.put(rootX, valueToKeys.get(rootX) + valueToKeys.get(rootY));

            }

            setNumber--;

        }
    }
}
