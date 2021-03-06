package movie.registraction.gui.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import movie.registraction.be.Movie;
import movie.registraction.bll.BLLManager;
import movie.registraction.bll.Rating;
import movie.registraction.bll.exception.BLLException;

/**
 *
 * @author Axl
 */
public class MainWindowModel
{

    private BLLManager bll;

    private final ObservableList<String> allCategories;
    private final ObservableList<Path> moviePaths;
    private final ObservableList<Path> changeList;

    private final ArrayList<String> extensionList;

    /**
     * The constructor
     */
    public MainWindowModel()
    {
        try
        {
            bll = new BLLManager();
        }
        catch (BLLException ex)
        {
        }

        changeList = bll.getChangeList();

        moviePaths = FXCollections.observableArrayList();
        allCategories = FXCollections.observableArrayList();

        extensionList = new ArrayList();
        extensionList.add(".mp4");
        extensionList.add(".mpeg4");
        extensionList.add(".wav");
        extensionList.add(".flv");
        loadMovieFromLibrary();
        setupLibraryListener();
        bll.setDirectoryWatch();

    }

    /**
     * Sets up a listner on the library
     */
    private void setupLibraryListener()
    {
        changeList.addListener((ListChangeListener.Change<? extends Path> c) ->
        {
            while (c.next())
            {
                System.out.println("Scanning again");
                this.updateLibrary();
                changeList.clear();
            }
        });
    }

    /**
     * Updates the library
     */
    private void updateLibrary()
    {
        try
        {
            bll.getMovieList(extensionList);
            List<String> updateLibrary = bll.getUpdateLibrary(bll.getMovieList(extensionList));
            if (updateLibrary.isEmpty())
            {
                // Don't add anything
            }
            else
            {
                // Add all entries to the library
                updateLibrary.forEach((filePath) ->
                {
                    String fileName = new File(filePath).toPath().getFileName().toString();
                    String[] metaData = getMovieMetaData(fileName);
                    this.addMovie(metaData, filePath);
                });
            }
        }
        catch (BLLException ex)
        {
        }
    }

    /**
     * Sends the movie information to dal 
     *
     * @param metaData
     * @param filePath The local path of the movie to add
     */
    public void addMovie(String[] metaData, String filePath)
    {

        try
        {
            bll.addMovie(metaData, filePath);
        }
        catch (BLLException ex)
        {
            System.out.println("Could not get search result");
            System.out.println("Could not add the movie in the database");
            System.out.println(ex);
        }

    }

    /**
     * Calls to getOmdbTitleResult method that creates the searchlink and
     * passes it to getSearchMetaData method which gets the movie metadata 
     * 
     * @param title The title of the movie to add
     * @return String array of movie metadata
     */
    public String[] getMovieMetaData(String title)
    {
        String[] metaData = new String[7];
        try
        {
            // Replace all the whitespaces with plus signs to make it URL friendly
            title = title.replaceAll(" ", "+");

            // Uses the API url + our fixed search index to display us all the
            // metadata of the movie searched for - if possible.
            URL searchLink;
            
            searchLink = bll.getOmdbTitleResult(title);
            
            metaData = bll.getSearchMetaData(searchLink);
        }
        catch (BLLException ex)
        {
            System.out.println("Could not retrieve metadata");
        }
        
        return metaData;
    }

    /**
     * Clears the filters.
     * 
     */
    public void fxmlClearFilters()
    {
        try
        {
            bll.clearFilters();
        }
        catch (BLLException ex)
        {
            System.out.println("Could not clear filters");
        }
    }

    /**
     * Gets all categories from changeCategories class
     *
     * @return Returns a List of Strings with all categories
     */
    public ObservableList<String> getAllCategories()
    {
        try
        {
            allCategories.addAll(bll.allCategories());
        }
        catch (BLLException ex)
        {
            System.out.println("Could not get the list of categories");
        }
        return allCategories;
    }

