package movie.registraction.dal;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Axl
 */
public class LibraryScan implements Runnable
{

    ArrayList<Path> list;

    public LibraryScan(ArrayList<Path> list)
    {
        this.list = list;
    }

    @Override
    public void run()
    {
        System.out.println("Thread START");
        for (Path path : list)
        {
            System.out.println(path);
        }
        System.out.println("Thread END");
        watch(list);
    }

    private void m()
    {
    }

    private void watch(ArrayList<Path> list)
    {
        try (WatchService watcher = FileSystems.getDefault().newWatchService())
        {
            Map<WatchKey, Path> keyMap = new HashMap<>();
            for (Path path : list)
            {
                keyMap.put(path.register(watcher,
                                         StandardWatchEventKinds.ENTRY_CREATE,
                                         StandardWatchEventKinds.ENTRY_DELETE,
                                         StandardWatchEventKinds.ENTRY_MODIFY),
                           path);
            }

            Collection<Path> values = keyMap.values();
            for (Path value : values)
            {
                System.out.println(value);
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
                    System.out.println(eventDir + ": " + kind + ": " + eventPath);
                }
            }
            while (key.reset());
        }
        catch (IOException | InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
}
