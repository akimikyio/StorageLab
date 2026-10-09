package ru.akkyne13.storage.controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.akkyne13.storage.util.AlertUtil;
import ru.akkyne13.storage.model.Batch;
import ru.akkyne13.storage.model.Editable;
import ru.akkyne13.storage.model.ImportBatch;
import ru.akkyne13.storage.model.StorageItem;
import ru.akkyne13.storage.view.BatchFormView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BatchFormController {
    private final BatchFormView view = new BatchFormView();
    private final Stage dialogStage = new Stage();
    private StorageItem resultItem = null;
    private StorageItem itemToEdit = null;

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

    private Integer parseAmount(String amountText) {
        int amount = 0;
        try {
            amount = Integer.parseInt(amountText);
        } catch (NumberFormatException exception) {
            return null;
        }

        return amount;
    }

    private Editable buildBatchFromView(Integer amount) {
        String sku = view.getSku();
        String name = view.getName();
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
            List<String> errors = new ArrayList<>();

            Integer amount = parseAmount(view.getAmountText());
            if (amount == null) {
                errors.add("Пустое количество");
            }

            Editable resultBatch = buildBatchFromView(amount == null ? 0 : amount);

            errors.addAll(resultBatch.validate());

            if (!errors.isEmpty()) {
                String errorsText = String.join("\n", errors);
                AlertUtil.showError("Ошибка ввода", errorsText);
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

    private void fillViewFromItem() {
        view.setSku(itemToEdit.getSku());
        view.setName(itemToEdit.getName());
        view.setAmount(Integer.toString(itemToEdit.getAmount()));
        view.setCell(itemToEdit.getCell());
        view.setReceiptDate(itemToEdit.getReceiptDate());

        if (itemToEdit instanceof ImportBatch) {
            view.getBatchTypeChooser().setValue("Импортная партия");
            view.setCountry(((ImportBatch) itemToEdit).getCountry());
            view.setCustomsCode(((ImportBatch) itemToEdit).getCustomsCode());
        } else {
            view.getBatchTypeChooser().setValue("Обычная партия");
        }

        view.getBatchTypeChooser().setDisable(true);
    }

    public BatchFormController() {

    }

    public BatchFormController(StorageItem item) {
        itemToEdit = item;
        fillViewFromItem();
    }
}
