package movie.registraction.dal;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        ArrayList<Path> folders = new ArrayList();

        Path startPath = Paths.get(this.loadDirectory("path.txt"));

        fileTreeSearch(startPath, list, filter);
        findFolders(startPath, folders);

        this.directoryWatcher(folders);

        return list;
    }

    private void findFolders(Path startPath, ArrayList<Path> folders) throws DALException
    {
        try
        {
            Files.walkFileTree(
                    startPath,
                    new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult preVisitDirectory(
                        Path dir,
                        BasicFileAttributes attrs)
                        throws IOException
                {
                    return FileVisitResult.CONTINUE;
                }

            });
        }
        catch (IOException ex)
        {
            throw new DALException();
        }
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
        {
            if (file == null)
            {
                break;
            }
            else if (systemFilter(file))
            {
                // Do nothing
            }
            else if (file.isDirectory())
            {
                System.out.println("Going into folder");
                fileTreeSearch(file.toPath(), list, filter);
            }
            else if (positiveFilter(file, filter))
            {
                list.add(file.toPath());
            }
        }
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
        {
            return true;
        }
        else if (config.equalsIgnoreCase(":\\Config.Msi"))
        {
            return true;
        }
        else if (config.equalsIgnoreCase(":\\DeliveryOptimization"))
        {
            return true;
        }
        else if (config.equalsIgnoreCase(":\\Recovery"))
        {
            return true;
        }
        else if (config.equalsIgnoreCase(":\\System Volume Information"))
        {
            return true;
        }
        else if (config.equalsIgnoreCase(":\\WindowsApps"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean positiveFilter(File file, ArrayList<String> filter)
    {
        for (int i = 0; i < filter.size(); i++)
        {
            if (file.getAbsolutePath().endsWith(filter.get(i)))
            {
                return true;
            }
        }
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

            String[] metaMovieCategories = movieMetaData[5].split(" ");
            for (String cat : metaMovieCategories)
            {
                mDAO.addMovieCategory(id, cat);
            }
        }
        catch (DALException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Add a change listener to a folder and all sub folders
     *
     * @param root    The root folder to watch
     * @param folders
     *
     * @throws movie.registraction.dal.DALException
     */
    public void directoryWatcher(ArrayList<Path> folders)
    {
        System.out.println("Before thread");

        Thread scan;
        scan = new Thread(new LibraryScan(folders)); // Creates the thread
        scan.setDaemon(true); // Tells the thread to close with the app
        //scan.start(); // Start the thread

        System.out.println("After thread");
    }

    /**
     * Sends the given category and specific movieid to MovieDAO where it is
     * removed in the db
     *
     * @param movieid
     * @param category
     *
     * @throws DALException
     */
    public void removeMovieCategory(int movieid, String category) throws DALException
    {
        try
        {
            mDAO.removeMovieCategory(movieid, category);
        }
        catch (DALException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Sends the given category and specific movieid to MovieDAO
     * where it is added to the db
     *
     * @param movieid
     * @param category
     *
     * @throws DALException
     */
    public void addMovieCategory(int movieid, String category) throws DALException
    {
        try
        {
            mDAO.addMovieCategory(movieid, category);
        }
        catch (DALException ex)
        {
            throw new DALException();
        }
    }

    /**
     * gets all categories from the database and MovieDAO and retur
     *
     * @return
     *
     * @throws DALException
     */
    public List<String> getAllCategories() throws DALException
    {
        try
        {
            return mDAO.getAllCategories();
        }
        catch (DALException ex)
        {
            throw new DALException();
        }

    }

    /**
     * Sends the given category to MovieDAO where it is removed in the db
     *
     * @param cat
     *
     * @throws DALException
     */
    public void removeCategory(String cat) throws DALException
    {
        try
        {
            mDAO.removeCategory(cat);
        }
        catch (DALException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Sends the given category to MovieDAO where it is added to the db
     *
     * @param cat
     *
     * @throws DALException
     */

    public void addCategory(String cat) throws DALException
    {
        try
        {
            mDAO.addCategory(cat);
        }
        catch (DALException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Sends the personal rating and movie id to MovieDAO to update
     * personalrating
     *
     * @param movieId
     * @param rating
     *
     * @throws DALException
     */
    public void setPersonalRating(int movieId, int rating) throws DALException
    {
        try
        {
            mDAO.setPersonalRating(movieId, rating);
        }
        catch (DALException ex)
        {
            throw new DALException();
        }
    }

    /**
     * This method is to get a imgPath from a specific movie. So that it can be
     * thrown into the tilepane.
     *
     * @param movieName
     *
     * @return
     *
     * @throws DALException
     */
    public String getSpecificMovieImage(String movieName) throws DALException
    {
        return mDAO.getSpecificMovieImage(movieName);
    }

    public void removeMovie(int movieId) throws DALException
    {
        try
        {
            mDAO.removeMovie(movieId);
        }
        catch (DALException ex)
        {
            throw new DALException();
        }
    }

  
    public ObservableList<Movie> searchMovies(String sqlString, List<String> categories, List<String> year, String searchText, boolean searchNumeric) throws DALException
    {
        try
        {
           return mDAO.searchMovies(sqlString, categories, year, searchText, searchNumeric);
        }
        catch (DALException ex)
        {
            throw new DALException();
        }
    }

}
