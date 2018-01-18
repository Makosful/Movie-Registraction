package movie.registraction.dal;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Axl
 */
public class LibraryScan implements Runnable
{

    private final ObservableList<Path> list;

    private List folders;

    /**
     * Constructor
     */
    public LibraryScan()
    {
        this.list = FXCollections.observableArrayList();
    }

    /**
     * Implimented from Runnable
     */
    @Override
    public void run()
    {
        watch(folders);
        //watch(path);
    }

    /**
     * Sets a WatchService on all Paths provided
     *
     * @param folders A List of Paths to watch
     */
    private void watch(List<Path> folders)
    {
        try (WatchService watcher = FileSystems.getDefault().newWatchService())
        {
            WatchKey key;
            Map<WatchKey, Path> keyMap = new HashMap<>();

            for (Path folder : folders)
            {
                keyMap.put(folder.register(watcher,
                                           StandardWatchEventKinds.ENTRY_CREATE,
                                           StandardWatchEventKinds.ENTRY_DELETE,
                                           StandardWatchEventKinds.ENTRY_MODIFY),
                           folder);
                //this.list.add(folder);
            }

            do
            {
                key = watcher.take();
                Path eventDir = keyMap.get(key);

                for (WatchEvent<?> event : key.pollEvents())
                {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path eventPath = (Path) event.context();

                    if ("ENTRY_DELETE".equals(kind.toString()))
                    {
                        Path p = new File(eventDir + "\\" + eventPath).toPath();
                        this.list.remove(p);
                        //System.out.println("Removed: " + eventPath);
                        //System.out.println(this.list.size());
                    }

                    if ("ENTRY_CREATE".equals(kind.toString()))
                    {
                        Path p = new File(eventDir + "\\" + eventPath).toPath();
                        this.list.add(p);
                        //System.out.println("Created: " + eventPath);
                        //System.out.println(this.list.size());
                    }

                }
            }
            while (key.reset());
        }
        catch (IOException | InterruptedException ex)
        {
        }
    }

    /**
     * Gets the list containing the changes
     *
     * @return Returns an ObservableList containing all the changed files' paths
     */
    public ObservableList<Path> getObsList()
    {
        return this.list;
    }

    /**
     * Sets the folders' list to reference the given list
     *
     * @param folderList The list to mirror
     */
    public void setFolders(ArrayList<Path> folderList)
    {
        this.folders = folderList;
    }
}
