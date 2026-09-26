package ru.akkyne13.storage;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StorageApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        /*
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
        */
        MainView mainView = new MainView();

        Scene scene = new Scene(mainView, 1360, 760);

        primaryStage.setTitle("Storage App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
