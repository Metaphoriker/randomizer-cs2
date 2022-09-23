package dev.luzifer;

import dev.luzifer.model.config.ConfigRepository;
import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.EventExecutor;
import dev.luzifer.model.event.EventRepository;
import dev.luzifer.model.event.cluster.EventClusterRepository;
import dev.luzifer.model.event.events.CrouchEvent;
import dev.luzifer.model.event.events.DropWeaponEvent;
import dev.luzifer.model.event.events.EmptyMagazineEvent;
import dev.luzifer.model.event.events.EscapeEvent;
import dev.luzifer.model.event.events.IWannaKnifeEvent;
import dev.luzifer.model.event.events.JumpEvent;
import dev.luzifer.model.event.events.MouseLeftClickEvent;
import dev.luzifer.model.event.events.MouseMoveEvent;
import dev.luzifer.model.event.events.MouseRightClickEvent;
import dev.luzifer.model.event.events.MoveEvent;
import dev.luzifer.model.event.events.ReloadEvent;
import dev.luzifer.model.event.events.ShiftEvent;
import dev.luzifer.model.exception.UncaughtExceptionLogger;
import dev.luzifer.model.messages.Messages;
import dev.luzifer.model.scheduler.Scheduler;
import dev.luzifer.model.scheduler.SchedulerThread;
import dev.luzifer.gui.AppStarter;
import dev.luzifer.model.updater.Updater;
import javafx.application.Application;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    public static final File APPDATA_FOLDER = new File(System.getenv("APPDATA") + File.separator + "randomizer");
    
    private static final String TEST_FLAG = "-test";

    // TODO: new Log file everyday to avoid huge log files and to make it easier to find the last log (file)
    private static final File LOG_FILE = new File(System.getenv("APPDATA") + "\\randomizer\\logs", "log.txt");
    private static final Thread.UncaughtExceptionHandler DEFAULT_UNCAUGHT_EXCEPTION_LOGGER = new UncaughtExceptionLogger(LOG_FILE);

    private static final EventRepository EVENT_REPOSITORY = new EventRepository();
    private static final EventClusterRepository EVENT_CLUSTER_REPOSITORY = new EventClusterRepository();
    private static final ConfigRepository CONFIG_REPOSITORY = new ConfigRepository();
    private static final Scheduler SCHEDULER = new Scheduler();
    
    public static void main(String[] args) throws IOException {
        
        setupAppdataFolder();
        startUpdaterIfNecessary();
        
        registerEvents();
        evaluateFlags(Collections.unmodifiableList(Arrays.asList(args)));

        startScheduler();
        startEventExecutor();

        Messages.cache();
        Thread.currentThread().setUncaughtExceptionHandler(DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);

        Application.launch(AppStarter.class);
    }
    
    public static Scheduler getScheduler() {
        return SCHEDULER;
    }
    
    public static EventRepository getEventRepository() {
        return EVENT_REPOSITORY;
    }
    
    public static EventClusterRepository getEventClusterRepository() {
        return EVENT_CLUSTER_REPOSITORY;
    }
    
    public static ConfigRepository getConfigRepository() {
        return CONFIG_REPOSITORY;
    }
    
    private static void startUpdaterIfNecessary() throws IOException {
        
        File thisFile = new File("randomizer.jar");
        if(Updater.isUpdateAvailable(thisFile, Updater.RANDOMIZER_VERSION_URL)) {
            Runtime.getRuntime().exec("java -jar randomizer-updater.jar -randomizerLocation=" + thisFile.getAbsolutePath());
            System.exit(0);
        }
    }
    
    private static void setupAppdataFolder() {
        APPDATA_FOLDER.mkdirs();
        installUpdater(APPDATA_FOLDER);
    }
    
    private static File installUpdater(File folderInto) {
        
        File updaterJar = new File(folderInto, "randomizer-updater.jar");
        try {
            updaterJar.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        if(Updater.isUpdateAvailable(updaterJar, Updater.UPDATER_VERSION_URL))
            Updater.update(updaterJar, Updater.UPDATER_DOWNLOAD_URL);
        
        return updaterJar;
    }
    
    private static void evaluateFlags(List<String> args) {
        if(args.contains(TEST_FLAG))
            disableEvents(); // TODO: TBR once the randomizer's ApplicationState works
    }

    private static void startScheduler() {
    
        SchedulerThread schedulerThread = new SchedulerThread(SCHEDULER);
        schedulerThread.setUncaughtExceptionHandler(DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
        schedulerThread.setDaemon(true);
        schedulerThread.start();
    }
    
    private static void startEventExecutor() {
        
        EventExecutor eventExecutor = new EventExecutor(EVENT_REPOSITORY, EVENT_CLUSTER_REPOSITORY);
        eventExecutor.setUncaughtExceptionHandler(DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
        eventExecutor.setDaemon(true);
        eventExecutor.start();
    }
    
    private static void registerEvents() {
    
        EVENT_REPOSITORY.registerEvent(new MouseLeftClickEvent());
        EVENT_REPOSITORY.registerEvent(new MouseRightClickEvent());
        EVENT_REPOSITORY.registerEvent(new MouseMoveEvent());
        EVENT_REPOSITORY.registerEvent(new MoveEvent());
        EVENT_REPOSITORY.registerEvent(new CrouchEvent());
        EVENT_REPOSITORY.registerEvent(new ShiftEvent());
        EVENT_REPOSITORY.registerEvent(new JumpEvent());
        EVENT_REPOSITORY.registerEvent(new ReloadEvent());
        EVENT_REPOSITORY.registerEvent(new EscapeEvent());
        EVENT_REPOSITORY.registerEvent(new DropWeaponEvent());
        EVENT_REPOSITORY.registerEvent(new EmptyMagazineEvent());
        EVENT_REPOSITORY.registerEvent(new IWannaKnifeEvent());
    }

    private static void disableEvents() {
        for(Event event : EVENT_REPOSITORY.getRegisteredEvents())
            EVENT_REPOSITORY.disableEvent(event);
    }
    
}
