package ru.akkyne13.storage;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class BatchFormController {
    private final BatchFormView view = new BatchFormView();
    private final Stage dialogStage = new Stage();
    private StorageItem resultItem = null;

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

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Integer parseAmount(String amountText) {
        int amount = 0;
        try {
            amount = Integer.parseInt(amountText);
        } catch (NumberFormatException exception) {
            showErrorAlert(
                    "Ошибка ввода",
                    "В поле 'Количество' должно быть введено целое неотрицательно число"
            );
            return null;
        }

        return amount;
    }

    private Editable buildBatchFromView(Integer amount) {
        String sku = view.getSku();
        String name = view.getName();
        String amountText = view.getAmountText();
        String cell = view.getCell();
        LocalDate receiptDate = view.getReceiptDate();
        String country = null;
        String customsCode = null;

        String batchType = view.getBatchTypeChooser().getValue();
        Editable resultBatch;

        if (batchType.equals("Импортная партия")) {
            country = view.getCountry();
            customsCode = view.getCustomsCode();

            resultBatch = new ImportBatch(sku, name, amount, cell, receiptDate, country, customsCode);
        } else {
            resultBatch = new Batch(sku, name, amount, cell, receiptDate);
        }

        return resultBatch;
    }

    private void setupSaveButton() {
        Button saveButton = view.getSaveButton();
        saveButton.setOnAction((e) -> {
            Integer amount = parseAmount(view.getAmountText());
            if (amount == null) {
                return;
            }

            Editable resultBatch = buildBatchFromView(amount);

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
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        setupListeners();

        Scene scene = new Scene(view);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();

        return resultItem;
    }
}
