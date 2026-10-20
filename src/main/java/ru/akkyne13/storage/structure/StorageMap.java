package ru.akkyne13.storage.structure;

import java.util.List;

public interface StorageMap<K, V> {
    void put(K key, V value);
    V get(K key);
    boolean delete(K key);
    int size();

    List<Integer> getLastProbes();
    String[] getKeysSnapshot();
}
