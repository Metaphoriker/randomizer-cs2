package de.metaphoriker.model.watcher;

import de.metaphoriker.model.action.sequence.ActionSequenceDao;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileSystemWatcher implements Runnable {

  private final List<Consumer<Path>> fileChangeListeners = new ArrayList<>();

  public void addFileChangeListener(Consumer<Path> fileChangeListener) {
    fileChangeListeners.add(fileChangeListener);
  }

  @Override
  public void run() {
    try (WatchService watcher = FileSystems.getDefault().newWatchService()) {

      Path clusterDirectory = ActionSequenceDao.ACTION_SEQUENCE_FOLDER.toPath();
      clusterDirectory.register(
          watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

      log.debug("Started watching the directory: {}", clusterDirectory);

      while (!Thread.currentThread().isInterrupted()) {
        WatchKey key = watcher.take();
        for (WatchEvent<?> event : key.pollEvents()) {

          WatchEvent.Kind<?> kind = event.kind();
          if (kind == StandardWatchEventKinds.OVERFLOW) continue;

          WatchEvent<Path> ev = (WatchEvent<Path>) event;
          Path filename = ev.context();

          if (filename.toString().endsWith(".sequence")) {
            fileChangeListeners.forEach(consumer -> consumer.accept(filename));
            log.debug("Detected {} on file: {}", kind.name(), filename);
          }
        }

        boolean valid = key.reset();
        if (!valid) {
          log.warn("WatchKey no longer valid. Stopping watcher.");
          break;
        }
      }

    } catch (IOException e) {
      log.error("IOException occurred while watching directory: ", e);
    } catch (InterruptedException e) {
      log.warn("FileSystemWatcher was interrupted: ", e);
      Thread.currentThread().interrupt();
    }
  }
}
