package ru.akkyne13.storage.exception;

import java.io.IOException;

public class SchemaMigrationException extends IOException {
    public enum SchemaMigrationErrorCode {
        VERSION_TOO_NEW("Версия файла выше текущей поддерживаемой"),
        INVALID_VERSION("Неверная версия файла"),
        MISSING_FIELD("Пропущено необходимое поле"),
        INVALID_FIELD_VALUE("Неизвестное значение поля");

        private final String description;
        SchemaMigrationErrorCode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final SchemaMigrationErrorCode errorCode;

    public SchemaMigrationException(SchemaMigrationErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public SchemaMigrationException(SchemaMigrationErrorCode errorCode, String details) {
        this(errorCode, details, null);
    }

    public SchemaMigrationException(SchemaMigrationErrorCode errorCode, String details, Throwable cause) {
        super(details == null || details.isBlank()
                ? errorCode.getDescription()
                : errorCode.getDescription() + ": " + details, cause);
        this.errorCode = errorCode;
    }

    public SchemaMigrationErrorCode getErrorCode() {
        return errorCode;
    }
}
