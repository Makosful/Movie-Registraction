package movie.registraction.gui.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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
    //</editor-fold>

    private MainWindowModel model;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXListView<?> lstGenre1;
    @FXML
    private JFXListView<?> lstGenre2;

    /**
     * Constructor for all intrents and purposes
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // Access the Model
        model = new MainWindowModel();

        // Set default values
        acdPanes.setExpandedPane(acdGenre);

        lstGenre.setItems(model.getGenreList());
        lstYear.setItems(model.getYearList());
        lstOther.setItems(model.getOtherList());

        //binding anchorpane to scrollpane
        //anchorForScroll.maxWidthProperty().bind(scrlFilterSearch.widthProperty().subtract(10).subtract(10));
        //anchorForScroll.minWidthProperty().bind(scrlFilterSearch.widthProperty().subtract(10).subtract(10));
    }

    @FXML
    private void titleSearch(ActionEvent event)
    {
        model.fxmlTitleSearch(txtTitleSearch.getText());
    }

    @FXML
    private void clearFilters(ActionEvent event)
    {
        model.fxmlClearFilters();
    }

    @FXML
    private void btnChangeCategories(ActionEvent event)
    {
        try
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
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void uploadFiles(ActionEvent event)
    {
        model.fxmlUploadFiles();
    }
}
