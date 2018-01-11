/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import movie.registraction.bll.Rating;

/**
 * FXML Controller class
 *
 * @author B
 */
public class RatingController implements Initializable {

    @FXML
    private GridPane gridPaneRating;
    private Rating r;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        r = new Rating();
        r.initRating(7.5, "imdb", gridPaneRating);
        
        
        
    }    


    
    

       
    

}
