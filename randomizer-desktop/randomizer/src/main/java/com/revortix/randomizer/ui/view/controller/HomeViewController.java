package com.revortix.randomizer.ui.view.controller;

import com.google.inject.Inject;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.viewmodel.HomeViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

@View
public class HomeViewController {

    private final HomeViewModel homeViewModel;

    @FXML
    private AnchorPane root;

    @Inject
    public HomeViewController(HomeViewModel homeViewModel) {
        this.homeViewModel = homeViewModel;
        Platform.runLater(this::initialize);
    }

    private void initialize() {
    }
}