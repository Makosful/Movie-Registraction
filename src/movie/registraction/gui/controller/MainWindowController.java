package movie.registraction.gui.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import movie.registraction.be.Movie;
import movie.registraction.gui.model.MainWindowModel;
import org.controlsfx.control.PopOver;

/**
 *
 * @author Axl
 */
public class MainWindowController implements Initializable
{

    //<editor-fold defaultstate="collapsed" desc="FXML Variables">
    @FXML
    private ScrollPane scrlFilterSearch;
    @FXML
    private TextField txtTitleSearch;
    @FXML
    private Button btnTitleSearch;
    @FXML
    private Button btnClearFilters;
    @FXML
    private TitledPane acdGenre;
    @FXML
    private TitledPane acdYear;
    @FXML
    private TitledPane acdOther;
    @FXML
    private Accordion acdPanes;
    private JFXListView<JFXCheckBox> lstGenre;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ComboBox<String> comBoxSortOrder;
    @FXML
    private ComboBox<String> comBoxMinRating;
    @FXML
    private RadioButton rBTitle;
    @FXML
    private ToggleGroup rbToggleGrp;
    @FXML
    private RadioButton rBRating;
    @FXML
    private TilePane tilePane;
    @FXML
    private Button btnSetLibrary;
    @FXML
    private FlowPane flpGenre;
    @FXML
    private FlowPane flpYear;
    @FXML
    private FlowPane flpOther;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Different Variables">
    private MainWindowModel model;

    private int gridHeight;
    private int gridWidth;
    boolean popOverVisible;

    private final int IMAGE_HEIGHT;
    private final int IMAGE_WIDTH;

    List<ImageView> imageViewList;

    Movie moviePoster;

    VBox vBox;
    Hyperlink imdbURL;
    PopOver popOver;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Context menu">
    ContextMenu contextMenu;
    MenuItem playMovie;
    MenuItem editData;
    MenuItem deleteMovie;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Labels">
    Label lblMovieTitle;
    Label lblGenre;
    Label lblYear;
    Label lblImdbRating;
    Label lblPersonalRating;
    Label lblMovieLength;
    Label lblLastView;
    Label lblIMDBId;
    Label lblRating;
    Label lblRatingImdb;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="JFXCheckboxlists">
    private final ObservableList<JFXCheckBox> genres;
    private final ObservableList<JFXCheckBox> years;
//</editor-fold>

    public MainWindowController()
    {
        IMAGE_HEIGHT = 150;
        IMAGE_WIDTH = 125;

        years = FXCollections.observableArrayList();
        genres = FXCollections.observableArrayList();
    }

    /**
     * Our initializer, which is run when the program has its initial start up.
     * We create our tile pane and also our context menu for the image view
     * followed by setting our popOvers visibility to false (used to bug fix)
     *
     * @param location  The location used to resolve relative paths for the root
     *                  object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null
     *                  if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        SetupTilePane();
        setupContextMenu();
        popOverVisible = false;

        // Access the Model
        model = new MainWindowModel();
        loadMovies(model.getAllMovies());
        imageClick();

        defaultValues();

        //Initializing methods
        comboBoxSetup();

        model.findOldAndBadMovies();
    }

    /**
     * Sets up the combo boxes
     */
    private void comboBoxSetup()
    {
        comBoxSortOrder.getItems().addAll("Ascending", "Descending");
        comBoxMinRating.getItems().addAll("All", "min. 1 star", "min. 2 stars", "min. 3 stars", "min. 4 stars",
                                          "min. 5 stars", "min. 6 stars", "min. 7 stars", "min. 8 stars", "min. 9 stars");
    }

    /**
     * Searches for movies based on the title
     *
     * @param event The event that called this method
     */
    @FXML
    private void titleSearch(ActionEvent event)
    {
        model.setSearchText(txtTitleSearch.getText());
        prepareSearch();
        imageClick();
    }

    /**
     * Clears the filters
     *
     * @param event The event that called this method
     */
    @FXML
    private void clearFilters(ActionEvent event)
    {
        model.fxmlClearFilters();
        prepareSearch();
        
        //uncheck checkboxes
        getGenreList().forEach((cb) ->
        {
            cb.selectedProperty().set(false);
        });
        getYearList().forEach((cb) ->
        {
            cb.selectedProperty().set(false);
        }); 
        
        //set comboboxes to default / clear selcted value
        comBoxSortOrder.getSelectionModel().clearSelection();
        comBoxMinRating.getSelectionModel().clearSelection();
    }

