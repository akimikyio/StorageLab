package ru.akkyne13.storage.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public interface Serializer<T> {
    String format();
    void save(T data, Path file) throws IOException;
    T load(Path file) throws IOException;

    default T load(Path file, BiConsumer<Integer, Integer> progressCallback) throws IOException {
        return load(file);
    }
}
