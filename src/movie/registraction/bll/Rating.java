/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.bll;

import java.io.IOException;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import movie.registraction.dal.MovieDAO;

/**
 *
 * @author B
 */
public class Rating {
    
    private boolean half;
    private int wholeNumber;
    private MovieDAO mDAO;
    
    public Rating(double rating, String ratingType, GridPane gridPane) throws IOException
    {
        try {
            mDAO = new MovieDAO();
        } catch (IOException ex) {
            throw new IOException();
        }
        
        initRating(ratingType, rating);
        setRatingStars(gridPane);
    }
    
    /**
     * 
     * @param ratingType
     * @param rating 
     */
    private void initRating(String ratingType, double rating)
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
    }
    
    
    /**
     * 
     * @param gridPane 
     */
    private void setRatingStars(GridPane gridPane)
    {
    
        for(int i = 1; i < 11; i++)
        {
            if(i <= wholeNumber)
            {

                Label label = new Label("*"+i);
                gridPane.setColumnIndex(label, i-1);
                gridPane.getChildren().add(label);
                
                setOnMouseEnteredHandler(label, gridPane);
                
            }
            else if(i == wholeNumber+1 && half == true)
            {

                Label label = new Label("half"+i);
                gridPane.setColumnIndex(label, i-1);
                gridPane.getChildren().add(label);
                
                setOnMouseEnteredHandler(label, gridPane);
            }
            else
            {
                
                Label label = new Label("-"+i);
                gridPane.setColumnIndex(label, i-1);
                gridPane.getChildren().add(label);
                
               setOnMouseEnteredHandler(label, gridPane);
            }
            
        }
        
    }
    
    /**
     * 
     * @param gridPane
     * @param starIndex 
     */
    public void onMouseOver(GridPane gridPane, int starIndex){
        gridPane.getChildren().clear();
        for(int i = 0; i < 10; i++){
           if(i <= starIndex){  
              
         
                Label label = new Label("++");
                gridPane.setColumnIndex(label, i);
                gridPane.getChildren().add(label);
                
                saveRatingChangesHandler(label, gridPane);
                setOnMouseEnteredHandler(label, gridPane);
                setOnMouseExitedHandler(gridPane);
                
               
           }else{

                Label label = new Label("0");
                gridPane.setColumnIndex(label, i);
                gridPane.getChildren().add(label);
                
                saveRatingChangesHandler(label, gridPane);
                setOnMouseEnteredHandler(label, gridPane);
                setOnMouseExitedHandler(gridPane);
                
                
           }
       }
    }
    
    /**
     * 
     * @param label
     * @param gridPane 
     */
    private void setOnMouseEnteredHandler(Label label, GridPane gridPane)
    {
        label.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                Node node = (Node)e.getSource();
                onMouseOver(gridPane, gridPane.getColumnIndex(node));
            }
        }); 
    }
    
    /**
     * 
     * @param gridPane 
     */
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
    
    /**
     * 
     * @param label
     * @param gridPane 
     */
    private void saveRatingChangesHandler(Label label, GridPane gridPane)
    {
        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                Node node = (Node)e.getSource();
                System.out.println(gridPane.getColumnIndex(node));
            }
        });
    }
         
}
