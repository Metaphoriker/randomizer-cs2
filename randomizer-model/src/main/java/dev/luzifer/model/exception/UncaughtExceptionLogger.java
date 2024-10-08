package dev.luzifer.model.exception;

import dev.luzifer.model.stuff.WhateverThisFuckerIs;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;

public class UncaughtExceptionLogger implements Thread.UncaughtExceptionHandler {

  public static final UncaughtExceptionLogger DEFAULT_UNCAUGHT_EXCEPTION_LOGGER =
      new UncaughtExceptionLogger();

  private static final File LOG_FOLDER =
      new File(WhateverThisFuckerIs.getAppdataFolder() + File.separator + "logs");

  static {
    LOG_FOLDER.mkdirs();
  }

  @Override
  public void uncaughtException(Thread thread, Throwable throwable) {

    StringBuilder builder =
        new StringBuilder("Exception caught in" + thread.getName() + "Thread: ")
            .append(throwable.getMessage())
            .append("\n");
    for (StackTraceElement element : throwable.getStackTrace())
      builder.append("\tat ").append(element.toString()).append("\n");

    log(builder.toString());
  }

  private void log(String message) {

    File logFile =
        new File(
            LOG_FOLDER, DateFormat.getDateInstance().format(System.currentTimeMillis()) + ".log");
    try {
      logFile.createNewFile();
    } catch (IOException e) {
      // well, fuck
    }

    try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(logFile, true))) {
      printWriter.println(message);
      printWriter.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
