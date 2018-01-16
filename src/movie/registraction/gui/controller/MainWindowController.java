package movie.registraction.gui.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
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

    // Model
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

    ContextMenu contextMenu;
    MenuItem play;
    MenuItem editData;
    MenuItem deleteMovie;
    

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
     * Constructor for all intents and purposes
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
        flpYear.getChildren().setAll(model.getYearNodes());
        flpOther.getChildren().setAll(model.getOtherNodes());

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
        comBoxMinRating.getItems().addAll("min. 1 star", "min. 2 stars", "min. 3 stars", "min. 4 stars", "min. 5 stars", "min. 6 stars", "min. 7 stars", "min. 8 stars", "min. 9 stars");
    }

    /**
     * TODO
     */
    private void modalWindowSetup()
    {

    }

    /**
     * Searches for movies based on the title
     *
     * @param event
     */
    @FXML
    private void titleSearch(ActionEvent event)
    {
       // model.addMovie(txtTitleSearch.getText());
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
     * Change the category of a movie
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
     * Adds files from outside the library to the program
     *
     * @param event
     */
    @FXML
    private void uploadFiles(ActionEvent event) throws DALException
    {
        setChosenFilesWithPicture();
    }

    /**
     * Setting the movie files and picture.
     */
    private void setChosenFilesWithPicture() throws DALException
    {
        model.chooseFile(tilePane);
        imageClick();
    }

    /**
     * Binds the TilePane to the ScrollPane, height n width.
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
        String genreCategories = null;
        for (int i = 0; i < movie.getCategories().size(); i++)
        {
            if (genreCategories == null)
            {
                genreCategories = movie.getCategories().get(i);
            }
            else
            {
                genreCategories += "\n" + movie.getCategories().get(i);
                System.out.println(genreCategories);
            }
        }

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

        //<editor-fold defaultstate="collapsed" desc="PopOver Gridpane Setup">
        GridPane popGrid = new GridPane();
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
            System.out.println("boolean is false");
            popOver = new PopOver(popGrid);
            popOver.show(tilePane, event.getScreenX() + 5, event.getScreenY());
            popOverVisible = true;
        }
        else if(popOverVisible)
        {
         popOver.hide();
         popOver = new PopOver(popGrid);
            popOver.show(tilePane, event.getScreenX() + 5, event.getScreenY());
            
         
            System.out.println("boolean is not true");
        }

//        if (!popOver.isShowing())
//        {
//            popOver = new PopOver(popGrid);
//            popOver.show(tilePane, event.getScreenX() + 5, event.getScreenY());
//        }

        if (popOver.isShowing() && popOverVisible)
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

        // So you can close the contextmenu when clicking on picture
        // without opening the popover.
        if(contextMenu.isShowing())
        {
            popOver.hide();
        }

    }

    /**
     * Code so you can click or right click on an image and something happens.
     * Mouse event.
     */
    private void imageClick()
    {
        for (ImageView imageView : model.GetImageViewList())
        {
            Movie movie = model.getMovieInfo(imageView);

            imageView.setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent me)
                {
                    MouseButton mouseButton = me.getButton();
                    if (mouseButton == MouseButton.PRIMARY)
                    {
                        PopOverSetup(movie, me);
                        System.out.println(movie.getMovieTitle());
                        model.contextMenuOpenOrNot(contextMenu);
                    }

                    if (mouseButton == MouseButton.SECONDARY)
                    {
                        closePopOverIfRightClick();
                        Movie movie = model.getMovieInfo(imageView);
                        contextMenuAction(imageView, movie);
                        model.contextMenuOpenOrNot(contextMenu);
                        contextMenu.show(tilePane, me.getScreenX(), me.getScreenY());
                    }
                }
            });
        }
    }

    /**
     * Sets the library
     *
     * @param event
     */
    @FXML
    private void setLibrary(ActionEvent event)
    {
        model.fxmlSetLibrary();
    }

    /**
     * Initialize contextmenu and menuitems.
     *
     * @param tilePane
     */
    private void setupContextMenu()
    {
        contextMenu = new ContextMenu();
        play = new MenuItem("Play Movie");
        editData = new MenuItem("Edit Metadata TODO");
        deleteMovie = new MenuItem("Delete Movie TODO");
        contextMenu.getItems().addAll(play, editData, deleteMovie);

    }
    
    public void contextMenuAction(ImageView imageView, Movie movie)
    {
                //<editor-fold defaultstate="collapsed" desc="setOnAction">
        play.setOnAction(new EventHandler<ActionEvent>()
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
    
    
    private void deleteMovie(ImageView imageView, Movie movie)
    {
        tilePane.getChildren().remove(imageView);
        model.removeMovie(movie.getId());
    }
    
    private void closePopOverIfRightClick()
    {
        if (popOver != null && popOver.isShowing())
        {
            popOver.hide();
        }
    }
}
