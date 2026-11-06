import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.akkyne13.storage.io.BinSerializer;
import ru.akkyne13.storage.io.JsonSerializer;
import ru.akkyne13.storage.io.StorageData;
import ru.akkyne13.storage.model.ArchiveBatch;
import ru.akkyne13.storage.model.Batch;
import ru.akkyne13.storage.model.ImportBatch;
import ru.akkyne13.storage.model.StorageItem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SerializerTest {

    @TempDir
    Path tempDir;

    private StorageData dummyData;
    private JsonSerializer jsonSerializer;
    private BinSerializer binSerializer;

    @BeforeEach
    void setUp() {
        jsonSerializer = new JsonSerializer();
        binSerializer = new BinSerializer();

        StorageItem[] items = new StorageItem[]{
                new Batch("SKU-1", "Товар 1", 10, "A1", LocalDate.of(2026, 1, 1)),
                new ImportBatch("SKU-2", "Импорт 2", 20, "B2", LocalDate.of(2026, 2, 2), "Китай", "12345"),
                new ArchiveBatch("SKU-3", "Архив 3", 30, "C3", LocalDate.of(2026, 3, 3), LocalDate.of(2026, 4, 4), "Брак")
        };
        dummyData = new StorageData(1, items);
    }

    private Path getResourcePath(String fileName) throws Exception {
        var resource = getClass().getResource(fileName);
        assertNotNull(resource, "Файл не найден в src/test/resources: " + fileName);
        return Paths.get(resource.toURI());
    }

    // Позитивные сценарии

    @Test
    @DisplayName("json_roundtrip: Объекты должны совпадать после JSON сериализации")
    void json_roundtrip_test() throws IOException {
        Path file = tempDir.resolve("test.json");
        jsonSerializer.save(dummyData, file);

        StorageData actualData = jsonSerializer.load(file);

        assertEquals(dummyData, actualData);
    }

    @Test
    @DisplayName("bin_roundtrip: Объекты должны совпадать после бинарной сериализации")
    void bin_roundtrip_test() throws IOException {
        Path file = tempDir.resolve("test.bin");
        binSerializer.save(dummyData, file);

        StorageData actualData = binSerializer.load(file);

        assertEquals(dummyData, actualData);
    }

    @Test
    @DisplayName("empty_data_roundtrip: Пустой массив должен сериализоваться без ошибок")
    void empty_data_roundtrip_test() throws IOException {
        Path file = tempDir.resolve("empty.json");
        StorageData emptyData = new StorageData(1, new StorageItem[0]);

        jsonSerializer.save(emptyData, file);
        StorageData actualData = jsonSerializer.load(file);

        assertEquals(emptyData, actualData);
    }

    @Test
    @DisplayName("load_valid_json: Поля загруженного файла должны совпадать с ожидаемыми")
    void load_valid_json_from_resources_test() throws Exception {
        Path file = getResourcePath("/valid-data.json");

        StorageData actualData = jsonSerializer.load(file);

        assertNotNull(actualData, "Данные должны успешно загрузиться");
        assertEquals(1, actualData.getSchemaVersion(), "Версия схемы должна быть 1");
        assertEquals(3, actualData.getItems().length, "Должно быть загружено ровно 3 партии");
        assertEquals("SKU-1", actualData.getItems()[0].getSku(), "Артикул первой партии должен совпадать");
    }


    // Негативные сценарии

    @Test
    @DisplayName("load_missing_file: Чтение несуществующего файла должно вызывать IOException")
    void load_missing_file_throws_io_exception_test() {
        Path missingFile = tempDir.resolve("not_exists.json");

        assertThrows(IOException.class, () -> jsonSerializer.load(missingFile));
    }

    @Test
    @DisplayName("load_broken_json: Сломанный JSON должен вызывать IOException при парсинге")
    void load_broken_json_throws_exception_test() throws Exception {
        Path file = getResourcePath("/broken-syntax.json");

        assertThrows(IOException.class, () -> jsonSerializer.load(file));
    }

    @Test
    @DisplayName("load_broken_bin: Ложные данные должны вызывать IOException при бинарной десериализации")
    void load_broken_bin_throws_exception_test() throws Exception {
        Path file = getResourcePath("/broken.bin");

        assertThrows(IOException.class, () -> binSerializer.load(file));
    }

    @Test
    @DisplayName("json_unknown_subtype: Неизвестный тип партии должен ронять парсер с IOException")
    void json_unknown_subtype_throws_exception_test() throws Exception {
        Path file = getResourcePath("/non-existing-type.json");

        assertThrows(IOException.class, () -> jsonSerializer.load(file));
    }
}