package ru.akkyne13.storage.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.time.LocalDate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Batch.class, name = "NORMAL"),
        @JsonSubTypes.Type(value = ImportBatch.class, name = "IMPORT"),
        @JsonSubTypes.Type(value = ArchiveBatch.class, name = "ARCHIVE")
})

public interface StorageItem extends Serializable {
    String getSku();
    String getName();
    int getAmount();
    String getCell();
    LocalDate getReceiptDate();
}
