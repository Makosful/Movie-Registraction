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

    VBox vBox;
    Hyperlink imdbURL;
    PopOver popOver;

    ContextMenu contextMenu;
    MenuItem test1;
    MenuItem test2;
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
        flpGenre.getChildren().setAll(model.getGenreNodes());
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
        model.fxmlTitleSearch(txtTitleSearch.getText());
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

    private void vBoxAndLabelSetup(Movie movie, MouseEvent event)
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

        //<editor-fold defaultstate="collapsed" desc="Label And One VBox">
        lblMovieTitle = new Label();
        lblGenre = new Label();
        lblPersonalRating = new Label();
        lblMovieLength = new Label();
        lblLastView = new Label();
        lblImdbRating = new Label();
        lblYear = new Label();
        imdbURL = new Hyperlink();
        vBox = new VBox();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="labelSetText">
        lblMovieTitle.setText("Title: " + movie.getMovieTitle());
        lblMovieTitle.setStyle("-fx-text-fill: black");
        lblGenre.setText("Genres: " + genreCategories);
        lblGenre.setStyle("-fx-text-fill: black");
        lblYear.setText("Release year: " + movie.getYear());
        lblYear.setStyle("-fx-text-fill: black");
        lblMovieLength.setText("Length: " + genreCategories + "minutes");
        lblMovieLength.setStyle("-fx-text-fill: black");
        lblImdbRating.setText("IMDB rating: " + movie.getImdbRating() + "/10");
        lblImdbRating.setStyle("-fx-text-fill: black");
        lblPersonalRating.setText("Personal rating: " + movie.getPersonalRating());
        lblPersonalRating.setStyle("-fx-text-fill: black");
        lblLastView.setText("Last viewed on: " + movie.getLastView());
        lblLastView.setStyle("-fx-text-fill: black");
        
        //HYPERLINK
        imdbURL.setText("http://www.imdb.com/title/" + movie.getImdbLink());
        //imdbURL.setStyle("-fx-text-fill: black");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="addToVbox">
        vBox.getChildren().add(lblMovieTitle);
        vBox.getChildren().add(lblGenre);
        vBox.getChildren().add(lblYear);
        vBox.getChildren().add(lblMovieLength);
        vBox.getChildren().add(lblImdbRating);
        vBox.getChildren().add(lblPersonalRating);
        vBox.getChildren().add(lblLastView);
        vBox.getChildren().add(imdbURL);

        //</editor-fold>
        
//        GridPane popGrid = new GridPane();
//
//        popGrid.setPadding(new Insets(20));
//        popGrid.setHgap(10);
//        popGrid.setVgap(5); //irrelevant
//
//        popGrid.add(labelMovieTitle, 0, 0);
//        popGrid.add(labelGenre, 0, 1);
//        popGrid.add(labelYear, 0, 2);
//        popGrid.add(labelImdbRating, 0, 3);
//        popGrid.add(labelPersonalRating, 0, 4);
//        popGrid.add(labelLastView, 0, 5);
//        popGrid.add(imdbURL, 0, 6);

        if (popOver == null)
        {
            popOver = new PopOver(vBox); //WOLOLO (change to vBox)
            popOver.show(tilePane, event.getScreenX(), event.getScreenY());
        }
        else if(popOver.isShowing())
        {
            popOver.hide();
        }
        else if(!popOver.isShowing())
        {
            popOver = new PopOver(vBox); //WOLOLO (change to vBox)
            popOver.show(tilePane, event.getScreenX(), event.getScreenY());
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
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    MouseButton mouseButton = event.getButton();
                    if (mouseButton == MouseButton.PRIMARY)
                    {
                        model.closeMenuOrClick(contextMenu);
                        if(popOver.isShowing())
                        {
                            popOver.hide();
                        }
                        Movie movie = model.getMovieInfo(imageView);
                        vBoxAndLabelSetup(movie, event);
                    }

                    if (mouseButton == MouseButton.SECONDARY)
                    {
                        Movie movie = model.getMovieInfo(imageView);
                        contextMenuAction(imageView, movie);
                        model.contextMenuOpenOrNot(contextMenu);
                        contextMenu.show(tilePane, event.getScreenX(), event.getScreenY());
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
        test1 = new MenuItem("");
        test2 = new MenuItem("2");
        deleteMovie = new MenuItem("Delete Movie");
        contextMenu.getItems().addAll(test1, test2, deleteMovie);

    }
    
    public void contextMenuAction(ImageView imageView, Movie movie)
    {
                //<editor-fold defaultstate="collapsed" desc="setOnAction">
        test1.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                contextMenu.hide();
            }
        });

        test2.setOnAction(new EventHandler<ActionEvent>()
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
}
