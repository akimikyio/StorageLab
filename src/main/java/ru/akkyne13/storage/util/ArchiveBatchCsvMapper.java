package ru.akkyne13.storage.util;

import ru.akkyne13.storage.model.ArchiveBatch;

import static ru.akkyne13.storage.util.CsvStorageUtil.DATE_FORMATTER;


public class ArchiveBatchCsvMapper implements CsvRowMapper<ArchiveBatch> {
    @Override
    public String map(ArchiveBatch item) {
        String formattedReceiptDate = item.getReceiptDate() != null ?
                item.getReceiptDate().format(DATE_FORMATTER) : "";
        String formattedArchiveDate = item.archiveDate() != null ?
                item.archiveDate().format(DATE_FORMATTER) : "";

        return String.join(";",
                "ARCHIVE",
                item.getSku(),
                item.getName(),
                String.valueOf(item.getAmount()),
                item.getCell(),
                formattedReceiptDate,
                "", "",
                formattedArchiveDate,
                item.archiveReason() != null ? item.archiveReason() : ""
        );
    }
}
