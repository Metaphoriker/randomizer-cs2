package dev.luzifer.model.watcher;

import dev.luzifer.model.event.cluster.EventClusterDao;
import dev.luzifer.model.notify.Notification;
import dev.luzifer.model.notify.Speaker;
import java.io.IOException;
import java.nio.file.*;

public class FileSystemWatcher implements Runnable {

  @Override
  public void run() {

    try (WatchService watcher = FileSystems.getDefault().newWatchService()) {

      Path clusterDirectory = EventClusterDao.CLUSTER_FOLDER.toPath();
      clusterDirectory.register(
          watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

      while (true) {

        WatchKey key = watcher.take();
        for (WatchEvent<?> event : key.pollEvents()) {

          WatchEvent.Kind<?> kind = event.kind();
          if (kind == StandardWatchEventKinds.OVERFLOW) continue;

          WatchEvent<Path> ev = (WatchEvent<Path>) event;
          Path filename = ev.context();

          if (filename.toString().endsWith(".cluster"))
            Speaker.notify(new Notification(getClass(), filename.toString()));
        }
        key.reset();
      }

    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e); // rethrow for uncaught handler
    }
  }
}
