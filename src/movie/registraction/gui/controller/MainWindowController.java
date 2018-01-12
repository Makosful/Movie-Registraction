package movie.registraction.gui.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import movie.registraction.be.Movie;
import movie.registraction.dal.DALException;
import movie.registraction.gui.model.MainWindowModel;
import org.apache.commons.io.FilenameUtils;

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

    /**
     * Constructor for all intrents and purposes
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        bindTileToScroll();

        try
        {
            // Access the Model
            model = new MainWindowModel();
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

    }

    /**
     * Sets up the combo bozes
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
        //TODO
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
        imageClick(tilePane, model.getContextMenu());
    }

    /**
     * Binds the TilePane to the ScrollPane, height n width.
     */
    private void bindTileToScroll()
    {
        tilePane.prefWidthProperty().bind(scrlFilterSearch.widthProperty());
        tilePane.prefHeightProperty().bind(scrlFilterSearch.heightProperty());
    }

    /**
     * Code so you can click or right click on an image and soemthing happens.
     * Mouse event.
     */
    private void imageClick(TilePane tilePane, ContextMenu contextMenu)
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
                        model.getMovieData(imageView);
                        model.closeMenuOrClick(contextMenu);
                    }

                    if (mouseButton == MouseButton.SECONDARY) 
                    {
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
}
