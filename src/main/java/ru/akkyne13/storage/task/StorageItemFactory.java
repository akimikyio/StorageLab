package ru.akkyne13.storage.task;

import ru.akkyne13.storage.model.ArchiveBatch;
import ru.akkyne13.storage.model.Batch;
import ru.akkyne13.storage.model.ImportBatch;
import ru.akkyne13.storage.model.StorageItem;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class StorageItemFactory {
    private static final String[] COUNTRIES = { "Китай", "Турция", "Беларусь", "Индия", "Узбекистан" };

    public static StorageItem createRandomItem(int index, String sku) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        String name = "Товар " + index;
        int amount = random.nextInt(100) + 1;
        String cell = (char) (random.nextInt(26) + 'a') + String.valueOf(random.nextInt(50));
        LocalDate receiptDate = LocalDate.now().minusDays(random.nextInt(365));

        int type = random.nextInt(3);

        return switch (type) {
            case 0 -> new Batch(sku, name, amount, cell, receiptDate);
            case 1 -> {
                String country = COUNTRIES[random.nextInt(COUNTRIES.length)];
                String customsCode = String.valueOf(random.nextInt(1000000001));
                yield new ImportBatch(sku, name, amount, cell, receiptDate, country, customsCode);
            }
            case 2 -> {
                LocalDate archiveDate = LocalDate.now().minusDays(random.nextInt(365));
                String archiveReason = "Запретили в стране " + index + " раз";
                yield new ArchiveBatch(sku, name, amount, cell, receiptDate, archiveDate, archiveReason);
            }
            default -> throw new IllegalStateException("Неизвестный тип: " + type);
        };
    }
}
