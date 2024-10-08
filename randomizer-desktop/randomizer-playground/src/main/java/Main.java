
import de.metaphoriker.gui.util.ImageUtil;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application {

    @FXML private Label randomizerIcon;
    @FXML private Label logbookIcon;
    @FXML private Label gameIcon;
    @FXML private Label discordIcon;
    @FXML private Label websiteIcon;

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            setUpGraphics();

            stage.setScene(scene);
            stage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpGraphics() {
        randomizerIcon.setGraphic(ImageUtil.getImageView("randomizerIcon.png"));
        logbookIcon.setGraphic(ImageUtil.getImageView("icons/logbookIcon.png"));
        gameIcon.setGraphic(ImageUtil.getImageView("icons/gameIcon.png"));
        discordIcon.setGraphic(ImageUtil.getImageView("icons/discordIcon.png"));
        websiteIcon.setGraphic(ImageUtil.getImageView("icons/websiteIcon.png"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
