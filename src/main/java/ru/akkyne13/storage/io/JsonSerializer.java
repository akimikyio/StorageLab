package ru.akkyne13.storage.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonSerializer implements Serializer<StorageData> {
    private final ObjectMapper mapper;

    public JsonSerializer() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public String format() {
        return "json";
    }

    @Override
    public void save(StorageData data, Path file) throws IOException {
        try (var writer = Files.newBufferedWriter(file)) {
            mapper.writeValue(writer, data);
        }
    }

    @Override
    public StorageData load(Path file) throws IOException {
        try (var reader = Files.newBufferedReader(file)) {
            return mapper.readValue(reader, StorageData.class);
        } catch (JsonProcessingException e) {
            int line = e.getLocation() != null ? e.getLocation().getLineNr() : -1;
            throw new IOException("Повреждён синтаксис или структура JSON (ошибка на строке " + line + ")", e);
        }
    }
}
