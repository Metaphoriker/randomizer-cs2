package com.revortix.randomizer.ui.view.controller;

import com.google.inject.Inject;
import com.revortix.randomizer.ui.RandomizerApplication;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.viewmodel.HomeViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@View
public class HomeViewController {

    private final HomeViewModel homeViewModel;

    @Inject
    public HomeViewController(HomeViewModel homeViewModel) {
        this.homeViewModel = homeViewModel;
    }

    @FXML
    void onDiscordOpen(MouseEvent event) {
        try {
            homeViewModel.openDiscord();
        } catch (IOException e) {
            log.error("Error opening discord", e);
            showAlertInternetConnection();
        }
    }

    @FXML
    void onGitHubOpen(MouseEvent event) {
        try {
            homeViewModel.openGitHub();
        } catch (IOException e) {
            log.error("Error opening github", e);
            showAlertInternetConnection();
        }
    }

    private void showAlertInternetConnection() {
        Platform.runLater(() -> {
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add(RandomizerApplication.class.getResource("alert-style.css").toExternalForm());
            alert.setTitle("Internet Connectivity Issue");
            alert.setHeaderText(null);
            alert.setContentText("Please check your internet connection and try again.");
            alert.showAndWait();
        });
    }
}