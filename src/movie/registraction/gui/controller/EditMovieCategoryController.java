/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import movie.registraction.be.Movie;
import movie.registraction.dal.DALException;
import movie.registraction.dal.MovieDAO;
import movie.registraction.gui.model.MainWindowModel;

/**
 *
 * @author B
 */
public class EditMovieCategoryController implements Initializable {
    
    
    private MainWindowModel m;
    private Movie movie;
    
    @FXML
    private ListView<String> listViewAll;
    @FXML
    private ListView<String> listViewChosen;
    @FXML
    private Button btnSave;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    
        try {
            m = new MainWindowModel();
        } catch (DALException ex) {
            System.out.println(ex.getMessage());
        }
  
        listViewAll.setItems(m.getAllCategories());
        try {
            MovieDAO mDAO = new MovieDAO();
            
            ObservableList<Movie> movie = mDAO.getAllMovies();
            listViewChosen.setItems(m.loadChosenMovieCategories(movie.get(0)));
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (DALException ex) {
            System.out.println(ex.getMessage());
        }
        
        
        listViewAll.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
               
                m.addChosenMovieCategory(newValue);

            }
            
        });
        
        
    }

    @FXML
    private void btnSave(ActionEvent event) {
            m.saveMovieCategories();
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
    }

    @FXML
    private void btnRemoveCat(ActionEvent event) {
        m.removeChosenMovieCategory(listViewChosen.getSelectionModel().getSelectedItem()); 
    }
    

    
}
