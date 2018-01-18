/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.bll;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import movie.registraction.bll.exception.BLLException;
import movie.registraction.dal.DALManager;
import movie.registraction.dal.exception.DALException;

/**
 *
 * @author B
 */

public class Rating {
    
    private int wholeNumber;
    private DALManager dal;
    private Label lblRating;
    List<ImageView> stars;
    List<ImageView> emptyStars;
    
    
    public Rating(int movieId, double rating, GridPane gridPane, Label lblRating) throws BLLException, FileNotFoundException 

    {

        try
        {
            dal = new DALManager();
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }

        stars = new ArrayList();
        emptyStars = new ArrayList();
        this.lblRating = lblRating;
        wholeNumber = (int) rating;
        
        for(int i = 0; i < 10; i++)
        {
            ImageView star = new ImageView("/movie/registraction/rsx/star.png");
            
            saveRatingChangesHandler(star, gridPane, movieId);
            setOnMouseEnteredHandler(star, gridPane);
            stars.add(star);

            
            
            ImageView emptyStar = new ImageView("/movie/registraction/rsx/emptyStar.png");
            
            saveRatingChangesHandler(emptyStar, gridPane, movieId);
            setOnMouseEnteredHandler(emptyStar, gridPane);
            emptyStars.add(emptyStar);

        }

        setOnMouseExitedHandler(gridPane);
        setRatingStars(gridPane);
    }


    


    /**
     * Sets the gridpanes nodes as stars according to the rating.
     *
     * @param gridPane
     */
    private void setRatingStars(GridPane gridPane)
    {

        for (int i = 1; i < 11; i++)
        {
            if (i <= wholeNumber)
            {
                gridPane.setColumnIndex(stars.get(i - 1), i - 1);
                gridPane.getChildren().add(stars.get(i - 1));
            }
            else
            {
                gridPane.setColumnIndex(emptyStars.get(i - 1), i - 1);
                gridPane.getChildren().add(emptyStars.get(i - 1));
            }

        }

        lblRating.setText(Integer.toString(wholeNumber));

    }

    /**
     * Add method to when the user moves the mouse over the gridpane. Depending
     * on which star the users mouse is over the gridpanes nodes are changed
     * correspondingly.
     *
     * @param gridPane
     * @param starIndex
     */
    private void onMouseOver(GridPane gridPane, int starIndex)
    {
        lblRating.setText(Integer.toString(starIndex + 1));
        gridPane.getChildren().clear();
        for (int i = 0; i < 10; i++)
        {
            if (i <= starIndex)
            {
                gridPane.setColumnIndex(stars.get(i), i);
                gridPane.getChildren().add(stars.get(i));
            }
            else
            {
                gridPane.setColumnIndex(emptyStars.get(i), i);
                gridPane.getChildren().add(emptyStars.get(i));
            }
        }
    }

    /**
     * Add evenhandler to when the user moves the mouse over the gridpane to
     * set a new score
     *
     * @param label
     * @param gridPane
     */
    private void setOnMouseEnteredHandler(ImageView image, GridPane gridPane)
    {
        image.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e)
            {
                Node node = (Node) e.getSource();
                onMouseOver(gridPane, gridPane.getColumnIndex(node));
            }
        });
    }

    /**
     * Add evenhandler to when the user moves the mouse away from the gridpane
     * or the rating has
     * been changed, clear the nodes in gridpane and set the score
     *
     * @param gridPane
     * @param rating
     */
    private void setOnMouseExitedHandler(GridPane gridPane)
    {
        gridPane.setOnMouseExited(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                gridPane.getChildren().clear();
                setRatingStars(gridPane);
            }
        });
    }

    /**
     * If the rating is for imdb the method links to imdb rating. Else it sends
     * the new rating to the db and resets the rating with the new score.
     *
     * @param label
     * @param gridPane
     * @param ratingType
     */
    private void saveRatingChangesHandler(ImageView image, GridPane gridPane, int movieId) throws BLLException 
    {
        image.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                Node node = (Node) e.getSource();

                try
                {
                    dal.setPersonalRating(movieId, gridPane.getColumnIndex(node) + 1);
                }
                catch (DALException ex)
                {
                }
                wholeNumber = gridPane.getColumnIndex(node) + 1;
                setOnMouseExitedHandler(gridPane);
            }
        });
    }



    
         

}
