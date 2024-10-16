package de.metaphoriker.model.exception;

import de.metaphoriker.model.ApplicationContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UncaughtExceptionLogger implements Thread.UncaughtExceptionHandler {

  public static final UncaughtExceptionLogger DEFAULT_UNCAUGHT_EXCEPTION_LOGGER =
      new UncaughtExceptionLogger();

  private static final File LOG_FOLDER =
      new File(ApplicationContext.getAppdataFolder() + File.separator + "logs");
  private static final SimpleDateFormat LOG_DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

  static {
    LOG_FOLDER.mkdirs();
  }

  @Override
  public void uncaughtException(Thread thread, Throwable throwable) {
    String exceptionMessage = buildExceptionMessage(thread, throwable);
    logToFile(exceptionMessage);
  }

  private String buildExceptionMessage(Thread thread, Throwable throwable) {
    StringBuilder exceptionMessageBuilder =
        new StringBuilder()
            .append("Uncaught exception in thread \"")
            .append(thread.getName())
            .append("\": ")
            .append(throwable.getClass().getSimpleName())
            .append(" - ")
            .append(throwable.getMessage())
            .append("\n");

    for (StackTraceElement element : throwable.getStackTrace()) {
      exceptionMessageBuilder.append("\tat ").append(element.toString()).append("\n");
    }

    if (throwable.getCause() != null) {
      exceptionMessageBuilder.append("Caused by: ").append(throwable.getCause()).append("\n");
    }
    return exceptionMessageBuilder.toString();
  }

  private void logToFile(String message) {
    String logFileName = LOG_DATE_FORMAT.format(new Date()) + ".log";
    File logFile = new File(LOG_FOLDER, logFileName);
    try {
      if (!logFile.exists()) {
        Files.createFile(logFile.toPath());
        log.debug("Log file wurde angelegt: {}", logFile.getAbsolutePath());
      }
      try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(logFile, true))) {
        printWriter.println(message);
      }
      log.debug("Nachricht {} wurde zur Datei {} geschrieben", message, logFile.getAbsolutePath());
    } catch (IOException e) {
      log.error("Failed to log exception to file: {}", logFileName, e);
    }
  }
}
