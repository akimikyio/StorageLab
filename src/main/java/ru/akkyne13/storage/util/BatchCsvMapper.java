package ru.akkyne13.storage.util;

import ru.akkyne13.storage.model.Batch;

import static ru.akkyne13.storage.util.CsvStorageUtil.DATE_FORMATTER;

public class BatchCsvMapper implements CsvRowMapper<Batch> {
    @Override
    public String map(Batch item) {
        String formattedReceiptDate = item.getReceiptDate() != null ? item.getReceiptDate().format(DATE_FORMATTER) : "";

        return String.join(";",
                "NORMAL",
                item.getSku(),
                item.getName(),
                String.valueOf(item.getAmount()),
                item.getCell(),
                formattedReceiptDate,
                "", "", "", ""
        );
    }
}
