package movie.registraction.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import java.net.*;
import java.io.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
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
    private JFXListView<JFXCheckBox> lstGenre;
    @FXML
    private JFXListView<JFXCheckBox> lstYear;
    @FXML
    private JFXListView<JFXCheckBox> lstOther;

    private MainWindowModel model;
    @FXML
    private TitledPane acdOther1;
    @FXML
    private JFXListView<?> lstOther1;
    @FXML
    private AnchorPane anchorForScroll;

    /**
     * Constructor for all intrents and purposes
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Access the Model
            model = new MainWindowModel();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

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
    private void titleSearch(ActionEvent event) throws Exception {
        model.fxmlTitleSearch(txtTitleSearch.getText());

        //Grabs the text from the search field and forces white spaces to become '+' to allow the API to recognize the spaces.
        String searchInput = txtTitleSearch.getText();
        searchInput = searchInput.replaceAll(" ", "+");

        //uses the API url + our fixed search index to display us all the metadata of the movie searched for - if possible.
        URL searchLink = new URL("http://www.omdbapi.com/?apikey=872a80a7&t=" + searchInput);
        //Opens up a connection for us to READ from a buffered reader in an inputstream from our API link.
        URLConnection con = searchLink.openConnection();
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String inputLine;
        while ((inputLine = buffRead.readLine()) != null) {
            System.out.println(inputLine);
        }
        buffRead.close();
    }

    private void searchFilters(ActionEvent event) {
        model.fxmlFilterSearch();
    }

    @FXML
    private void clearFilters(ActionEvent event) {
        model.fxmlCleatFilters();
    }
}
