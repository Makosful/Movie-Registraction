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

    private final MovieDAO mDAO;
    private final LibraryScan lib;

    private final List<Path> folders;
    private final ObservableList<Path> changes;

    /**
     * Constructor
     *
     * @throws DALException Throws an exception if it fails to initiate the DAL
     *                      layer objects
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
     * Compares the extension of the file with the filters list.
     *
     * @param file   The file to compare
     * @param filter The list of extensions to compare to
     *
     * @return If the file's extension matches any on the list, return try.
     *         Otherwise return false
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
     * @throws DALException Throws an exception if it can't access the database
     */
    public ObservableList<Movie> getAllMovies() throws DALException
    {
        return mDAO.getAllMovies();
    }

    /**
     * Adds a movie with the supplied metadata, the addmovie returns the
     * inserted movie row id, which is used to inserting the movies categories
     *
     * @param meta     A String Array containing the MetaData of a movie
     * @param filePath The local file path for the movie
     *
     * @throws DALException Throws an exception if it fails to access the
     *                      database
     */
    public void addMovie(String[] meta, String filePath) throws DALException
    {

        int id = mDAO.addMovie(meta, filePath);

        String[] metaMovieCategories = meta[5].split(" ");
        
        for (String cat : metaMovieCategories)
        {
            mDAO.addMovieCategory(id, cat);
        }
    }

    /**
     * Add a change listener to a folder and all sub folders
     *
     * @param root The root of the folders to watch
     */
    public void directoryWatcher(ArrayList root)
    {
        // Sets the folders in the library scanner
        this.lib.setFolders(root);

        // Initiates the thread the scanner will run on
        Thread scan;
        scan = new Thread(lib); // Creates the thread
        scan.setDaemon(true); // Tells the thread to close with the app
        scan.start(); // Start the thread

        changes.setAll(lib.getObsList());
    }

    /**
     * Sends the given category and specific movieid to MovieDAO where it is
     * removed in the db
     *
     * @param id       The ID of the Movie
     * @param category The category to remove
     *
     * @throws DALException Throws an exception if it fails to access the
     *                      database
     */
    public void removeMovieCategory(int id, String category) throws DALException
    {
        mDAO.removeMovieCategory(id, category);
    }

    /**
     * Sends the given category and specific movieid to MovieDAO
     * where it is added to the db
     *
     * @param id       The ID of the Movie
     * @param category The category to add
     *
     * @throws DALException Throws an exception if it fails to access the
     *                      database
     */
    public void addMovieCategory(int id, String category) throws DALException
    {
        mDAO.addMovieCategory(id, category);
    }

    /**
     * gets all categories from the database and MovieDAO and retur
     *
     * @return Returns a List of Strings with all the categories
     *
     * @throws DALException Throws an exception if it fails to access the
     *                      databse
     */
    public List<String> getAllCategories() throws DALException
    {
        return mDAO.getAllCategories();
    }

    /**
     * Sends the given category to MovieDAO where it is removed in the db
     *
     * @param category The category to remove
     *
     * @throws DALException Throws an exception if it fails to access the
     *                      database
     */
    public void removeCategory(String category) throws DALException
    {
        mDAO.removeCategory(category);
    }

    /**
     * Sends the given category to MovieDAO where it is added to the db
     *
     * @param category The category to add
     *
     * @throws DALException Throws an exception if it fails to access the
     *                      database
     */
    public void addCategory(String category) throws DALException
    {
        mDAO.addCategory(category);
    }

    /**
     * Sends the personal rating and movie id to MovieDAO to update
     * personalrating
     *
     * @param id     The ID of the Movie
     * @param rating The value of the personal rating
     *
     * @throws DALException Throws an exception if it fails to access the
     *                      database
     */
    public void setPersonalRating(int id, int rating) throws DALException
    {
        mDAO.setPersonalRating(id, rating);
    }

    /**
     * This method is to get a imgPath from a specific movie. So that it can be
     * thrown into the tilepane.
     *
     * @param title The title of the Movie
     *
     * @return Returns a String containing the URL for the movie image
     *
     * @throws DALException Throws an exception if it fails to access the API
     */
    public String getSpecificMovieImage(String title) throws DALException
    {
        return mDAO.getSpecificMovieImage(title);
    }

    /**
     * Removes a Movie from the database
     *
     * @param id The ID of the Movie to remove
     *
     * @throws DALException Throws an exception if it fails to access the
     *                      database
     */
    public void removeMovie(int id) throws DALException
    {
        mDAO.removeMovie(id);
    }

    /**
     * Search the database for Movies matching the criteria
     *
     * @param sqlString     String containing the sql query
     * @param categories    List of categories as criteria for the search
     * @param year          HashMap of years as criteria
     * @param rating        the rating number movies should be above in the search
     * @param searchText    The text to search for
     * @param searchNumeric boolean if true the searchtext is for years only 
     *
     * @return Returns a List of all Movies matching the cireteria
     *
     * @throws DALException Thorws an exception if it fails to access the
     *                      database
     */
    public List<Movie> searchMovies(String sqlString,
                                    List<String> categories,
                                    HashMap<String, String> year,
                                    int rating,
                                    String searchText,
                                    boolean searchNumeric)
            throws DALException
    {
        return mDAO.searchMovies(sqlString, categories, year, rating, searchText, searchNumeric);
    }

    /**
     * Gets the list holding the Paths to the files changed in the library
     *
     * @return Returns an ObservableList of Paths with all the files changed in
     *         the library
     */
    public ObservableList<Path> getChangeList()
    {
        return this.changes;
    }

    /**
     * Sets when you last saw the video.
     *
     * @param movieId
     * @throws DALException Throws an exception if it fails to access the
     *                      database
     */
    public void setLastView(int movieId) throws DALException 
    { 
       mDAO.setLastView(movieId);
    }

}
