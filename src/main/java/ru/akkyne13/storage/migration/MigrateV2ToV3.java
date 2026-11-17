package ru.akkyne13.storage.migration;

import ru.akkyne13.storage.exception.SchemaMigrationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class MigrateV2ToV3 implements SchemaStep {
    @Override
    public int fromVersion() {
        return 2;
    }

    @Override
    public int toVersion() {
        return 3;
    }

    @Override
    public RawStorageRecord apply(RawStorageRecord record) throws SchemaMigrationException {
        RawStorageRecord copy = record.copy();

        copy.getRequired("note");

        String receiptDateStr = copy.getRequiredNonBlank("receiptDate");
        try {
            LocalDate receiptDate = LocalDate.parse(receiptDateStr);
            copy.putIfAbsent("updatedAt", receiptDate.atStartOfDay().toString());
        } catch (DateTimeParseException e) {
            throw new SchemaMigrationException(
                    SchemaMigrationException.SchemaMigrationErrorCode.INVALID_FIELD_VALUE,
                    "некорректный формат даты receiptDate '" + receiptDateStr + "'", e
            );
        }

        return copy;
    }
}
