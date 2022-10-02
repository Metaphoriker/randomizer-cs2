package dev.luzifer.model.exception;

import dev.luzifer.model.file.WhateverThisFuckerIs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;

public class UncaughtExceptionLogger implements Thread.UncaughtExceptionHandler {

    public static final UncaughtExceptionLogger DEFAULT_UNCAUGHT_EXCEPTION_LOGGER = new UncaughtExceptionLogger();
    
    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    
    private static final File LOG_FOLDER = new File(WhateverThisFuckerIs.getAppdataFolder() + File.separator + "logs");
    private static final File LOG_FILE = new File(LOG_FOLDER, DATE_FORMAT.format(System.currentTimeMillis()) + ".log");
    
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        
        StringBuilder builder = new StringBuilder("Exception caught in" + thread.getName() + "Thread: ").append(throwable.getMessage()).append("\n");
        for(StackTraceElement element : throwable.getStackTrace())
            builder.append("\tat ").append(element.toString()).append("\n");
    
        log(builder.toString());
    }
    
    private void log(String message) {
        
        if(!LOG_FILE.exists()) {
            LOG_FOLDER.mkdirs();
            try {
                LOG_FILE.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        try(PrintWriter printWriter = new PrintWriter(new FileOutputStream(LOG_FILE, true))) {
            printWriter.println(message);
            printWriter.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
