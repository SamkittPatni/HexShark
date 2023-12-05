package COMP34111_p86963sp.agents.Group888;

import java.util.*;

public class DisjointSet {
    private Map<Integer, Integer> sets;
    private Map<Integer, Integer> rank;
    private Map<Integer, Integer> valueToKeys;
    private int rows;
    private int columns;
    private int setNumber;

    // RedTop = 1000, RedBottom = 1001, BlueLeft = 1002, BlueRight = 1003
    public DisjointSet(DisjointSet set) {
        this.sets = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : set.sets.entrySet()) {
            // No need to create new instances for Integer keys and values
            this.sets.put(entry.getKey(), entry.getValue());
        }
        this.rank = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : set.rank.entrySet()) {
            // No need to create new instances for Integer keys and values
            this.rank.put(entry.getKey(), entry.getValue());
        }
        this.valueToKeys = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : set.valueToKeys.entrySet()) {
            // No need to create new instances for Integer keys and values
            this.valueToKeys.put(entry.getKey(), entry.getValue());
        }
        this.rows = set.rows;
        this.columns = set.columns;
        this.setNumber = set.setNumber;
    }

    public DisjointSet(int rows, int coloums) {
        this.sets = new HashMap<>();
        this.rank = new HashMap<>();
        this.valueToKeys = new HashMap<>();
        this.rows = rows;
        this.columns = coloums;
        this.setNumber = 0;
    }

    public int setCount() {
        return setNumber;
    }

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

    public int size() {
        return sets.size();
    }

    public void add(int x, int y) {
        int element = (x * columns) + y;
        if (!sets.containsKey(element)) {
            sets.put(element, element);
            rank.put(element, 0);
            valueToKeys.put(element, 1);
            setNumber++;
        }

        // (x-1,y) (x+1,y), (x,y-1), (x,y+1), (x-1,y+1), (x+1,y-1)

        if (x-1 >= 0 && sets.containsKey(((x-1)*columns)+y)) {
            union(element, ((x-1)*columns)+y);
        }

        if (x+1 < rows && sets.containsKey(((x+1)*columns)+y)) {
            union(element, ((x+1)*columns)+y);
        }

        if (y-1 >= 0 && sets.containsKey(((x)*columns)+y-1)) {
            union(element, ((x)*columns)+y-1);
        }

        if (y+1 < columns && sets.containsKey(((x)*columns)+y+1)) {
            union(element, ((x)*columns)+y+1);
        }

        if (x-1 >= 0 && y+1 < columns && sets.containsKey(((x-1)*columns)+y+1)) {
            union(element, ((x-1)*columns)+y+1);
        }

        if (x+1 < rows && y-1 >= 0 && sets.containsKey(((x+1)*columns)+y-1)) {
            union(element, ((x+1)*columns)+y-1);
        }

    }

    public void remove(int x, int y) {
        valueToKeys.put(find((x * columns) + y), valueToKeys.get(find((x * columns) + y)) - 1);
        sets.remove((x * columns) + y);
        rank.remove((x * columns) + y);
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

    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
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

    // public static void main(String[] args) {
    //     DisjointSet moves = new DisjointSet(11, 11);

    //     moves.add(0,2);
    //     System.out.println(moves.setCount());
    //     System.out.println(moves.getAllSets());
    //     System.out.println(moves.setSize(2));

    //     moves.add(0,3);
    //     System.out.println(moves.setCount());
    //     System.out.println(moves.getAllSets());
    //     System.out.println(moves.setSize(3));

    //     moves.add(3,3);
    //     System.out.println(moves.setCount());
    //     System.out.println(moves.getAllSets());
    //     System.out.println(moves.setSize(36));

    //     System.out.println("Test: " + moves.find(300));

    // }
}
