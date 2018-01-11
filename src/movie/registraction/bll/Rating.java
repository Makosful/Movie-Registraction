/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.bll;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import movie.registraction.be.Movie;

/**
 *
 * @author B
 */
public class Rating {
    
    private boolean half;
    private int wholeNumber;
 

    public void initRating(double rating, String ratingType, GridPane gridPane)
    {
        if(ratingType.equals("imdb")){
            if(rating-Math.floor(rating) < 0.75){


                if(rating-Math.floor(rating) > 0.25)
                {
                    half = true;
                }

                wholeNumber = (int) Math.floor(rating); 

            }
            else
            {
                wholeNumber = (int) Math.ceil(rating);
            }
        }
        else
        {
            wholeNumber = (int) rating;
        }
        
        setRatingStars(gridPane);
    }
    
    
    private void setRatingStars(GridPane gridPane)
    {
    
        for(int i = 1; i < 11; i++)
        {
            if(i <= wholeNumber)
            {

                Label label = new Label("*"+i);
                gridPane.setColumnIndex(label, i-1);
                gridPane.setRowIndex(label, 0);
                gridPane.getChildren().add(label);
                
                setOnMouseEnteredHandler(label, gridPane);
                
            }
            else if(i == wholeNumber+1 && half == true)
            {

                Label label = new Label("half"+i);
                gridPane.setColumnIndex(label, i-1);
                gridPane.setRowIndex(label, 0);
                gridPane.getChildren().add(label);
                
                setOnMouseEnteredHandler(label, gridPane);
            }
            else
            {
                
                Label label = new Label("-"+i);
                gridPane.setColumnIndex(label, i-1);
                gridPane.setRowIndex(label, 0);
                gridPane.getChildren().add(label);
                
               setOnMouseEnteredHandler(label, gridPane);
            }
            
        }
        
    }
    
    
    public void onMouseOver(GridPane gridPane, int starIndex){
        gridPane.getChildren().clear();
        for(int i = 0; i < 10; i++){
           if(i <= starIndex){  
              
         
                Label label = new Label("++");
                gridPane.setColumnIndex(label, i);
                gridPane.getChildren().add(label);
                
                setOnMouseEnteredHandler(label, gridPane);
                setOnMouseExitedHandler(gridPane);
               
           }else{

                Label label = new Label("0");
                gridPane.setColumnIndex(label, i);
                gridPane.getChildren().add(label);
                
                setOnMouseEnteredHandler(label, gridPane);
                setOnMouseExitedHandler(gridPane);
                
           }
       }
    }
    
    
    private void setOnMouseEnteredHandler(Label label, GridPane gridPane)
    {
        label.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
              Node node = (Node)e.getSource();
              onMouseOver(gridPane, gridPane.getColumnIndex(node));
            }
        });
    
    }
    
    
    private void setOnMouseExitedHandler(GridPane gridPane)
    {
        gridPane.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
              gridPane.getChildren().clear();
              setRatingStars(gridPane);
            }
        });
    }
         
}
