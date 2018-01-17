package movie.registraction.gui.model;

import com.jfoenix.controls.JFXCheckBox;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import movie.registraction.be.Movie;
import movie.registraction.bll.*;
import movie.registraction.dal.DALException;
import movie.registraction.gui.controller.EditMovieCategoryController;

/**
 *
 * @author Axl
 */
public class MainWindowModel
{

    List<ImageView> imageViewList;

    private BLLManager bll;
    private Search search = new Search();

    private final ObservableList<JFXCheckBox> genres;
    private final ObservableList<JFXCheckBox> years;
    private final ObservableList<JFXCheckBox> others;
    private final ObservableList<String> allCategories;
    private final ObservableList<Path> moviePaths;
    private final ObservableList<Path> changeList;

    private final int IMAGE_HEIGHT;
    private final int IMAGE_WIDTH;

    private final ArrayList<String> extensionList;

    private ChangeCategories categories;

    public MainWindowModel() throws DALException
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
        categories = new ChangeCategories();
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
     * Sets up a listener to the List connected to the Library Watcher
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

    private void updateLibrary()
    {
        try
        {
            bll.getMovieList(extensionList);
            bll.updateLibrary(bll.getMovieList(extensionList));
        }
        catch (BLLException ex)
        {
        }
    }

    /**
     * Makes a search on movies titles
     *
     * @param text
     */
    public void addMovie(String text, String filePath)
    {
        // Replace all the whitespaces with plus signs to make it URL friendly
        text = text.replaceAll(" ", "+");

        // Uses the API url + our fixed search index to display us all the
        // metadata of the movie searched for - if possible.
        URL searchLink;
        try
        {
            searchLink = bll.getOmdbTitleResult(text);

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
    }

    /**
     * Gets all categories from changeCategories class
     *
     * @return
     *
     */
    public ObservableList<String> getAllCategories()
    {
        try
        {
            allCategories.addAll(categories.allCategories());
        }
        catch (DALException ex)
        {
            System.out.println("Could not get the list of categories");
        }
        return allCategories;
    }

    /**
     * Gets all categories, is used in EditCategoryController
     *
     * @return all categories in observable list
     */
    public ObservableList<String> loadCategories() throws BLLException
    {
        return categories.loadCategories();
    }

    /**
     * Sends the category string to chosenCategories class to be added
     *
     * @param category
     */
    public void addChosenCategory(String category)
    {
        categories.addChosenCategory(category);
    }

    /**
     * Sends the category string to chosenCategories class to be removed
     *
     * @param category
     */
    public void removeChosenCategory(String category)
    {
        categories.removeChosenCategory(category);
    }

    /**
     * Save the category changes in changeCategories class
     */
    public void saveCategories()
    {
        try
        {
            categories.saveCategories();
        }
        catch (DALException ex)
        {
            System.out.println("Could not save categories");
        }

    }

    /**
     * Gets the allready exsisting categories for a specific movie
     *
     * @param movie
     *
     * @return Observable list of category strings
     */
    public ObservableList<String> loadChosenMovieCategories(Movie movie)
    {
        return categories.loadChosenMovieCategories(movie);
    }

    /**
     * Sends the category string to chosenCategories class to be added for a
     * movie
     *
     * @param category
     */
    public void addChosenMovieCategory(String category)
    {
        categories.addChosenMovieCategory(category);
    }

    /**
     * Sends the category string to chosenCategories class to be removed for a
     * movie
     *
     * @param category
     */
    public void removeChosenMovieCategory(String category)
    {
        categories.removeChosenMovieCategory(category);
    }

    /**
     * Save the movie category changes in changeCategories class
     */
    public void saveMovieCategories()
    {
        try
        {
            categories.saveMovieCategories();
        }
        catch (DALException ex)
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
     * @param tilePane
     * @param fileList
     *
     * @throws movie.registraction.dal.DALException
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
                    if (!bll.movieAlreadyExisting(nameOfMovie))
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

    public void setPictures(TilePane tilePane, File chosenFile, String imgUrl) throws DALException
    {
        ImageView imageView = new ImageView(imgUrl);
        imageView.setFitHeight(IMAGE_HEIGHT);
        imageView.setFitWidth(IMAGE_WIDTH);
        imageViewList.add(imageView);

        tilePane.getChildren().add(imageView);
        try
        {
            bll.setImageId(chosenFile, imageView);
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
     * @param contextMenu
     */
    public void closeMenuOrClick(ContextMenu contextMenu)
    {
        if (!contextMenu.isShowing())
        {
            System.out.println("You clicked on the picture.");
        }
        else
        {
            contextMenu.hide();
        }
    }

    /**
     * Checks whether contextmenu is open or not, if yes, it closes.
     * Incase user dobbleclicks several times, so it doesnt stack.
     *
     * @param contextMenu
     */
    public void contextMenuOpenOrNot(ContextMenu contextMenu)
    {
        // So the contextMenu doesnt stack.
        if (contextMenu.isShowing())
        {
            contextMenu.hide();
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
            lib = bll.loadDirectory("path.txt");
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
     * @param rating
     * @param ratingType
     * @param gridPaneRating
     * @param lblRating
     */
    public void setUpRating(double rating, String ratingType, GridPane gridPaneRating, Label lblRating)
    {
        try
        {
            Rating r = new Rating(rating, ratingType, gridPaneRating, lblRating);
        }
        catch (DALException ex)
        {
            System.out.println("Could not create new rating");
        }
    }

    /**
     * Gets the movie list
     *
     * @return
     */
    public ObservableList<Path> getMovieList()
    {
        return moviePaths;
    }

    /**
     * Returns list of the imageviews. // The images the user puts in.
     *
     * @return
     */
    public List<ImageView> GetImageViewList()
    {
        return imageViewList;
    }

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
     * @return Observablelist of checkboxes
     */
    public ObservableList<JFXCheckBox> getGenreList()
    {
        try
        {
            for (String category : categories.allCategories())
            {
                JFXCheckBox cb = new JFXCheckBox(category);
                cb.setOnMouseClicked(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent e)
                    {
                        search.setSearchCategories(cb.getText());
                    }
                });
                genres.add(cb);
            }
        }
        catch (DALException ex)
        {
            System.out.println("Could not get the list of categories");
        }

        return genres;
    }

    /**
     * Returns the list of CheckBoxes for the years
     *
     * @return Returns the list CheckBoxes for the years
     */
    public ObservableList<JFXCheckBox> getYearList()
    {
        for (int i = 0; i < 12; i++)
        {
            int j = 1900 + (i * 10);
            int q = 1900 + ((1 + i) * 10);
            JFXCheckBox cb = new JFXCheckBox(j + "-" + q);
            cb.setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent e)
                {
                    search.setSearchYears(cb.getText());
                }
            });

            years.add(cb);

        }

        return years;
    }

    /**
     * Tries to match ids of image and movie.
     *
     * @param imageView
     *
     * @return
     */
    public Movie getMovieIdMatch(ImageView imageView)
    {
        Movie idMatchMovie = null;
        try
        {
            idMatchMovie = bll.getMovieIdMatch(imageView);
        }
        catch (BLLException ex)
        {
            System.out.println(ex);
        }
        return idMatchMovie;
    }

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
     * @param tilePane
     */
    public void loadMoviesFromStart(TilePane tilePane)
    {
        ImageView imageView;
        imageViewList = new ArrayList();
        for (Movie movie : getAllMovies())
        {
            imageView = new ImageView("https:" + movie.getImgPath());
            imageView.setFitHeight(IMAGE_HEIGHT);
            imageView.setFitWidth(IMAGE_WIDTH);
            imageView.setId("" + movie.getId());
            imageViewList.add(imageView);
            tilePane.getChildren().add(imageView);
        }
    }

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

    public void setLastView(int movieId)
    {
        try
        {
            bll.setLastView(movieId);
        }
        catch (BLLException ex)
        {
            System.out.println(ex);
        }
    }
}
