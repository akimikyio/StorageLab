package ru.akkyne13.storage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<ArchiveBatch> archiveBatches = new ArrayList<>();
        archiveBatches.add(new ArchiveBatch("ОЗН234525472", "Чайник", 40, "AH2501",
                LocalDate.of(2026, 3, 16), LocalDate.of(2026, 9, 22),
                "Закончились товары"));

        IO.println();

        for (ArchiveBatch batch : archiveBatches) {
            IO.println(batch);
        }

        StorageApp.main(args);
    }
}
