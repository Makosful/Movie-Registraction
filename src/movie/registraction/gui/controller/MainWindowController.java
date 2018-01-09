package movie.registraction.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
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
    private JFXButton btnTitleSearch;
    @FXML
    private JFXButton btnFilterSearch;
    @FXML
    private JFXButton btnClearFilters;
    @FXML
    private TitledPane acdGenre;
    @FXML
    private TitledPane acdYear;
    @FXML
    private TitledPane acdOther;
    @FXML
    private Accordion acdPanes;
    @FXML
    private GridPane gridResults;
    @FXML
    private JFXListView<JFXCheckBox> lstGenre;
    @FXML
    private JFXListView<JFXCheckBox> lstYear;
    @FXML
    private JFXListView<JFXCheckBox> lstOther;
    //</editor-fold>

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
        // Access the Model
        model = new MainWindowModel();

        // Set default values
        acdPanes.setExpandedPane(acdGenre);

        lstGenre.setItems(model.getGenreList());
        lstYear.setItems(model.getYearList());
        lstOther.setItems(model.getOtherList());

    }

    @FXML
    private void titleSearch(ActionEvent event)
    {
        model.fxmlTitleSearch(txtTitleSearch.getText());
    }

    @FXML
    private void searchFilters(ActionEvent event)
    {
        model.fxmlFilterSearch();
    }

    @FXML
    private void clearFilters(ActionEvent event)
    {
        model.fxmlCleatFilters();
    }
}
