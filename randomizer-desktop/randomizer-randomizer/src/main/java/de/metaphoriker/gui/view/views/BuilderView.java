package de.metaphoriker.gui.view.views;

import com.google.gson.JsonSyntaxException;
import de.metaphoriker.gui.component.EventComponent;
import de.metaphoriker.gui.util.CSSUtil;
import de.metaphoriker.gui.util.ImageUtil;
import de.metaphoriker.gui.view.View;
import de.metaphoriker.gui.view.models.BuilderViewModel;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class BuilderView extends View<BuilderViewModel> {

  @FXML private VBox clusterBuilderVBox;

  @FXML private ListView<Label> clusterListView;

  @FXML private FlowPane eventFlowPane;

  @FXML private Button randomizerButton;

  @FXML private Button saveButton;

  @FXML private Button clearButton;

  @FXML private Button deleteClusterButton;

  @FXML private ScrollPane clusterVBoxScrollPane;

  @FXML private Label totalClusterLabel;

  public BuilderView(BuilderViewModel viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    setGraphics();
    fillEventHBox();
    refreshCluster();
  }

  @FXML
  public void onRandomize(ActionEvent actionEvent) {

    clusterBuilderVBox.getChildren().clear();

    for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, 10); i++) {

      EventComponent eventComponent = new EventComponent(getViewModel().getRandomEvent());
      setupDragAlreadyDropped(eventComponent);

      clusterBuilderVBox.getChildren().add(eventComponent);
    }
  }

  @FXML
  public void onSave(ActionEvent actionEvent) {

    if (clusterBuilderVBox.getChildren().isEmpty()) return;

    StringBuilder stringBuilder = new StringBuilder();
    clusterBuilderVBox.getChildren().stream()
        .map(EventComponent.class::cast)
        .forEach(
            eventComponent -> {
              eventComponent.apply();
              stringBuilder
                  .append(getViewModel().serialize(eventComponent.getRepresent()))
                  .append(";");
            });

    String clusterName = awaitClusterName();
    if (clusterName == null) return;

    getViewModel().saveCluster(clusterName, stringBuilder.toString());

    refreshCluster();
    clusterBuilderVBox.getChildren().clear();
  }

  @FXML
  public void onClear(ActionEvent actionEvent) {
    clusterBuilderVBox.getChildren().clear();
  }

  @FXML
  private void onDeleteCluster(ActionEvent actionEvent) {

    Label selectedCluster = clusterListView.getSelectionModel().getSelectedItem();

    if (selectedCluster == null) return;

    refreshCluster();

    clusterBuilderVBox.getChildren().clear();
  }

  private void refreshCluster() {

    clusterListView.getItems().clear();

    getViewModel()
        .loadEventClusters()
        .forEach(
            eventCluster -> {
              Label label =
                  new Label(
                      eventCluster.getName(),
                      ImageUtil.getImageView(
                          "images/bundle_icon.png", ImageUtil.ImageResolution.SMALL));

              clusterListView.getItems().add(label);
            });

    totalClusterLabel.setText(String.valueOf(clusterListView.getItems().size()));
  }

  private void setupDrag(EventComponent node) {
    node.setOnDragDetected(
        dragEvent -> {
          node.apply();

          Dragboard dragboard = node.startDragAndDrop(TransferMode.ANY);
          dragboard.setDragView(node.snapshot(null, null), dragEvent.getX(), dragEvent.getY());

          ClipboardContent content = new ClipboardContent();
          content.putString(getViewModel().serialize(node.getRepresent()));

          dragboard.setContent(content);
          dragEvent.consume();
        });
  }

  // TODO: BOILERPLATE
  private void setupDrag(Label node) {
    node.setOnDragDetected(
        dragEvent -> {
          Dragboard dragboard = node.startDragAndDrop(TransferMode.ANY);
          dragboard.setDragView(node.snapshot(null, null), dragEvent.getX(), dragEvent.getY());

          ClipboardContent content = new ClipboardContent();
          content.putString(getViewModel().serialize(getViewModel().getEvent(node.getText())));

          dragboard.setContent(content);
          dragEvent.consume();
        });
  }

  private void setupDragAlreadyDropped(EventComponent node) {

    setupDrag(node);
    node.setCursor(Cursor.OPEN_HAND);

    node.setOnDragDropped(
        dragEvent -> {
          Dragboard dragboard = dragEvent.getDragboard();
          boolean success = false;

          if (dragboard.hasString()) {

            int index = clusterBuilderVBox.getChildren().indexOf(node);

            // offset -1 because then it gets set before the separator which gets removed in the
            // next step
            EventComponent eventComponent =
                new EventComponent(getViewModel().deserialize(dragboard.getString()));
            clusterBuilderVBox.getChildren().add(index - 1, eventComponent);

            setupDragAlreadyDropped(eventComponent);

            success = true;
          }

          dragEvent.setDropCompleted(success);
          dragEvent.consume();
        });

    node.setOnDragEntered(
        dragEvent -> {
          Separator separator = new Separator();
          clusterBuilderVBox
              .getChildren()
              .add(clusterBuilderVBox.getChildren().indexOf(node), separator);
        });

    node.setOnDragExited(
        dragEvent ->
            clusterBuilderVBox
                .getChildren()
                .remove(clusterBuilderVBox.getChildren().indexOf(node) - 1));

    node.setOnDragDone(
        dragEvent -> {
          clusterBuilderVBox.getChildren().remove(node);
          dragEvent.consume();
        });
  }

  private void setupDrop(Region region) {

    region.setOnDragOver(
        dragEvent -> {
          if (dragEvent.getGestureSource() != region && dragEvent.getDragboard().hasString())
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);

          dragEvent.consume();
        });

    region.setOnDragDropped(
        dragEvent -> {
          Dragboard dragboard = dragEvent.getDragboard();
          boolean success = false;

          if (dragboard.hasString()) {

            try {

              EventComponent eventComponent =
                  new EventComponent(getViewModel().deserialize(dragboard.getString()));
              setupDragAlreadyDropped(eventComponent);

              clusterBuilderVBox.getChildren().add(eventComponent);

              success = true;
            } catch (JsonSyntaxException ignored) { // this could be probably done better
            }
          }

          dragEvent.setDropCompleted(success);
          dragEvent.consume();
        });
  }

  private void setGraphics() {

    getIcons().add(ImageUtil.getImage("images/build_icon.png"));

    randomizerButton.setGraphic(
        ImageUtil.getImageView("images/shuffle_icon.png", ImageUtil.ImageResolution.OKAY));
    saveButton.setGraphic(
        ImageUtil.getImageView("images/plus_icon.png", ImageUtil.ImageResolution.OKAY));
    clearButton.setGraphic(
        ImageUtil.getImageView("images/delete_icon.png", ImageUtil.ImageResolution.OKAY));
    deleteClusterButton.setGraphic(
        ImageUtil.getImageView("images/delete_icon.png", ImageUtil.ImageResolution.OKAY));
  }

  private void fillEventHBox() {

    setupDrop(clusterVBoxScrollPane);

    getViewModel()
        .getEvents()
        .forEach(
            event -> {
              Label label = new Label(event.name());
              label.setCursor(Cursor.OPEN_HAND);
              label.setContentDisplay(ContentDisplay.RIGHT);
              label.setPadding(new Insets(0, 0, 0, 5));
              label.setTooltip(new Tooltip(event.description()));
              label.setGraphic(
                  ImageUtil.getImageView("images/drag_icon.png", ImageUtil.ImageResolution.SMALL));
              label.getGraphic().setOpacity(0);

              label.setOnMouseEntered(mouseEvent -> label.getGraphic().setOpacity(1));
              label.setOnMouseExited(mouseEvent -> label.getGraphic().setOpacity(0));

              setupDrag(label);

              eventFlowPane.getChildren().add(label);
            });
  }

  private String awaitClusterName() {
    while (true) {

      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Cluster Name");
      dialog.setHeaderText("Enter a name for the cluster");
      dialog.setContentText("Name:");

      CSSUtil.applyTheme(dialog.getDialogPane().getScene(), CSSUtil.Theme.MODENA_DARK);

      Optional<String> result = dialog.showAndWait();
      if (!result.isPresent()) return null;

      if (dialog.getResult().isEmpty()
          || getViewModel().getClusters().stream()
              .anyMatch(eventCluster -> eventCluster.getName().equals(dialog.getResult()))) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText("Cluster name is empty or already exists!");

        CSSUtil.applyTheme(alert.getDialogPane().getScene(), CSSUtil.Theme.MODENA_DARK);

        alert.showAndWait();

        continue;
      }

      return dialog.getResult();
    }
  }
}
