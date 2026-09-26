package ru.akkyne13.storage;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class BatchAddView extends VBox {
    // Основные поля
    private final TextField skuField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField amountField = new TextField();
    private final TextField cellField = new TextField();
    private final DatePicker dateField = new DatePicker();

    // Дополнительные поля для ImportBatch
    private final TextField countryField = new TextField();
    private final TextField customsCodeField  = new TextField();

    // Элементы управления
    private final ComboBox<String> batchTypeChooser = new ComboBox<>();

    private final Button saveButton = new Button("Сохранить");
    private final Button cancelButton = new Button("Отмена");


    private GridPane createBatchTable() {
        GridPane editTable = new GridPane(10, 10);
        editTable.setPadding(new Insets(10));

        editTable.add(new Label("Артикул"), 0, 0);
        editTable.add(skuField, 1, 0);

        editTable.add(new Label("Наименование"),  0, 1);
        editTable.add(nameField, 1, 1);

        editTable.add(new Label("Количество"), 0, 2);
        editTable.add(amountField, 1, 2);

        editTable.add(new Label("Ячейка"), 0, 3);
        editTable.add(cellField, 1, 3);

        editTable.add(new Label("Дата поступления"), 0, 4);
        editTable.add(dateField, 1, 4);

        editTable.add(new Label("Страна-отправитель"),  0, 5);
        editTable.add(countryField, 1, 5);

        editTable.add(new Label("Таможенный код"),  0, 6);
        editTable.add(customsCodeField, 1, 6);

        return editTable;
    }


    private void createBatchTypeChooser() {
        batchTypeChooser.getItems().addAll("Обычная партия", "Импортная партия");
        batchTypeChooser.setValue("Обычная партия");
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(saveButton, cancelButton);

        return buttonBox;
    }

    private HBox createChooserBox() {
        HBox chooserBox = new HBox(10);
        chooserBox.getChildren().addAll(new Label("Тип партии: "), batchTypeChooser);

        return chooserBox;
    }

    public void setImportFieldsDisable(boolean disable) {
        countryField.setDisable(disable);
        customsCodeField.setDisable(disable);
    }


    public BatchAddView() {
        setImportFieldsDisable(true);

        // Создание объектов управления
        GridPane editTable = createBatchTable();
        createBatchTypeChooser();
        HBox buttonBox = createButtonBox();
        HBox chooserBox = createChooserBox();

        this.setPadding(new Insets(20));
        this.getChildren().addAll(chooserBox, editTable, buttonBox);
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public ComboBox<String> getBatchTypeChooser() {
        return batchTypeChooser;
    }

    public String getSku() {
        return skuField.getText();
    }

    public String getName() {
        return nameField.getText();
    }

    public String getAmountText() {
        return amountField.getText();
    }

    public String getCellText() {
        return cellField.getText();
    }

    public LocalDate getDate() {
        return dateField.getValue();
    }

    public String getCountry() {
        return countryField.getText();
    }

    public String getCustomsCode() {
        return customsCodeField.getText();
    }
}