    /**
     * Change the global categories
     *
     * @param event The event that called this method
     *
     */
    @FXML
    private void btnChangeCategories(ActionEvent event)
    {
        File fxml = new File("src/movie/registraction/gui/view/editCategories.fxml");
        FXMLLoader fxmlLoader;
        try
        {
            fxmlLoader = new FXMLLoader(fxml.toURL());
            Parent root;
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(anchorPane.getScene().getWindow());
            EditCategoriesController controller;
            controller = fxmlLoader.getController();

            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (MalformedURLException ex)
        {
            System.out.println(ex);
        }
        catch (IOException ex)
        {
            System.out.println(ex);
        }
    }

    /**
     * Allows for changing the category of a moviePoster, creates our new modal
     * window
     * and sets up the necessary
     * parameters for it to function and lastly display it to the user when
     * prompted.
     *
     * @param event The event that called this method
     */
    private void openChangeMovieCategoriesWindow(Movie selectedMovie)
    {
        try
        {
            File fxml = new File("src/movie/registraction/gui/view/EditMovieCategory.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxml.toURL());
            Parent root;
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(anchorPane.getScene().getWindow());
            EditMovieCategoryController controller;
            controller = fxmlLoader.getController();
            controller.changeMovieCategories(selectedMovie);
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (MalformedURLException ex)
        {
            System.out.println(ex);
        }
        catch (IOException ex)
        {
            System.out.println(ex);
        }
    }

    /**
     * Runs two methods which are described below (line 244)
     *
     * @param event The event that called this method
     */
    @FXML
    private void uploadFiles(ActionEvent event)
    {
        setChosenFilesWithPicture();
    }

    /**
     * Firstly creates a file chooser and allows us to select multiple files
     * with certain extentions (mp4 etc.),
     * then runs the code that loops through each file (has description on
     * line 388)
     * Setting the moviePoster files and picture.
     */
    private void setChosenFilesWithPicture()
    {
        chooseFiles();
        imageClick();
    }

    /**
     * Sets up the TilePane with the necessary binds to the width & height
     * and gaps for each moviePoster poster.
     */
    private void SetupTilePane()
    {
        tilePane.prefWidthProperty().bind(scrlFilterSearch.widthProperty());
        tilePane.prefHeightProperty().bind(scrlFilterSearch.heightProperty());
        tilePane.setHgap(20);
        tilePane.setVgap(20);
    }

    /**
     * Sets up the popover window
     *
     * @param movie The Movie which it clicked on
     * @param event The mouse that clicked
     */
    private void PopOverSetup(Movie movie, MouseEvent event)
    {
        //<editor-fold defaultstate="collapsed" desc="Foreach loop w/ moviePoster categories">
        /**
         * Creates a for each loop for all the movie categories and allows us to
         * display them.
         * Code makes a new line, and throws the remaining genres down,
         * once 4, 8 or 12 categories have been added. This is due to space.
         */
        String genreCategories = null;
        for (int i = 0; i < movie.getCategories().size(); i++)
        {
            if (genreCategories == null)
            {
                genreCategories = movie.getCategories().get(i);
            }
            else if (i == 4 || i == 8 || i == 12)
            {
                genreCategories += "\n" + movie.getCategories().get(i);
            }
            else
            {
                genreCategories += " " + movie.getCategories().get(i);
            }
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="PopOver Content">
        lblMovieTitle = new Label();
        lblGenre = new Label();
        lblPersonalRating = new Label();
        lblMovieLength = new Label();
        lblLastView = new Label();
        lblImdbRating = new Label();
        lblYear = new Label();
        imdbURL = new Hyperlink();

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="labelSetText">
        lblMovieTitle.setText("Title: " + movie.getMovieTitle());
        lblMovieTitle.setStyle("-fx-text-fill: black");

        lblGenre.setText("Genres: " + genreCategories);
        lblGenre.setStyle("-fx-text-fill: black");

        lblYear.setText("Release year: " + movie.getYear());
        lblYear.setStyle("-fx-text-fill: black");

        lblMovieLength.setText("Length: " + movie.getMovieLength() + "minutes");
        lblMovieLength.setStyle("-fx-text-fill: black");

        lblImdbRating.setText("IMDB rating: " + movie.getImdbRating() + "/10");
        lblImdbRating.setStyle("-fx-text-fill: black");

        if (movie.getPersonalRating() != -1)
        {
            lblPersonalRating.setText("Personal rating: " + movie.getPersonalRating() + "/10");
        }
        else
        {
            lblPersonalRating.setText("Personal rating: Not rated");
        }
        lblPersonalRating.setStyle("-fx-text-fill: black");

        lblLastView.setText("Last viewed on: " + movie.getLastView());
        lblLastView.setStyle("-fx-text-fill: black");

        lblRating = new Label("");
        lblRating.setStyle("-fx-text-fill: black");

        lblRatingImdb = new Label();
        lblRatingImdb.setStyle("-fx-text-fill: black");

        //HYPERLINK
        imdbURL.setText("http://www.imdb.com/title/" + movie.getImdbLink());
        //imdbURL.setStyle("-fx-text-fill: black");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Gridpane Setup">
        GridPane popGrid = new GridPane();
        popGrid.getStylesheets().add("movie/registraction/css/gridpane.css");
        popGrid.setPadding(new Insets(30));
        popGrid.setHgap(20);
        popGrid.setVgap(10);

        GridPane ratingGrid = new GridPane();
        ratingGrid.setStyle("-fx-cursor: hand");

        model.setUpRating(movie.getId(), movie.getPersonalRating(), ratingGrid, lblRating);

        popGrid.add(lblMovieTitle, 0, 0);
        popGrid.add(lblGenre, 0, 1);
        popGrid.add(lblYear, 0, 2);
        popGrid.add(lblImdbRating, 0, 3);
        popGrid.add(lblPersonalRating, 0, 4);
        popGrid.add(lblLastView, 0, 5);
        popGrid.add(imdbURL, 0, 6);
        popGrid.add(ratingGrid, 0, 7);
        popGrid.add(lblRating, 1, 7);

        gridHeight = 350;
        gridWidth = 425;
        popGrid.setPrefSize(gridWidth, gridHeight);
        //</editor-fold>

        if (popOverVisible == false)
        {
            popOver = new PopOver(popGrid);
            //popOver.getRoot().getStylesheets().add("movie/registraction/css/popover.css"); //Not working ??? ControlsFX issue
            popOver.show(tilePane, event.getScreenX() + 5, event.getScreenY());
            popOverVisible = true;
        }
        else if (popOverVisible)
        {
            popOver.hide();
            popOver = new PopOver(popGrid);
            popOver.show(tilePane, event.getScreenX() + 5, event.getScreenY());
        }

        if (popOverVisible)
        {
            popOverVisible = true;
            popOver.hide();
            popOver = null;
            popOver = new PopOver(popGrid);
            popOver.show(tilePane, event.getScreenX() + 5, event.getScreenY());
        }
        popOver.setDetachable(false);
        popOver.setDetached(false);
        popOver.setHeaderAlwaysVisible(true);

        popOver.setTitle("Movie Metadata");

        /**
         * Automatically hides the PopOver if the context menu is showing
         */
        if (contextMenu.isShowing())
        {
            popOver.hide();
        }
    }

    /**
     * Loops through all the images in the image view and pulls the moviePoster
     * meta data. Stores them and allows us to click the moviePoster poster in
     * our image
     * view and display the Popover info panel which in turn displays all the
     * relevant moviePoster information
     * (apart from production team, actor + actress list and plot)
     * Also allows us to display a context menu where we enable the ability to
     * 1: Play the moviePoster (with systems standard media player)
     * 2: Edit data for each moviePoster and lastly 3: Delete the moviePoster
     * from the database
     */
    private void imageClick()
    {
        imageViewList.forEach((imageView) ->
        {
            imageView.setOnMouseClicked((MouseEvent event) ->
            {
                MouseButton mouseButton = event.getButton();

                if (mouseButton == MouseButton.PRIMARY)
                {
                    // getting movieInfo everytime u click, to stay updated with database.
                    moviePoster = model.getMovieInfo(imageView);
                    PopOverSetup(moviePoster, event);
                    System.out.println(moviePoster.getMovieLength());
                    contextMenuOpenOrNot();
                }

                else if (mouseButton == MouseButton.SECONDARY)
                {
                    moviePoster = model.getMovieInfo(imageView);
                    closePopOverIfRightClick();
                    contextMenuAction(imageView, moviePoster);
                    contextMenuOpenOrNot();
                    contextMenu.show(tilePane, event.getScreenX() - 5, event.getScreenY() - 5);
                }
            });
        });
    }

    /**
     * Sets the library
     *
     * @param event The event that called this method
     */
    @FXML
    private void setLibrary(ActionEvent event)
    {
        model.fxmlSetLibrary();
    }

    /**
     * Initialize context menu and menu items.
     */
    private void setupContextMenu()
    {
        contextMenu = new ContextMenu();
        playMovie = new MenuItem("Play Movie");
        editData = new MenuItem("Edit Categories");
        deleteMovie = new MenuItem("Delete Movie");
        contextMenu.getItems().addAll(playMovie, editData, deleteMovie);
    }

    /**
     * Making the setOnActions for context menu.
     *
     * @param imageView The ImageView to set the ContextMenu for
     * @param movie     The Movie within
     */
    public void contextMenuAction(ImageView imageView, Movie movie)
    {
        //<editor-fold defaultstate="collapsed" desc="setOnAction">
        playMovie.setOnAction((ActionEvent event) ->
        {
            System.out.println(movie.getFilePath());
//                model.openFileInNative(new File(movie.getFilePath()));
            model.setLastView(movie.getId());
            PlayMovieCustomPlayer(imageView);
            contextMenu.hide();
        });

        editData.setOnAction((ActionEvent event) ->
        {
            openChangeMovieCategoriesWindow(movie);
            contextMenu.hide();
        });

        deleteMovie.setOnAction((ActionEvent event) ->
        {
            removeMovie(imageView, movie);
            contextMenu.hide();
        });
        //</editor-fold>
    }

    /**
     * Deletes movie from database, tilepane and arraylist which cointains
     * imageviews.
     *
     * @param imageView The ImageView to delete
     * @param movie     The Movie within
     */
    private void removeMovie(ImageView imageView, Movie movie)
    {
        tilePane.getChildren().remove(imageView);
        model.removeMovie(movie.getId());
        imageViewList.remove(imageView);

    }

    /**
     * Closes the popover incase user rights click to open context menu.
     * This is so both windows aren't open at the same time.
     */
    private void closePopOverIfRightClick()
    {
        if (popOverVisible)
        {
            popOver.hide();
        }
    }

    /**
     * Filtering after minimum stars.
     *
     * @param event
     *              Handles the minimun rating
     *
     * @param event The event that called this method
     */
    @FXML
    private void comBoxMinRatingHandler(ActionEvent event)
    {
        model.setRatingSearch(comBoxMinRating.getSelectionModel().getSelectedItem());
        prepareSearch();
        imageClick();
    }

    /**
     * Filtering after the radiobuttons, "title" or "rating"
     *
     * @param event
     *              /**
     *              Handles the sort order
     *
     * @param event The event that called this method
     */
    @FXML
    private void setOrderHandler(ActionEvent event)
    {

        RadioButton orderRadiobtn = (RadioButton) rbToggleGrp.getSelectedToggle();
        model.setOrderSearch(orderRadiobtn.getText());
        prepareSearch();
        imageClick();
    }

    /**
     * Filtering after descending or ascending, based on title or rating.
     * Handles the sot order in the combo box
     *
     * @param event The event that called this method
     */
    @FXML
    private void comBoxSortOrderHandler(ActionEvent event)
    {
        model.setSortOrder(comBoxSortOrder.getSelectionModel().getSelectedItem());
        prepareSearch();
        imageClick();
    }

    /**
     * Opens a Movie in our custom media player
     *
     * @param imageView The ImageView with the Movie to play
     *
     * @throws IOException
     */
    private void PlayMovieCustomPlayer(ImageView imageView)
    {
        FXMLLoader fxmlLoader;
        try
        {
            fxmlLoader = new FXMLLoader(getClass().getResource("/movie/registraction/gui/view/MediaWindow.fxml"));
            Parent root;
            root = fxmlLoader.load();
            
            MediaWindowController controller;
            controller = fxmlLoader.getController();
            controller.setImageView(imageView);
            controller.setImageView(imageView);
            controller.MediaSetup(model);
            
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(anchorPane.getScene().getWindow());
            stage.setMinHeight(700);
            stage.setMinWidth(825);
            stage.setScene(new Scene(root));
            stage.show();
        }

        catch (IOException ex)
        {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Expanding the accordion panes to the titled pane.
     * Adding genre and year check boxes to the different flowpanes.
     * Mouse events, every click is registered on checkbox,
     * we make the filtering and register context menus, pop overs etc to the
     * images/movie.
     * Sets the default values
     */
    private void defaultValues()
    {
        // Set default values
        acdPanes.setExpandedPane(acdGenre);

        flpGenre.getChildren().setAll(getGenreList());
        flpYear.getChildren().setAll(getYearList());

        getGenreList().forEach((cb) ->
        {
            cb.setOnMouseClicked((MouseEvent e) ->
            {
                model.setSearchCategories(cb.getText());
                prepareSearch();
                imageClick();
            });
        });

        getYearList().forEach((cb) ->
        {
            cb.setOnMouseClicked((MouseEvent e) ->
            {
                model.setSearchYears(cb.getText());
                prepareSearch();
                imageClick();
            });
        });
    }

    /**
     * This loads all the movies from start.
     *
     * @param movies The List of Movies to add to the TilePane
     */
    public void loadMovies(List<Movie> movies)
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
     * Sets the imageView/poster dimensions and id
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
     * Adds Movies to the TilePane
     * Gets the search result in form of a list of movies, which is looped
     * through
     * adding a new imageView/poster to the tilePane
     */
    public void prepareSearch()
    {
        imageViewList.clear();
        tilePane.getChildren().clear();

        model.prepareSearch().stream().map((movie) ->
        {
            ImageView imageView = new ImageView("https:" + movie.getImgPath());
            imageViewSizeAndId(imageView, movie);
            return imageView;
        }).map((imageView) ->
        {
            imageViewList.add(imageView);
            return imageView;
        }).forEachOrdered((imageView) ->
        {
            tilePane.getChildren().add(imageView);
        });
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
     * Gets the list of Genres
     *
     * @return Returns a List of JFXCheckBoxes with all the genres
     */
    public ObservableList<JFXCheckBox> getGenreList()
    {
        model.allCategories().stream().map((category) -> new JFXCheckBox(category)).forEachOrdered((cb) ->
        {
            genres.add(cb);
        });
        return genres;
    }

    /**
     * Making imageViews, setting their sizes, adding them to an array list,
     * adding them to our tile pane, and finally giving the image views an id,
     * that will refer to the actual movie.
     *
     * @param image The picture to set in
     * @param url   The URL of the image
     */
    public void setPictures(File image, String url)
    {
        ImageView imageView = new ImageView(url);
        imageView.setFitHeight(IMAGE_HEIGHT);
        imageView.setFitWidth(IMAGE_WIDTH);
        imageViewList.add(imageView);

        tilePane.getChildren().add(imageView);
        model.setImageId(image, imageView);
    }

    /**
     * Adds movies to db and program if they don't exist in db/program.
     */
    private void chooseFiles()
    {
        for (File chosenFile : model.chooseFile())
        {
            // Boolean for if it is a IMDB movie.
            Boolean imdbMovie = false;
            // Boolean for if movie is not in database;
            Boolean movieNotInDatabase = false;
            String[] metaData = null;
            String nameOfMovie = model.splitDot(chosenFile.getName());
            try
            {
             metaData = model.getMovieMetaData(nameOfMovie);
             imdbMovie = true;
            }
            catch(Exception e)
            {
                imdbMovie = false;
                alertButtonMovieAlreadyExist("Movie(s) does not exist on IMDB");
            }

            if (imdbMovie && !model.movieAlreadyExisting(metaData[0].toLowerCase()))
            {
                movieNotInDatabase = true;
                model.addMovie(metaData, chosenFile.getPath());
                String imgPath = model.getSpecificMovieImage(model.splitDot(chosenFile.getName()));
                imgPath = "https:" + imgPath;
                setPictures(chosenFile, imgPath);
            }
            if(!movieNotInDatabase && imdbMovie)
            {
                alertButtonMovieAlreadyExist("Selected Movie(s) has already been added");
            }
        }
    }

    /**
     * Alert if movie already exists.
     */
    private void alertButtonMovieAlreadyExist(String message)
    {
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.ERROR, message,
                                okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == okButton)
        {
            alert.close();
        }
    }

    /**
     * Checks whether context menu is open or not, if yes, it closes.
     * Incase user double clicks several times, so it doesn't stack.
     */
    public void contextMenuOpenOrNot()
    {
        // So the contextMenu doesnt stack.
        if (contextMenu.isShowing())
        {
            contextMenu.hide();
        }
    }
}
