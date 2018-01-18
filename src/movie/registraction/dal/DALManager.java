package movie.registraction.dal;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.collections.ObservableList;
import movie.registraction.be.Movie;
import movie.registraction.dal.exception.DALException;

/**
 *
 * @author Axl
 */
public class DALManager
{

    private MovieDAO mDAO;
    private LibraryScan lib;

    private List<Path> folders;
    private ObservableList<Path> changes;

    /**
     * Constructor
     *
     * @throws DALException
     */
    public DALManager() throws DALException
    {
            mDAO = new MovieDAO();
            this.lib = new LibraryScan();

            this.changes = lib.getObsList();
            this.folders = new ArrayList();
    }

    /**
     * Saves the path of the movie library
     *
     * Stores the path of the folder where the movie collection is saved as a
     * String
     *
     * @param path The String containing the path to the library
     *
     * @throws DALException Throws an exception if it fails to write the file
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
     * @param name The name of the file to read
     *
     * @return A String object containing the path to the library
     *
     * @throws DALException Throws an exception if it fails to read the file
     */
    public String loadDirectory(String name) throws DALException
    {
        try (BufferedReader br = new BufferedReader(new FileReader(name)))
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
     * @param filter A List of filters to allow through
     *
     * @return An ArrayList containing paths
     *
     * @throws DALException Throws an exception if it fails to go through the
     *                      folders
     */
    public ArrayList<Path> getMovieList(ArrayList<String> filter) throws DALException
    {
        ArrayList<Path> list = new ArrayList();

        Path startPath = Paths.get(this.loadDirectory("path.txt"));

        fileTreeSearch(startPath, list, filter);

        return list;
    }

    /**
     * Puts a watcher on the library
     */
    public void setDirectoryWatch()
    {
        //
        ArrayList<Path> singleFolders = new ArrayList();

        // Removes the dublicates
        this.folders.stream().filter((folder)
                -> (!singleFolders.contains(folder))).forEachOrdered((folder) ->
        {
            singleFolders.add(folder);
        });

        singleFolders.forEach((path) ->
        {
            System.out.println(path);
        });

        directoryWatcher(singleFolders);
    }

    /**
     * Loops through all folders and and them to a list
     * Setting startPath as the root, it'll loop through all files, as well as
     * all the files in subfolders and add them to a list
     *
     * @param startPath The root folder from where to start
     * @param list      The list which will get all the files added to it
     *
     * @throws DALException Throws an exception if it fails to access a
     *                      file/folder
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
            }
            else if (file.isDirectory())
            {
                fileTreeSearch(file.toPath(), list, filter);
            }
            else if (positiveFilter(file, filter))
            {
                this.folders.add(file.toPath().getParent());
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
     * Filters are hardcoded, do to lacking any other method
     *
     * @param file The file to check for
     *
     * @return True if encountered a system folder/file. False otherwise
     */
    private boolean systemFilter(File file)
    {
        String config = file.getAbsolutePath().substring(1);

        /**
         * This has been hardcoded because I couldn't find another way to make
         * it skip these folders, should it encounter any of them.
         */
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
        else
        {
            return config.equalsIgnoreCase(":\\WindowsApps");
        }
    }

    /**
     * IF fits with the filter, returns true.
     *
     * @param file
     * @param filter
     *
     * @return
     */
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

    /**
     * @return a list of movie objects.
     *
     * @throws DALException
     */
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
     * @param filePath
     *
     * @throws DALException
     */
    public void addMovie(String[] movieMetaData, String filePath) throws DALException
    {

        int id = mDAO.addMovie(movieMetaData, filePath);

        String[] metaMovieCategories = movieMetaData[5].split(" ");
        for (String cat : metaMovieCategories)
        {
            mDAO.addMovieCategory(id, cat);
        }
    }

    /**
     * Add a change listener to a folder and all sub folders
     *
     * @param folders
     */
    public void directoryWatcher(ArrayList folders)
    {
        System.out.println("Before thread");

        // Sets the folders in the library scanner
        this.lib.setFolders(folders);

        // Initiates the thread the scanner will run on
        Thread scan;
        scan = new Thread(lib); // Creates the thread
        scan.setDaemon(true); // Tells the thread to close with the app
        scan.start(); // Start the thread

        changes.setAll(lib.getObsList());

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
        mDAO.removeMovieCategory(movieid, category);
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
        mDAO.addMovieCategory(movieid, category);
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
        return mDAO.getAllCategories();
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
        mDAO.removeCategory(cat);
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
        mDAO.addCategory(cat);
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
        mDAO.setPersonalRating(movieId, rating);
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
        mDAO.removeMovie(movieId);
    }

    public List<Movie> searchMovies(String sqlString, List<String> categories, HashMap<String, String> year, int rating, String searchText, boolean searchNumeric) throws DALException
    {
        return mDAO.searchMovies(sqlString, categories, year, rating, searchText, searchNumeric);
    }

    /**
     * Retrieves the list that hold the changes
     *
     * @return
     */
    public ObservableList<Path> getChangeList()
    {
        return this.changes;
    }

    /**
     * Sets when you last saw the video.
     *
     * @param movieId
     *
     * @throws DALException
     */
    public void setLastView(int movieId) throws DALException
    {
        mDAO.setLastView(movieId);
    }

}
