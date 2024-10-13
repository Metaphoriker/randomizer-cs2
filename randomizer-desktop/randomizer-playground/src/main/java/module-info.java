module de.metaphoriker.randomizer.playground {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires javafx.base;
  requires java.desktop;

  opens de.metaphoriker to
      javafx.fxml;

  exports de.metaphoriker;
  exports de.metaphoriker.util;

  opens de.metaphoriker.util to
      javafx.fxml;

  exports de.metaphoriker.view;

  opens de.metaphoriker.view to
      javafx.fxml;
    exports de.metaphoriker.view.views;
    opens de.metaphoriker.view.views to javafx.fxml;
}
