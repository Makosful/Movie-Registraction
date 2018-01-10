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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    
        m = new MainWindowModel();
  
        listViewAll.setItems(m.getAllCategories());
        try {
            MovieDAO mDAO = new MovieDAO();
            
            ObservableList<Movie> movie = mDAO.getAllMovies();
            listViewChosen.setItems(m.loadChosenMovieCategories(movie.get(0)));
            
        } catch (IOException ex) {
            Logger.getLogger(EditMovieCategoryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DALException ex) {
            Logger.getLogger(EditMovieCategoryController.class.getName()).log(Level.SEVERE, null, ex);
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
    }

    @FXML
    private void btnRemoveCat(ActionEvent event) {
        m.removeChosenMovieCategory(listViewChosen.getSelectionModel().getSelectedItem()); 
    }
    

    
}
