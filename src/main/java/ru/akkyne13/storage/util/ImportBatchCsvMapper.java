package ru.akkyne13.storage.util;

import ru.akkyne13.storage.model.ImportBatch;

import static ru.akkyne13.storage.util.CsvStorageUtil.DATE_FORMATTER;


public class ImportBatchCsvMapper implements CsvRowMapper<ImportBatch> {
    @Override
    public String map(ImportBatch item) {
        String formattedReceiptDate = item.getReceiptDate() != null ?
                item.getReceiptDate().format(DATE_FORMATTER) : "";

        return String.join(";",
                "IMPORT",
                item.getSku(),
                item.getName(),
                String.valueOf(item.getAmount()),
                item.getCell(),
                formattedReceiptDate,
                item.getCountry() != null ? item.getCountry() : "",
                item.getCustomsCode() != null ? item.getCustomsCode() : "",
                "", ""
        );
    }
}
