package ru.akkyne13.storage.structure;


public interface StorageMap<K, V> {
    void put(K key, V value);
    V get(K key);
    boolean delete(K key);
    void clear();
    int size();

    MapSnapshot getSnapshot();
}