    /**
     * Gets all categories, is used in EditCategoryController
     *
     * @return all categories in observable list
     *
     */
    public ObservableList<String> loadCategories()
    {
        ObservableList<String> categories = null;
        try
        {
            categories = bll.loadCategories();
        }
        catch (BLLException ex)
        {
            System.out.println(ex);
        }
        return categories;
    }

    /**
     * Adds a category
     * Sends the category string to chosenCategories class to be added
     *
     * @param category The category to add
     */
    public void addChosenCategory(String category)
    {
        bll.addChosenCategory(category);
    }

    /**
     * Removes a category
     * Sends the category string to chosenCategories class to be removed
     *
     * @param category The category to remove
     */
    public void removeChosenCategory(String category)
    {
        bll.removeChosenCategory(category);
    }

    /**
     * Save the category changes in changeCategories class
     */
    public void saveCategories()
    {

        try
        {
            bll.saveCategories();
        }
        catch (BLLException ex)
        {
            System.out.println("Could not save categories");
        }

    }

    /**
     * Gets the categories for a movie
     * Gets the allready exsisting categories for a specific movie
     *
     * @param movie The movie which category to get
     *
     * @return Observable list of category strings
     */
    public ObservableList<String> loadChosenMovieCategories(Movie movie)
    {
        return bll.loadChosenMovieCategories(movie);
    }

    /**
     * Adds a category to a movie
     * Sends the category string to chosenCategories class to be added for a
     * movie
     *
     * @param category The category to add
     */
    public void addChosenMovieCategory(String category)
    {
        bll.addChosenMovieCategory(category);
    }

    /**
     * Removes a category from a movie
     * Sends the category string to chosenCategories class to be removed for a
     * movie
     *
     * @param category The category to remove
     */
    public void removeChosenMovieCategory(String category)
    {
        bll.removeChosenMovieCategory(category);
    }

    /**
     * Save the movie category changes in changeCategories class
     */
    public void saveMovieCategories()
    {
        try
        {
            bll.saveMovieCategories();
        }
        catch (BLLException ex)
        {
            System.out.println("Could not save the movie categories");
        }

    }

    /**
     * Choses the library path
     *
     * Defaults to the Windows Videos library
     */
    public void fxmlSetLibrary()
    {
        DirectoryChooser dc = new DirectoryChooser();
        File dir = dc.showDialog(null);

        if (dir.exists())
        {
            try
            {
                // Save this path to storage
                String path = dir.getAbsolutePath();
                bll.saveDirectory(path);
                this.loadMovieFromLibrary();

            }
            catch (BLLException ex)
            {
                System.out.println("Could not save path");
                System.out.println(dir.getAbsolutePath());
            }
        }
    }

    /**
     * Setting the tile setup.
     *
     * @return List of files
     */
    public List<File> chooseFile()
    {
        // Creates a new FileChooser object
        FileChooser fc = new FileChooser();

        // Defines what files it will look for
        FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter("Supported Video Files", "*.mp4", ".mpeg4", ".wav", ".flv");

        // Adds the filters
        fc.getExtensionFilters().addAll(videoFilter);

        // Opens the FileChooser and saves the results in a list
        List<File> chosenFiles = fc.showOpenMultipleDialog(null);

        // Checks if any files where chosen
        if (chosenFiles != null)
        {
            return chosenFiles;
        }
        else
        {
            // Otherwise return
            System.out.println("One or more invalid file(s) / None selected");
        }
        return chosenFiles;
    }

    /**
     * Making imageViews, setting their sizes, adding them to an arraylist,
     * adding them to our tilepane, and finally giving the imageviews an id,
     * that will refer to the actual movie.
     *
     * @param fileImage The picture to set in
     * @param imageView The image to set imageId to.
     */
    public void setImageId(File fileImage, ImageView imageView)
    {
        try
        {
            bll.setImageId(fileImage, imageView);
        }
        catch (BLLException ex)
        {
            System.out.println(ex);
        }
    }

