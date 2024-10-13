module de.metaphoriker.randomizer.playground {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires javafx.base;
  requires randomizer.model;
  requires com.google.guice;
  requires java.desktop;
  requires static lombok;
  requires jnativehook;
  requires org.slf4j;

  opens de.metaphoriker to
      javafx.fxml,
      com.google.guice;

  exports de.metaphoriker;
  exports de.metaphoriker.util;

  opens de.metaphoriker.util to
      javafx.fxml;

  exports de.metaphoriker.view;

  opens de.metaphoriker.view to
      javafx.fxml;

  exports de.metaphoriker.view.views;

  opens de.metaphoriker.view.views to
      javafx.fxml;

  exports de.metaphoriker.view.viewmodel;

  opens de.metaphoriker.view.viewmodel to
      javafx.fxml;
}
