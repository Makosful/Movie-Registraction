package movie.registraction.dal;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Axl
 */
public class LibraryScan implements Runnable
{

    private Path path;
    private ObservableList<Path> list;
    private List folders;

    public LibraryScan(Path root)
    {
        this.path = root;
        this.list = FXCollections.observableArrayList();
    }

    public LibraryScan(ArrayList list)
    {
        this.folders = list;
        this.list = FXCollections.observableArrayList();
    }

    @Override
    public void run()
    {
        watch(folders);
        //watch(path);
    }

    private void watch(List<Path> folders)
    {
        try (WatchService watcher = FileSystems.getDefault().newWatchService())
        {
            Map<WatchKey, Path> keyMap = new HashMap<>();

            for (Path folder : folders)
            {
                keyMap.put(folder.register(watcher,
                                           StandardWatchEventKinds.ENTRY_CREATE,
                                           StandardWatchEventKinds.ENTRY_DELETE,
                                           StandardWatchEventKinds.ENTRY_MODIFY),
                           folder);
            }

            WatchKey key;

            do
            {
                key = watcher.take();
                Path eventDir = keyMap.get(key);

                for (WatchEvent<?> event : key.pollEvents())
                {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path eventPath = (Path) event.context();

                    /**
                     * ENTRY_DELETE
                     * ENTRY_CREATE
                     * ENTRY_MODIFY
                     */
                    System.out.println(eventDir + ": " + kind + ": " + eventPath);
                }
            }
            while (key.reset());
        }
        catch (IOException | InterruptedException ex)
        {
        }
    }

    private void watch(Path path)
    {
        try (WatchService watcher = FileSystems.getDefault().newWatchService())
        {
            Map<WatchKey, Path> keyMap = new HashMap<>();

            Files.walkFileTree(
                    path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult preVisitDirectory(
                        Path dir,
                        BasicFileAttributes attrs)
                        throws IOException
                {
                    keyMap.put(dir.register(watcher,
                                            StandardWatchEventKinds.ENTRY_CREATE,
                                            StandardWatchEventKinds.ENTRY_DELETE,
                                            StandardWatchEventKinds.ENTRY_MODIFY),
                               dir);
                    return FileVisitResult.CONTINUE;
                }
            });

            Collection<Path> values = keyMap.values();

            WatchKey key;

            do
            {
                key = watcher.take();
                Path eventDir = keyMap.get(key);

                for (WatchEvent<?> event : key.pollEvents())
                {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path eventPath = (Path) event.context();

                    if ("ENTRY_DELETE".equals(kind.name()))
                    {
                        Path toPath = new File(eventDir + "/" + eventPath).toPath();
                        // System.out.println("Deleted");
                        this.list.add(toPath);
                    }
                    if ("ENTRY_CREATE".equals(kind.name()))
                    {
                        Path toPath = new File(eventDir + "/" + eventPath).toPath();
                        // System.out.println("Created");
                        this.list.add(toPath);
                    }

                    //System.out.println(eventDir + ": " + kind + ": " + eventPath);
                }
            }
            while (key.reset());
        }
        catch (IOException | InterruptedException ex)
        {
        }
    }

    public ObservableList<Path> getObsList()
    {
        return this.list;
    }
}
