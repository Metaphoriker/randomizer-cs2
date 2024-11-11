package com.revortix.model.watcher;

import com.revortix.model.persistence.dao.ActionSequenceDao;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * The {@code FileSystemWatcher} class monitors changes to a specified directory and notifies registered listeners about
 * file creation and deletion events.
 *
 * <p>This watcher specifically monitors files with the ".sequence" extension, invoking the
 * registered {@code Consumer} instances whenever such a file is created, deleted or modified.
 */
@Slf4j
public class FileSystemWatcher implements Runnable {

    private static final long DEBOUNCE_TIME_MS = 50;

    private final List<Consumer<Path>> fileChangeListeners = new ArrayList<>();
    private final Map<Path, Long> lastModifiedTimes = new ConcurrentHashMap<>();

    public void addFileChangeListener(Consumer<Path> fileChangeListener) {
        fileChangeListeners.add(fileChangeListener);
    }

    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {

            Path clusterDirectory = ActionSequenceDao.ACTION_SEQUENCE_FOLDER.toPath();
            clusterDirectory.register(
                    watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            log.debug("Started watching the directory: {}", clusterDirectory);

            while (!Thread.currentThread().isInterrupted()) {
                WatchKey key = watcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {

                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    if (filename.toString().endsWith(".sequence")) {
                        Long lastModifiedTime = lastModifiedTimes.getOrDefault(filename, 0L);
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastModifiedTime > DEBOUNCE_TIME_MS) { // entprellzeit
                            lastModifiedTimes.put(filename, currentTime);
                            fileChangeListeners.forEach(consumer -> consumer.accept(filename));
                            log.debug("Detected {} on file: {}", kind.name(), filename);
                        }
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
            Thread.currentThread().interrupt();
        } catch (InterruptedException e) {
            log.warn("FileSystemWatcher was interrupted: ", e);
            Thread.currentThread().interrupt();
        }
    }
}
