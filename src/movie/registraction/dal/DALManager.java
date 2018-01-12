package movie.registraction.dal;

import java.io.*;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import movie.registraction.be.Movie;

/**
 *
 * @author Axl
 */
public class DALManager
{

    MovieDAO mDAO;

    public DALManager() throws DALException
    {
        try
        {
            mDAO = new MovieDAO();
        }
        catch (IOException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Saves the path of the movie library
     *
     * Stores the path of the folder where the movie collection is saved as a
     * String
     *
     * @param path The String containing the path to the library
     *
     * @throws DALException
     */
    public void saveDirectory(String path) throws DALException
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("path.txt")))
        {
            bw.write(path);
            bw.newLine();
        }
        catch (IOException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Loads the saved library
     *
     * @param path
     *
     * @return A String object containing the path to the library
     *
     * @throws DALException
     */
    public String loadDirectory(String path) throws DALException
    {
        try (BufferedReader br = new BufferedReader(new FileReader(path)))
        {
            String s = br.readLine();

            return s;
        }
        catch (FileNotFoundException ex)
        {
            throw new DALException();
        }
        catch (IOException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Gets the list of Paths for all items in the library
     *
     * This method will look through the specified library folder and retrive
     * all items with the ending .jpg and .png
     *
     * @param filter
     *
     * @return An ArrayList containing paths
     *
     * @throws DALException
     */
    public ArrayList<Path> getMovieList(ArrayList<String> filter) throws DALException
    {
        ArrayList<Path> list = new ArrayList();

        Path startPath = Paths.get(this.loadDirectory("path.txt"));

        fileTreeSearch(startPath, list, filter);
        return list;
    }

    /**
     * Uses file.listFiles()
     *
     * @param startPath
     * @param list
     *
     * @throws DALException
     */
    private void fileTreeSearch(Path startPath,
                                ArrayList<Path> list,
                                ArrayList<String> filter)
            throws DALException
    {
        File directory = new File(startPath.toString());

        File[] flist = directory.listFiles();

        for (File file : flist)
            if (file == null)
                break;
            else if (systemFilter(file))
            {
            }
            else if (file.isDirectory())
                fileTreeSearch(file.toPath(), list, filter);
            else if (positiveFilter(file, filter))
                list.add(file.toPath());
    }

    /**
     * Filters out the necessary files on drives.
     *
     * Returns true if any system or drive specific file is given. Returns false
     * is none has been found
     *
     * @param file The file to check for
     *
     * @return True if encountered a system folder/file. False otherwise
     */
    private boolean systemFilter(File file)
    {
        String config = file.getAbsolutePath().substring(1);

        if (file.getAbsolutePath().contains("$"))
            return true;
        else if (config.equalsIgnoreCase(":\\Config.Msi"))
            return true;
        else if (config.equalsIgnoreCase(":\\DeliveryOptimization"))
            return true;
        else if (config.equalsIgnoreCase(":\\Recovery"))
            return true;
        else if (config.equalsIgnoreCase(":\\System Volume Information"))
            return true;
        else if (config.equalsIgnoreCase(":\\WindowsApps"))
            return true;
        else
            return false;
    }

    private boolean positiveFilter(File file, ArrayList<String> filter)
    {
        for (int i = 0; i < filter.size(); i++)
            if (file.getAbsolutePath().endsWith(filter.get(i)))
                return true;
        return false;
    }

    public ObservableList<Movie> getAllMovies() throws DALException
    {
        return mDAO.getAllMovies();
    }

    /**
     * Adds a movie with the supplied metadata, the addmovie returns the
     * inserted
     * movie row id, which is used to inserting the movies categories
     *
     * @param movieMetaData
     *
     * @throws DALException
     */
    public void addMovie(String[] movieMetaData) throws DALException
    {
        try
        {
            int id = mDAO.addMovie(movieMetaData);

            String[] metaMovieCategories = movieMetaData[99].split(" ");
            for (String cat : metaMovieCategories)
                mDAO.addMovieCategory(id, cat);
        }
        catch (DALException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Add a change listener to a folder and all sub folders
     *
     * @param root The root folder to watch
     *
     * @throws movie.registraction.dal.DALException
     */
    public void m(Path root) throws DALException
    {
        try
        {
            WatchService watchService = root.getFileSystem().newWatchService();

            Files.walkFileTree(root, new SimpleFileVisitor<Path>()
                       {
                           @Override
                           public FileVisitResult preVisitDirectory(
                                   Path dir,
                                   BasicFileAttributes attrs) throws IOException
                           {
                               dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                               return FileVisitResult.CONTINUE;
                           }
                       });
        }
        catch (IOException ex)
        {
            throw new DALException();
        }
    }
}
