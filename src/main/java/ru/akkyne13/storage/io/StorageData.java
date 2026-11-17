package ru.akkyne13.storage.io;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.akkyne13.storage.exception.CsvParseException;
import ru.akkyne13.storage.model.StorageItem;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class StorageData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int CURRENT_VERSION = 3;

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
        return errors != null ? errors : new CsvParseException[0];
    }

    public void setErrors(CsvParseException[] errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StorageData that = (StorageData) o;
        return schemaVersion == that.schemaVersion && Objects.deepEquals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemaVersion, Arrays.hashCode(items));
    }
}
