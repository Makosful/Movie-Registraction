package movie.registraction.gui.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import movie.registraction.bll.Search;
import movie.registraction.dal.DALException;
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Instanciating">
    private MainWindowModel model;

    @FXML
    private FlowPane flpGenre;
    @FXML
    private FlowPane flpYear;
    @FXML
    private FlowPane flpOther;

    private int gridHeight;
    private int gridWidth;
    boolean popOverVisible;

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
    //</editor-fold>

    /**
     * Our initializer, which is run when the program has its initial start up.
     * We create our tile pane and also our context menu for the image view
     * followed by setting our popOvers visibility to false (used to bug fix)
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        SetupTilePane();
        setupContextMenu();
        popOverVisible = false;

        try
        {
            // Access the Model
            model = new MainWindowModel();
            model.loadMoviesFromStart(tilePane);
            imageClick();
        }
        catch (DALException ex)
        {
            System.out.println(ex);
        }

        // Set default values
        acdPanes.setExpandedPane(acdGenre);
        flpGenre.getChildren().setAll(model.getGenreList());
        flpYear.getChildren().setAll(model.getYearList());
       

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
        comBoxMinRating.getItems().addAll("min. 1 star", "min. 2 stars", "min. 3 stars", "min. 4 stars",
                                          "min. 5 stars", "min. 6 stars", "min. 7 stars", "min. 8 stars", "min. 9 stars");
    }

    /**
     * Searches for movies based on the title
     *
     * @param event
     */
    @FXML
    private void titleSearch(ActionEvent event)
    {
        //TODO model.addMovie(txtTitleSearch.getText());
    }

    /**
     * Clears the filters
     *
     * @param event
     */
    @FXML
    private void clearFilters(ActionEvent event)
    {
        model.fxmlClearFilters();
    }

    /**
     * Change the global categories
     *
     * @param event
     *
     * @throws IOException
     */
    @FXML
    private void btnChangeCategories(ActionEvent event) throws IOException
    {
        File fxml = new File("src/movie/registraction/gui/view/editCategories.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxml.toURL());
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

    /**
     * Allows for changing the category of a moviePoster, creates our new modal
     * window
     * and sets up the necessary
     * parameters for it to function and lastly display it to the user when
     * prompted.
     *
     * @param event
     *
     * @throws MalformedURLException
     * @throws IOException
     */
    @FXML
    private void btnChangeMovieCategory(ActionEvent event) throws MalformedURLException, IOException
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

        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Runs two methods which are described below (line 244)
     *
     * @param event
     */
    @FXML
    private void uploadFiles(ActionEvent event) throws DALException
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
    private void setChosenFilesWithPicture() throws DALException
    {
        model.chooseFile(tilePane);
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

    private void PopOverSetup(Movie movie, MouseEvent event)
    {
        //<editor-fold defaultstate="collapsed" desc="Foreach loop w/ moviePoster categories">
        /**
         * Creates a for each loop for all the movie categories and allows us to
         * display them.
         */
        String genreCategories = null;
        for (int i = 0; i < movie.getCategories().size(); i++)
        {
            if (genreCategories == null)
            {
                genreCategories = movie.getCategories().get(i);
            }
            else
            {
                genreCategories += movie.getCategories().get(i);
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

        lblPersonalRating.setText("Personal rating: " + movie.getPersonalRating() + "/10");
        lblPersonalRating.setStyle("-fx-text-fill: black");

        lblLastView.setText("Last viewed on: " + movie.getLastView());
        lblLastView.setStyle("-fx-text-fill: black");

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

        popGrid.add(lblMovieTitle, 0, 0);
        popGrid.add(lblGenre, 0, 1);
        popGrid.add(lblYear, 0, 2);
        popGrid.add(lblImdbRating, 0, 3);
        popGrid.add(lblPersonalRating, 0, 4);
        popGrid.add(lblLastView, 0, 5);
        popGrid.add(imdbURL, 0, 6);

        gridHeight = 300;
        gridWidth = 400;
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
     * meta data. Stores them and allows us to click the moviePoster poster in our image
     * view and display the Popover info panel which in turn displays all the relevant moviePoster information 
     * (apart from production team, actor + actress list and plot)
     * Also allows us to display a context menu where we enable the ability to
     * 1: Play the moviePoster (with systems standard media player)
     * 2: Edit data for each moviePoster and lastly 3: Delete the moviePoster from the database
     */
    private void imageClick()
    {
        model.GetImageViewList().forEach((imageView) ->
        {
            Movie moviePoster = model.getMovieInfo(imageView);

            imageView.setOnMouseClicked((MouseEvent event) ->
            {
                MouseButton mouseButton = event.getButton();
                
                if (mouseButton == MouseButton.PRIMARY)
                {
                    
                    PopOverSetup(moviePoster, event);
                    System.out.println(moviePoster.getMovieLength());
                    model.contextMenuOpenOrNot(contextMenu);
                }
                
                else if (mouseButton == MouseButton.SECONDARY)
                {
                    closePopOverIfRightClick();
                    contextMenuAction(imageView, moviePoster);
                    model.contextMenuOpenOrNot(contextMenu);
                    contextMenu.show(tilePane, event.getScreenX() - 5, event.getScreenY() - 5);
                }
            });
        });
    }
    
    /**
     * Sets the library
     * @param event
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
        editData = new MenuItem("Edit Metadata TODO");
        deleteMovie = new MenuItem("Delete Movie");
        contextMenu.getItems().addAll(playMovie, editData, deleteMovie);

    }

    /**
     * Making the setOnActions for context menu.
     * @param imageView
     * @param movie
     */
    public void contextMenuAction(ImageView imageView, Movie movie)
    {
        //<editor-fold defaultstate="collapsed" desc="setOnAction">
        playMovie.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.out.println(movie.getFilePath());
                model.openFileInNative(new File(movie.getFilePath()));
                contextMenu.hide();
            }
        });

        editData.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.out.println("2");
                contextMenu.hide();
            }
        });

        deleteMovie.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                deleteMovie(imageView, movie);
                contextMenu.hide();
            }
        });
        //</editor-fold>
    }

    /**
     * Deletes movie.
     * @param imageView
     * @param movie
     */
    private void deleteMovie(ImageView imageView, Movie movie)
    {
        tilePane.getChildren().remove(imageView);
        model.removeMovie(movie.getId());
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
}