    /**
     * Closes the menu incase the context menu is open
     * or else the user clicks normally.
     *
     * @param cm The ContextMenu to close
     */
    public void closeMenuOrClick(ContextMenu cm)
    {
        if (!cm.isShowing())
        {
            System.out.println("You clicked on the picture.");
        }
        else
        {
            cm.hide();
        }
    }

    /**
     * Checks whether contextmenu is open or not, if yes, it closes.
     * Incase user dobbleclicks several times, so it doesnt stack.
     *
     * @param cm The ContextMenu to check
     */
    public void contextMenuOpenOrNot(ContextMenu cm)
    {
        // So the contextMenu doesnt stack.
        if (cm.isShowing())
        {
            cm.hide();
            System.out.println("closed menu");
        }
    }

    /**
     * Loads the movies from the library
     */
    private void loadMovieFromLibrary()
    {
        String lib;

        // First tries to get the file
        try
        {
            lib = bll.readFile("path.txt");
        }
        catch (BLLException ex)
        {
            // If the files doesn't exist, tell the user and back out of the method
            System.out.println("Library has not been set.");
            return;
        }

        // If file was found
        try
        {
            // Make sure file isn't empty
            if (lib.isEmpty())
            {
                System.out.println("path.txt is corrupt. Set the library again.");
                System.out.println("If that doesn't work, delete path.txt and try again");
                return;
            }

            // Load the files located at the library
            moviePaths.setAll(bll.getMovieList(extensionList));

            // Tell the user the files have been added
            System.out.println("Successfully added library");
        }
        catch (BLLException ex)
        {
            System.out.println("Failed to load library");
        }
    }

    /**
     * Sets up a new rating instance with the given values
     *
     * @param movieId        The movie to set rating on, as id
     * @param rating         The rating to set, as a double
     * @param gridPaneRating The GridPane in which to set the rating
     * @param lblRating      The label in which to set the rating
     *
     * @throws BLLException Throws an exception if it fails to access the
     *                      database
     */
    public void setUpRating(int movieId, double rating, GridPane gridPaneRating, Label lblRating)
    {
        try
        {
            Rating r = new Rating(movieId, rating, gridPaneRating, lblRating);
        }
        catch (BLLException ex)
        {
            System.out.println("Could not create a new rating");
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("Image files for rating could not be loaded");
        }
    }

    /**
     * Gets the movie list
     *
     * @return Returns a List of Paths to all the movies
     */
    public ObservableList<Path> getMovieList()
    {
        return moviePaths;
    }

    /**
     * Gets the list of all movies
     *
     * @return Returns a List with all Movies
     */
    public ObservableList<Movie> getAllMovies()
    {
        ObservableList<Movie> movies = FXCollections.observableArrayList();
        try
        {
            movies = bll.getAllMovies();
        }
        catch (BLLException ex)
        {
            System.out.println(ex);
        }
        return movies;
    }

    /**
     * Call to findOldAndBadMovies in bll,
     * in order to find old and bad movies to remove
     */
    public void findOldAndBadMovies()
    {
        try
        {
            bll.findOldAndBadMovies();
        }
        catch (BLLException ex)
        {
            System.out.println(ex);
        }
    }

    /**
     * Finding the specific movie, which has same id as imageid
     * This is to find the object, that belongs to the image.
     * Gets the info of a Movie in the given ImageView
     *
     * @param imageView The ImageView which Movie to get info on
     *
     * @return Returns a Movie with the same ID as the ImageView
     */
    public Movie getMovieInfo(ImageView imageView)
    {
        Movie movieObject = null;
        try
        {
            movieObject = bll.getMovieInfo(imageView);
        }
        catch (BLLException ex)
        {
            System.out.println(ex);
        }
        return movieObject;
    }

    /**
     * Passes the movie ID to bll and further down to dataaccess
     * in order to delete it in the db
     *
     * @param id            The ID of the Movie to remove
     * @param imageView     The imageView of the movie to remove
     * @param imageViewList The list to remove imageView from
     */
    public void removeMovie(int id)
    {
        try
        {
            bll.removeMovie(id);
        }
        catch (BLLException ex)
        {
            System.out.println(ex);
        }
    }

