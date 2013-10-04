package com.ergle;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class DialogController {

    @FXML
    private Stage stage;

    @FXML
    public void dismiss() {
        stage.close();
    }
}
