package ru.akkyne13.storage.exception;

public class CsvParseException extends Exception {
    public enum CsvErrorCode {
        WRONG_COLUMN_COUNT("Неверное количество колонок"),
        BAD_NUMBER("Неверный формат числа"),
        BAD_DATE("Неверный формат даты"),
        UNKNOWN_BATCH_TYPE("Неизвестный тип партии"),
        UNKNOWN_PARSE_ERROR("Неизвестная ошибка при парсинге");

        private final String description;
        CsvErrorCode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }


    private final CsvErrorCode errorCode;
    private final int lineNumber;

    public CsvParseException(CsvErrorCode errorCode, int lineNumber) {
        super("Ошибка в строке " + lineNumber + ": " + errorCode.getDescription());
        this.errorCode = errorCode;
        this.lineNumber = lineNumber;
    }

    public CsvErrorCode getErrorCode() {
        return errorCode;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
