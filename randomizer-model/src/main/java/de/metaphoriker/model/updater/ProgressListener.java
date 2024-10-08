package de.metaphoriker.model.updater;

public interface ProgressListener {
  void onProgress(long bytesRead, long totalBytes);
}
