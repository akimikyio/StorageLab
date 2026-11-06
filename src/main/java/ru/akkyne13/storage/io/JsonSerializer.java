package ru.akkyne13.storage.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Path;

public class JsonSerializer implements Serializer<StorageData> {
    private final ObjectMapper mapper;

    public JsonSerializer() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public String format() {
        return "json";
    }

    @Override
    public void save(StorageData data, Path file) throws IOException {
        mapper.writeValue(file.toFile(), data);
    }

    @Override
    public StorageData load(Path file) throws IOException {
        return mapper.readValue(file.toFile(), StorageData.class);
    }
}
