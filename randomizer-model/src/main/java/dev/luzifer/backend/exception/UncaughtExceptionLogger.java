package dev.luzifer.backend.exception;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class UncaughtExceptionLogger implements Thread.UncaughtExceptionHandler {

    private final File logFile;
    
    public UncaughtExceptionLogger(File logFile) {
        this.logFile = logFile;
    }
    
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        
        StringBuilder builder = new StringBuilder("Exception caught in" + thread.getName() + "Thread: ").append(throwable.getMessage()).append("\n");
        for(StackTraceElement element : throwable.getStackTrace())
            builder.append("\tat ").append(element.toString()).append("\n");
    
        log(builder.toString());
    }
    
    private void log(String message) {
        
        if(!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        try(PrintWriter printWriter = new PrintWriter(new FileOutputStream(logFile, true))) {
            printWriter.println(message);
            printWriter.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
