/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.bll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    List<Label> stars;
    List<Label> emptyStars;
    public Rating(double rating, String ratingType, GridPane gridPane) throws IOException
    {
        try {
            mDAO = new MovieDAO();
        } catch (IOException ex) {
            throw new IOException();
        }
        
        stars = new ArrayList();
        emptyStars = new ArrayList();
        
        
        for(int i = 0; i < 10; i++)
        {
            Label star = new Label("*");
            saveRatingChangesHandler(star, gridPane);
            setOnMouseEnteredHandler(star, gridPane);
            stars.add(star);
            
            
            Label emptyStar = new Label("0");
            saveRatingChangesHandler(emptyStar, gridPane);
            setOnMouseEnteredHandler(emptyStar, gridPane);
            emptyStars.add(emptyStar);
            
        }
        
        setOnMouseExitedHandler(gridPane);
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

                gridPane.setColumnIndex(stars.get(i-1), i-1);
                gridPane.getChildren().add(stars.get(i-1));
                
                
                
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
              
                gridPane.setColumnIndex(emptyStars.get(i-1), i-1);
                gridPane.getChildren().add(emptyStars.get(i-1));
                

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
              
                gridPane.setColumnIndex(stars.get(i), i);
                gridPane.getChildren().add(stars.get(i));
                

               
           }else{

         
                gridPane.setColumnIndex(emptyStars.get(i), i);
                gridPane.getChildren().add(emptyStars.get(i));
                
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
        System.out.println("HEJ");
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
