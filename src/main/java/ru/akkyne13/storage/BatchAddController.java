package ru.akkyne13.storage;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class BatchAddController {
    private final BatchAddView view = new BatchAddView();
    private final Stage dialogStage = new Stage();
    private StorageItem resultItem = null;

    // TODO: Разнести функционал отображения, логики и обработки ошибок в разные функции
    private void setupBatchTypeChooser() {
        ComboBox<String> batchTypeChooser = view.getBatchTypeChooser();
        batchTypeChooser.setOnAction((e) -> {
            String currentValue = batchTypeChooser.getValue();

            if (currentValue.equals("Обычная партия")) {
                view.setImportFieldsDisable(true);
            } else {
                view.setImportFieldsDisable(false);
            }
        });
    }

    private void setupCancelButton() {
        Button cancelButton = view.getCancelButton();
        cancelButton.setOnAction((e) -> {
            dialogStage.close();
        });
    }

    // TODO: вынести сборку объекта в отдельный метод
    private void setupSaveButton() {
        Button saveButton = view.getSaveButton();
        saveButton.setOnAction((e) -> {
            String sku = view.getSku();
            String name = view.getName();
            String amountText = view.getAmountText();
            String cell = view.getCell();
            LocalDate receiptDate = view.getReceiptDate();
            String country = null;
            String customsCode = null;

            Integer amount = parseAmount(amountText);
            if (amount == null) {
                return;
            }

            String batchType = view.getBatchTypeChooser().getValue();
            Editable resultBatch;

            if (batchType.equals("Импортная партия")) {
                country = view.getCountry();
                customsCode = view.getCustomsCode();

                resultBatch = new ImportBatch(sku, name, amount, cell, receiptDate, country, customsCode);
            } else {
                resultBatch = new Batch(sku, name, amount, cell, receiptDate);
            }


            List<String> errorsList = resultBatch.validate();
            if (!errorsList.isEmpty()) {
                String errorsText = String.join("\n", errorsList);
                showErrorAlert("Ошибка ввода", errorsText);
                return;
            }

            resultItem = (StorageItem) resultBatch;

            dialogStage.close();
        });
    }

    private void setupListeners() {
        setupBatchTypeChooser();
        setupCancelButton();
        setupSaveButton();
    }

    public StorageItem showDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);


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


        ComboBox<String> batchTypeChooser = new ComboBox<>();
        batchTypeChooser.getItems().addAll("Обычная партия", "Импортная партия");
        batchTypeChooser.setValue("Обычная партия");


        countryField.setDisable(true);
        customsCodeField.setDisable(true);

        batchTypeChooser.setOnAction(event -> {
            String currentValue = batchTypeChooser.getValue();
            if (currentValue.equals("Обычная партия")) {
                countryField.setDisable(true);
                customsCodeField.setDisable(true);
            } else {
                countryField.setDisable(false);
                customsCodeField.setDisable(false);
            }
        });


        Button saveButton = new Button("Сохранить");
        saveButton.setOnAction(event -> {
            String sku = skuField.getText();
            String name = nameField.getText();
            String country;
            int amount = 0;
            String customsCode;
            String cell = cellField.getText();
            LocalDate receiptDate = dateField.getValue();

            try {
                amount = Integer.parseInt(amountField.getText());
            } catch (NumberFormatException e) {
                Alert numberAlert = new Alert(Alert.AlertType.INFORMATION);
                numberAlert.setTitle("Ошибка ввода");
                numberAlert.setHeaderText(null);
                numberAlert.setContentText("В поле 'Количество' должно быть введено целое число.");
                numberAlert.showAndWait();
                return;
            }

            String batchType = batchTypeChooser.getValue();
            Editable resultBatch;

            if (batchType.equals("Импортная партия")) {
                country = countryField.getText();
                customsCode = customsCodeField.getText();

                resultBatch = new ImportBatch(sku, name, amount, cell, receiptDate, country, customsCode);
            } else {
                resultBatch = new Batch(sku, name, amount, cell, receiptDate);
            }


            List<String> errorsList = resultBatch.validate();
            if (!errorsList.isEmpty()) {
                String errorsText = String.join("\n", errorsList);
                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setHeaderText(null);
                warningAlert.setContentText(errorsText);
                warningAlert.showAndWait();
                return;
            }


            resultItem = (StorageItem) resultBatch;

            dialogStage.close();
        });

        Button cancelButton = new Button("Отмена");
        cancelButton.setOnAction(event -> {
            dialogStage.close();
        });

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(saveButton, cancelButton);

        HBox chooserBox = new HBox(10);
        chooserBox.getChildren().addAll(new Label("Тип партии: "), batchTypeChooser);

        VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(20));
        mainVBox.getChildren().addAll(chooserBox, editTable, buttonBox);


        Scene scene = new Scene(mainVBox);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();


        return resultItem;
    }
}
