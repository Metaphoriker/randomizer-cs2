module randomizer.model {
  requires com.fasterxml.jackson.databind;
  requires com.google.gson;
  requires com.google.guice;
  requires com.sun.jna;
  requires com.sun.jna.platform;
  requires java.desktop;
  requires static lombok;
  requires org.apache.commons.io;
  requires org.slf4j;

  opens de.metaphoriker.model.action.handling to
      com.google.guice;

  exports de.metaphoriker.model;
  exports de.metaphoriker.model.action;
  exports de.metaphoriker.model.cfg.keybind;
  exports de.metaphoriker.model.action.custom;
  exports de.metaphoriker.model.exception;
  exports de.metaphoriker.model.stuff;
  exports de.metaphoriker.model.updater;
  exports de.metaphoriker.model.messages;
  exports de.metaphoriker.model.notify;
  exports de.metaphoriker.model.cfg;
  exports de.metaphoriker.model.action.handling;
  exports de.metaphoriker.model.action.sequence;
  exports de.metaphoriker.model.watcher;
}
