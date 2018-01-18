package movie.registraction.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import movie.registraction.gui.model.MainWindowModel;

/**
 * FXML Controller class
 *
 * @author B
 */
public class RatingController implements Initializable
{

    @FXML
    private GridPane gridPaneRating;

    @FXML
    private Label lblRating;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        MainWindowModel m = new MainWindowModel();
        m.setUpRating(6.6, "imdb", gridPaneRating, lblRating);
    }
}
