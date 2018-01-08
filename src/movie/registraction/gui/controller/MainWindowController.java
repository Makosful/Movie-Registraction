package movie.registraction.gui.controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Axl
 */
public class MainWindowController implements Initializable
{

    private Label label;
    @FXML
    private TabPane tabPanes;
    @FXML
    private Tab paneFilterSearch;
    @FXML
    private FlowPane flpGenre;
    @FXML
    private FlowPane flpYear;
    @FXML
    private FlowPane flpOther;
    @FXML
    private ScrollPane scrlFilterSearch;
    @FXML
    private GridPane gridFilterSearch;
    @FXML
    private Tab paneTitleSearch;
    @FXML
    private TextField txtTitleSearch;
    @FXML
    private JFXButton btnTitleSearch;
    @FXML
    private ScrollPane scrlTitleSearch;
    @FXML
    private GridPane gridTitleSearch;
    @FXML
    private JFXButton btnFilterSearch;
    @FXML
    private JFXButton btnClearFilters;

    private void handleButtonAction(ActionEvent event)
    {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }

    @FXML
    private void titleSearch(ActionEvent event)
    {
    }

    @FXML
    private void searchFilters(ActionEvent event)
    {
    }

    @FXML
    private void clearFilters(ActionEvent event)
    {
    }

}
