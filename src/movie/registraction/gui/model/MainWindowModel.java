package movie.registraction.gui.model;

import com.jfoenix.controls.JFXCheckBox;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import movie.registraction.be.Movie;
import movie.registraction.bll.BLLManager;
import movie.registraction.bll.Rating;
import movie.registraction.bll.exception.BLLException;
import movie.registraction.dal.exception.DALException;

/**
 *
 * @author Axl
 */
public class MainWindowModel
{

    List<ImageView> imageViewList;

    private BLLManager bll;

    private final ObservableList<JFXCheckBox> genres;
    private final ObservableList<JFXCheckBox> years;
    private final ObservableList<JFXCheckBox> others;
    private final ObservableList<String> allCategories;
    private final ObservableList<Path> moviePaths;
    private final ObservableList<Path> changeList;

    private final int IMAGE_HEIGHT;
    private final int IMAGE_WIDTH;

    private final ArrayList<String> extensionList;

    /**
     * The constructor
     */
    public MainWindowModel()
    {
        IMAGE_HEIGHT = 200;
        IMAGE_WIDTH = 150;

        try
        {
            bll = new BLLManager();
        }
        catch (BLLException ex)
        {
        }

        years = FXCollections.observableArrayList();
        genres = FXCollections.observableArrayList();
        others = FXCollections.observableArrayList();
        changeList = bll.getChangeList();

        moviePaths = FXCollections.observableArrayList();
        allCategories = FXCollections.observableArrayList();

        extensionList = new ArrayList();
        extensionList.add(".jpg");
        extensionList.add(".png");
        extensionList.add(".mp4");
        extensionList.add(".mpeg4");
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
                for (String filePath : updateLibrary)
                {
                    String fileName = new File(filePath).toPath().getFileName().toString();
                    this.addMovie(fileName, filePath);
                }
            }
        }
        catch (BLLException ex)
        {
        }
    }

    /**
     * Makes a search on movies titles
     *
     * @param title    The title of the movie to add
     * @param filePath The local path of the movie to add
     */
    public void addMovie(String title, String filePath)
    {
        // Replace all the whitespaces with plus signs to make it URL friendly
        title = title.replaceAll(" ", "+");

        // Uses the API url + our fixed search index to display us all the
        // metadata of the movie searched for - if possible.
        URL searchLink;
        try
        {
            searchLink = bll.getOmdbTitleResult(title);

            String[] metaData = bll.getSearchMetaData(searchLink);
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
     * Clears the filters.
     * Not in use
     */
    public void fxmlClearFilters()
    {
        try
        {
            bll.clearFilters();
        }
        catch (BLLException ex)
        {
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
     * @param tilePane The TilePane which to setup
     */
    public void chooseFile(TilePane tilePane)
    {
        // Creates a new FileChooser object
        FileChooser fc = new FileChooser();

        // Defines what files it will look for
        FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter("Video Files", "*.mp4", ".mpeg4");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");

        // Adds the filters
        fc.getExtensionFilters().addAll(videoFilter, imageFilter);

        // Opens the FileChooser and saves the results in a list
        List<File> chosenFiles = fc.showOpenMultipleDialog(null);

        // Checks if any files where chosen
        if (chosenFiles != null)
        {
            for (File chosenFile : chosenFiles)
            {
                String nameOfMovie = bll.splitDot(chosenFile.getName());

                try
                {
                    if (!bll.movieAlreadyExisting(nameOfMovie.toLowerCase()))
                    {

                        addMovie(nameOfMovie, chosenFile.getPath());
                        String imgPath = bll.getSpecificMovieImage(bll.splitDot(chosenFile.getName()));
                        imgPath = "https:" + imgPath;
                        setPictures(tilePane, chosenFile, imgPath);

                    }
                    else
                    {
                        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        Alert alert = new Alert(AlertType.ERROR, "Selected Movie(s) has already been added",
                                                okButton);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == okButton)
                        {
                            alert.close();
                        }
                    }
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        }
        else
        {
            // Otherwise return
            System.out.println("One or more invalid file(s) / None selected");
        }
    }

    /**
     * Making imageViews, setting their sizes, adding them to an arraylist,
     * adding them to our tilepane, and finally giving the imageviews an id,
     * that will refer to the actual movie.
     *
     * @param tilePane The tilePane to set the image into
     * @param image    The picture to set in
     * @param url      The URL of the image
     */
    public void setPictures(TilePane tilePane, File image, String url)
    {
        ImageView imageView = new ImageView(url);
        imageView.setFitHeight(IMAGE_HEIGHT);
        imageView.setFitWidth(IMAGE_WIDTH);
        imageViewList.add(imageView);

        tilePane.getChildren().add(imageView);
        try
        {
            bll.setImageId(image, imageView);
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
     * @param rating         The rating to set, as a double
     * @param ratingType     The type of rating
     * @param gridPaneRating The GridPane in which to set the rating
     * @param lblRating      The label in which to set the rating
     */
    public void setUpRating(double rating, String ratingType, GridPane gridPaneRating, Label lblRating) throws DALException
    {
        Rating r = new Rating(rating, ratingType, gridPaneRating, lblRating);
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
     * Returns list of the imageviews. // The images the user puts in.
     *
     * @return Return a List of ImageViews
     */
    public List<ImageView> getImageViewList()
    {
        return imageViewList;
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
     * Gets the list of Genres
     *
     * @return Returns a List of JFXCheckBoxes with all the genres
     */
    public ObservableList<JFXCheckBox> getGenreList()
    {
        try
        {
            for (String category : bll.allCategories())
            {
                JFXCheckBox cb = new JFXCheckBox(category);

                genres.add(cb);
            }
        }
        catch (BLLException ex)
        {
            System.out.println("Could not get the list of categories");
        }

        return genres;
    }

    /**
     * Returns the list of CheckBoxes for the years
     *
     * @return Returns a List of JFXCheckBoxes with all the
     */
    public ObservableList<JFXCheckBox> getYearList()
    {
        for (int i = 0; i < 12; i++)
        {
            int j = 1900 + (i * 10);
            int q = 1900 + ((1 + i) * 10);
            JFXCheckBox cb = new JFXCheckBox(j + "-" + q);

            years.add(cb);

        }

        return years;
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
     * This loads all the movies from start.
     *
     * @param tilePane The TilePane in which to add the Movies
     * @param movies   The List of Movies to add to the TilePane
     */
    public void loadMovies(TilePane tilePane, List<Movie> movies)
    {
        ImageView imageView;
        imageViewList = new ArrayList();
        for (Movie movie : movies)
        {
            imageView = new ImageView("https:" + movie.getImgPath());
            imageViewSizeAndId(imageView, movie);
            imageViewList.add(imageView);
            tilePane.getChildren().add(imageView);
        }
    }

    /**
     * Passes the movie ID to bll and further down to dataaccess
     * in order to delete it in the db
     *
     * @param id The IF of the Movie to remove
     */
    public void removeMovie(int id, ImageView imageView)
    {
        try
        {
            bll.removeMovie(id);
            imageViewList.remove(imageView);
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
     * Adds Movies to the TilePane
     * Gets the seach result in form of a list of movies, which is looped throuh
     * adding a new imageView/poster to the tilePane
     *
     * @param tilePane The TilePane which will be given the ImageViews
     */
    public void prepareSearch(TilePane tilePane)
    {
        imageViewList.clear();
        tilePane.getChildren().clear();
        try
        {
            for (Movie movie : bll.prepareSearch())
            {
                ImageView imageView = new ImageView("https:" + movie.getImgPath());
                imageViewSizeAndId(imageView, movie);
                imageViewList.add(imageView);
                tilePane.getChildren().add(imageView);
            }
        }
        catch (BLLException ex)
        {
            System.out.println("Could not retrieve the searchresult of movies");
        }
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
     * Sets the imageView/poster dimentions and id
     *
     * @param imageView The ImageView to set
     * @param movie     The Movie from which to get the data
     */
    public void imageViewSizeAndId(ImageView imageView, Movie movie)
    {
        imageView.setFitHeight(IMAGE_HEIGHT);
        imageView.setFitWidth(IMAGE_WIDTH);
        imageView.setId("" + movie.getId());
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

}
