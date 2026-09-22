package ru.akkyne13.storage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main() {
        List<Batch> batches = new ArrayList<>();
        batches.add(new Batch("ОЗН501256023", "Попрыгунчик", 300, "CY1725", LocalDate.of(2026, 4, 7)));
        batches.add(new Batch("ОЗН234525472", "Чайник", 40, "AH2501", LocalDate.of(2026, 3, 16)));

        List<ImportBatch> importBatches = new ArrayList<>();
        importBatches.add(new ImportBatch("ОЗН501256023", "Попрыгунчик", 300, "CY1725",
                LocalDate.of(2026, 4, 7), "China", "HZ322228"));

        List<ArchiveBatch> archiveBatches = new ArrayList<>();
        archiveBatches.add(new ArchiveBatch("ОЗН234525472", "Чайник", 40, "AH2501",
                LocalDate.of(2026, 3, 16), LocalDate.of(2026, 9, 22),
                "Закончились товары"));

        for (Batch batch : batches) {
            IO.println(batch);
        }

        IO.println();

        for (ImportBatch batch : importBatches) {
            IO.println(batch);
        }

        IO.println();

        for (ArchiveBatch batch : archiveBatches) {
            IO.println(batch);
        }
    }
}
