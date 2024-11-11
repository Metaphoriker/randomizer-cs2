package com.revortix.randomizer.ui.view.controller;

import com.revortix.randomizer.ui.view.View;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

@View
public class HomeViewController {

    @FXML
    private AnchorPane root;

    public HomeViewController() {
        Platform.runLater(this::initialize);
    }

    private void initialize() {
    }
}