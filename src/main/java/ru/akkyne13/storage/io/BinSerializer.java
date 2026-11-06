package ru.akkyne13.storage.io;

import java.io.*;
import java.nio.file.Path;

public class BinSerializer implements Serializer<StorageData> {

    @Override
    public String format() {
        return "bin";
    }

    @Override
    public void save(StorageData data, Path file) throws IOException {
        try (
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                        new FileOutputStream(file.toFile())
                )
        ) {
            objectOutputStream.writeObject(data);
        }
    }

    @Override
    public StorageData load(Path file) throws IOException {
        try(
                ObjectInputStream objectInputStream = new ObjectInputStream(
                        new FileInputStream(file.toFile())
                )
        ) {
            return (StorageData) objectInputStream.readObject();
        } catch(ClassNotFoundException e) {
            throw new IOException("Класс не найден при десериализации", e);
        }
    }
}
