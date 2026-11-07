package ru.akkyne13.storage.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class BinSerializer implements Serializer<StorageData> {

    @Override
    public String format() {
        return "bin";
    }

    @Override
    public void save(StorageData data, Path file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(file)))) {
            oos.writeObject(data);
        }
    }

    @Override
    public StorageData load(Path file) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(file)))) {
            Object obj = ois.readObject();
            if (obj instanceof StorageData data) {
                return data;
            }
            throw new IOException("Файл содержит неверный формат данных");
        } catch (ClassNotFoundException e) {
            throw new IOException("Ошибка структуры данных при чтении файла", e);
        }
    }
}
