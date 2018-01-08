package movie.registraction.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import java.net.*;
import java.io.*;
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
public class MainWindowController implements Initializable {

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

    private MainWindowModel model;

    /**
     * Constructor for all intrents and purposes
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Access the Model
        model = new MainWindowModel();

        // Set default values
        acdPanes.setExpandedPane(acdGenre);

        lstGenre.setItems(model.getGenreList());
        lstYear.setItems(model.getYearList());
        lstOther.setItems(model.getOtherList());

    }

    @FXML
    private void titleSearch(ActionEvent event) throws Exception {
        model.fxmlTitleSearch(txtTitleSearch.getText());

        //Grabs the text from the search field and forces white spaces to become '+' to allow the API to recognize the spaces.
        String searchInput = txtTitleSearch.getText();
        searchInput = searchInput.replaceAll(" ", "+");

        //uses the API url + our fixed search index to display us all the metadata of the movie searched for - if possible.
        URL searchLink = new URL("http://www.omdbapi.com/?apikey=872a80a7&t=" + searchInput);
        //Opens up a connection for us to READ from a buffered reader in an inputstream from our API link.
        URLConnection yc = searchLink.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
        }
        in.close();
    }

    @FXML
    private void searchFilters(ActionEvent event) {
        model.fxmlFilterSearch();
    }

    @FXML
    private void clearFilters(ActionEvent event) {
        model.fxmlCleatFilters();
    }
}
