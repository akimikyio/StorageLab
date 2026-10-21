package ru.akkyne13.storage.structure;


public class OpenAddressingHashMap<K, V> implements StorageMap<K, V> {
    private static class Entry<K, V> {
        K key;
        V value;
        boolean isDeleted;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.isDeleted = false;
        }
    }

    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<K, V>[] table;
    private int size;

    private int[] lastProbes;
    private int lastProbesCount;

    @SuppressWarnings("unchecked")
    public OpenAddressingHashMap() {
        table = (Entry<K, V>[]) new Entry[INITIAL_CAPACITY];
        size = 0;

        this.lastProbes = new int[INITIAL_CAPACITY];
        this.lastProbesCount = 0;
    }

    private void clearProbes() {
        this.lastProbesCount = 0;
    }

    private void addProbe(int index) {
        if (lastProbesCount < lastProbes.length) {
            lastProbes[lastProbesCount] = index;
            lastProbesCount++;
        }
    }


    @Override
    public synchronized boolean[] getLastProbes() {
        boolean[] probes = new boolean[table.length];
        for (int i = 0; i < lastProbesCount; i++) {
            int visitedIndex = lastProbes[i];
            probes[visitedIndex] = true;
        }
        return probes;
    }

    private int hash(K key, int capacity) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    private int hash(K key) {
        return hash(key, table.length);
    }

    @Override
    public synchronized int size() {
        return size;
    }

    @Override
    public synchronized void put(K key, V value) {
        if (size >= table.length * LOAD_FACTOR) {
            resize();
        }

        clearProbes();

        int index = hash(key);
        int firstDeletedIndex = -1;

        while(table[index] != null) {
            addProbe(index);

            if (!table[index].isDeleted && table[index].key.equals(key)) {
                table[index].value = value;
                return;
            }

            if (table[index].isDeleted && firstDeletedIndex == -1) {
                firstDeletedIndex = index;
            }

            index = (index + 1) % table.length;
        }

        addProbe(index);

        int targetIndex = (firstDeletedIndex != -1) ? firstDeletedIndex : index;

        table[targetIndex] = new Entry<>(key, value);
        size++;
    }

    @Override
    public synchronized V get(K key) {
        clearProbes();

        int index = hash(key);

        while(table[index] != null) {
            addProbe(index);

            if (!table[index].isDeleted && table[index].key.equals(key)) {
                return table[index].value;
            }

            index = (index + 1) % table.length;
        }

        addProbe(index);

        return null;
    }

    @Override
    public synchronized boolean delete(K key) {
        clearProbes();
        int index = hash(key);

        while (table[index] != null) {
            addProbe(index);

            if (!table[index].isDeleted && table[index].key.equals(key)) {
                table[index].isDeleted = true;
                size--;

                return true;
            }

            index = (index + 1) % table.length;
        }

        addProbe(index);
        return false;
    }

    @SuppressWarnings("unchecked")
    private synchronized void resize() {
        Entry<K, V>[] oldTable = table;

        int newCapacity = oldTable.length * 2;
        Entry<K, V>[] newTable = (Entry<K, V>[]) new Entry[newCapacity];

        for (Entry<K, V> entry : oldTable) {
            if (entry != null && !entry.isDeleted) {
                int newIndex = hash(entry.key, newCapacity);

                while (newTable[newIndex] != null) {
                    newIndex = (newIndex + 1) % newCapacity;
                }
                newTable[newIndex] = entry;
            }
        }

        this.table = newTable;
        this.lastProbes = new int[newCapacity];
    }

    @Override
    public synchronized String[] getKeysSnapshot() {
        String[] snapshot = new String[table.length];
        for (int i = 0; i < table.length; i++) {
            if (table[i] == null) {
                snapshot[i] = null;
            } else if (table[i].isDeleted) {
                snapshot[i] = "__DELETED__";
            } else {
                snapshot[i] = table[i].key.toString();
            }
        }
        return snapshot;
    }
}
