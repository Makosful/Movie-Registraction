package movie.registraction.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import movie.registraction.be.Movie;
import movie.registraction.gui.model.MainWindowModel;

/**
 *
 * @author B
 */
public class EditMovieCategoryController implements Initializable
{

    private MainWindowModel m;

    @FXML
    private ListView<String> listViewAll;
    @FXML
    private ListView<String> listViewChosen;
    @FXML
    private Button btnSave;

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        m = new MainWindowModel();
    }

    /**
     * Calls to save the changes of chosen movies
     * Then it closes the window
     * @param event 
     */
    @FXML
    private void btnSave(ActionEvent event)
    {
        m.saveMovieCategories();
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    /**
     * When pressing the remove button the listViewChosen lists selected item is removed from 
     * the list.
     * @param event 
     */
    @FXML
    private void btnRemoveCat(ActionEvent event)
    {
        m.removeChosenMovieCategory(listViewChosen.getSelectionModel().getSelectedItem());
    }

    /**
     * Makes a call to get alle the categories from the specific movie and adds them 
     * to the listView, the same with all categories. A listener is added to this 
     * liswView of all categories, so when a new category is selected the 
     * addChosenMovieCategory method is called to add the selected 
     * category to the other list.
     *
     * @param selectedMovie Movie object
     */
    public void changeMovieCategories(Movie selectedMovie)
    {        
        listViewChosen.setItems(m.loadChosenMovieCategories(selectedMovie));

        listViewAll.setItems(m.getAllCategories());
        
        listViewAll.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends String> observable,
                              String oldValue, String newValue) ->
                {
                    m.addChosenMovieCategory(newValue);
                });
    }
}
