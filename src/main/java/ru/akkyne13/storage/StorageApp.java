package ru.akkyne13.storage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.akkyne13.storage.controller.MainController;
import ru.akkyne13.storage.view.MainView;

public class StorageApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        MainView mainView = new MainView();
        MainController mainController = new MainController(mainView);

        Scene scene = new Scene(mainView, 1360, 760);

        primaryStage.setTitle("Управление складом");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
