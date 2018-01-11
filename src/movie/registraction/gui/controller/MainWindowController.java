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
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import movie.registraction.gui.model.MainWindowModel;

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
    @FXML
    private JFXListView<JFXCheckBox> lstGenre;
    @FXML
    private JFXListView<JFXCheckBox> lstYear;
    @FXML
    private JFXListView<JFXCheckBox> lstOther;
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

        // Access the Model
        model = new MainWindowModel();

        // Set default values
        acdPanes.setExpandedPane(acdGenre);
        lstGenre.setItems(model.getGenreList());
        lstYear.setItems(model.getYearList());
        lstOther.setItems(model.getOtherList());

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
    private void uploadFiles(ActionEvent event)
    {
        setPictures(); // Midlertidigt.
    }

    /**
     * Add comment
     */
    private void setPictures()
    {
        // Creates a new FileChooser object
        FileChooser fc = new FileChooser();

        // Defines what files it will look for
        FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter("MP4 Files", "*.mp4", ".mpeg4");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");

        // Adds the filters
        fc.getExtensionFilters().addAll(videoFilter, imageFilter);

        // Opens the FileChooser and saves the results in a list
        List<File> chosenFiles = fc.showOpenMultipleDialog(null);

        // Checks if any files where chosen
        if (chosenFiles != null)
        {
            // If valid files were chosen, add them as movies
            //List<File> addedFiles;
            model.setPictures(tilePane, chosenFiles);
            imageClick(tilePane, model.getContextMenu());
        }
        else
        {
            // Otherwise return
            System.out.println("One or more invalid file(s) / None selected");
            return;
        }
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
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    MouseButton mouseButton = event.getButton();
                    if (mouseButton == MouseButton.PRIMARY)
                        model.closeMenuOrClick(contextMenu);

                    if (mouseButton == MouseButton.SECONDARY)
                    {
                        model.contextMenuOpenOrNot(contextMenu);
                        contextMenu.show(tilePane, event.getScreenX(), event.getScreenY());
                    }
                }
            });
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
