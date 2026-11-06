package ru.akkyne13.storage.io;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.akkyne13.storage.exception.CsvParseException;
import ru.akkyne13.storage.model.StorageItem;

import java.io.Serial;
import java.io.Serializable;

public class StorageData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int schemaVersion = 1;
    private StorageItem[] items;

    @JsonIgnore
    private transient CsvParseException[] errors = new CsvParseException[0];

    public StorageData() { }

    public StorageData(int schemaVersion, StorageItem[] items) {
        this.schemaVersion = schemaVersion;
        this.items = items;
    }

    public StorageData(int schemaVersion, StorageItem[] items, CsvParseException[] errors) {
        this.schemaVersion = schemaVersion;
        this.items = items;
        this.errors = errors;
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public StorageItem[] getItems() {
        return items;
    }

    public void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public void setItems(StorageItem[] items) {
        this.items = items;
    }

    public CsvParseException[] getErrors() {
        return errors;
    }

    public void setErrors(CsvParseException[] errors) {
        this.errors = errors;
    }
}