    /**
     * Opens a File in the system's default application
     *
     * @param file The file to try and open
     */
    public void openFileInNative(File file)
    {
        try
        {
            bll.openFileInNative(file);
        }
        catch (BLLException ex)
        {
            System.out.println(ex);
        }
    }

    /**
     * Sets the minimum rating to search for
     * passes the rating filter number as string to search class through bll
     *
     * @param rating The Rating for which to search
     */
    public void setRatingSearch(String rating)
    {
        bll.setRating(rating);
    }

    /**
     * Passes the sort ASC/DESC to search class through bll
     *
     * @param sort A String containing either Ascending or Descending
     */
    public void setSortOrder(String sort)
    {
        bll.setSort(sort);
    }

    /**
     * Passes the selected order used in search
     *
     * @param order A String dictating the search order
     */
    public void setOrderSearch(String order)
    {
        bll.setOrder(order);
    }

    /**
     * Passes the movieId to set the date of the last view
     *
     * @param id The ID of the Movie last viewed
     */
    public void setLastView(int id)
    {
        try
        {
            bll.setLastView(id);
        }
        catch (BLLException ex)
        {
            System.out.println(ex);
        }
    }

    /**
     * Passes and sets the movie genre in search through bll
     *
     * @param categories The String of categories
     */
    public void setSearchCategories(String categories)
    {
        bll.setSearchCategories(categories);
    }

    /**
     * Passes and sets the selected filter years
     *
     * @param years The String of years
     */
    public void setSearchYears(String years)
    {
        bll.setSearchYears(years);
    }

    /**
     * Sends the searched text to seach class to prepare a sql query
     *
     * @param text The String of text to search
     */
    public void setSearchText(String text)
    {
        bll.setSearchText(text);
    }


    /**
     * Calls to bll, which calls to the search class, to create a new query
     * form the given criteria
     * @return List of movies from the resultset
     */
    public List<Movie> prepareSearch()
    {
        List<Movie> movies = new ArrayList();
        try
        {
            movies = bll.prepareSearch();
        }
        catch (BLLException ex)
        {
            System.out.println(ex);
        }
        return movies;
    }

    /**
     * Calls to bll and from there further down to the db in order to 
     * retrieve all categories
     *
     * @return An observable List containing all the categories
     */
    public ObservableList<String> allCategories()
    {
        ObservableList<String> returnCats = null;
        try
        {
            returnCats = bll.allCategories();
        }
        catch (BLLException ex)
        {
            System.out.println(ex);
        }
        return returnCats;
    }

    /**
     * Splits a String up every time it sees a . (peroid)
     *
     *
     * @param stringToSplit String which may contain periods
     *
     * @return Returns the same String, but now slit up
     */
    public String splitDot(String stringToSplit)
    {
        return bll.splitDot(stringToSplit);
    }

    /**
     * Check if movie already exists in the database
     *
     * @param title The title of the Movie
     *
     * @return Returns true if it found a match, false if the movie doesn't
     *         exist in the database
     */
    public boolean movieAlreadyExisting(String title)
    {
        Boolean ifMovieExist = false;
        try
        {
            ifMovieExist = bll.movieAlreadyExisting(title);
        }
        catch (BLLException ex)
        {
            Logger.getLogger(MainWindowModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ifMovieExist;
    }

    /**
     * This method is to get a imgPath from a specific movie. So that it can be
     * thrown into the tilepane.
     *
     * @param title The title of the movie
     *
     * @return Returnd a String containing the URL for the Movie image
     */
    public String getSpecificMovieImage(String title)
    {
        String movieTitle = null;
        try
        {
            movieTitle = bll.getSpecificMovieImage(title);
        }
        catch (BLLException ex)
        {
            Logger.getLogger(MainWindowModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return movieTitle;
    }

}
