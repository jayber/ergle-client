package com.ergle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class Controller {

    @FXML
    private Stage stage;
    @FXML
    private ListView watchedPaths;

    public void initialize() {
        loadListData();
    }

    @FXML
    private void loadListData() {
//        watchedPaths.getItems().addAll("path");
    }

    @FXML
    private void browse() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Browse directories");
        final File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            if (selectedDirectory.isDirectory()) {
                watchedPaths.getItems().addAll(selectedDirectory.getAbsolutePath());
            }
        } else {
            Stage dialog = new Stage();
            dialog.setTitle("Warning");
            dialog.initStyle(StageStyle.UTILITY);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("dialog.fxml"));
            Parent parent = (Parent) loader.load();
            final DialogController controller = loader.getController();
            controller.setStage(dialog);
            dialog.setScene(new Scene(parent));
            dialog.show();
        }

    }

}
