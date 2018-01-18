package movie.registraction.bll;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import movie.registraction.bll.exception.BLLException;
import movie.registraction.dal.DALManager;
import movie.registraction.dal.exception.DALException;

/**
 *
 * @author B
 */
public class Rating
{

    private DALManager dal;

    private boolean half;
    private int wholeNumber;
    private Label lblHalf;
    private Label lblRating;
    private List<Label> stars;
    private List<Label> emptyStars;

    /**
     * Constructor
     *
     * @param rating     The rating value
     * @param ratingType The type of the rating
     * @param gridPane   The GridPane in which the rating will exist
     * @param lblRating  The Label in which the rating value will be put
     *
     * @throws BLLException Throws an exception if it fails to initiate the DAL
     *                      Layer
     */
    public Rating(double rating, String ratingType, GridPane gridPane, Label lblRating) throws BLLException
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

        lblHalf = new Label("half");
        for (int i = 0; i < 10; i++)
        {
            Label star = new Label("*");
            saveRatingChangesHandler(star, gridPane, ratingType);
            setOnMouseEnteredHandler(star, gridPane);
            stars.add(star);

            Label emptyStar = new Label("0");
            saveRatingChangesHandler(emptyStar, gridPane, ratingType);
            setOnMouseEnteredHandler(emptyStar, gridPane);
            emptyStars.add(emptyStar);

        }

        setOnMouseExitedHandler(gridPane);
        initRating(ratingType, rating);
        setRatingStars(gridPane);
    }

    /**
     * Depending on the type of rating, set the whole number. If its a imdb
     * rating, it is possible to have half a star.
     *
     * @param ratingType The type of rating
     * @param rating     The value of the rating
     */
    private void initRating(String ratingType, double rating)
    {
        if (ratingType.equals("imdb"))
        {
            initIMDb(rating);
        }
        else
        {
            wholeNumber = (int) rating;
        }
    }

    /**
     * TODO
     *
     * @param rating The rating value as a Double
     */
    private void initIMDb(double rating)
    {
        if (rating - Math.floor(rating) < 0.75)
        {
            if (rating - Math.floor(rating) > 0.25)
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

    /**
     * Sets the gridpanes nodes as stars according to the rating.
     *
     * @param gridPane The GridPane where the rating will be displayed
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
            else if (i == wholeNumber + 1 && half == true)
            {
                gridPane.setColumnIndex(lblHalf, i - 1);
                gridPane.getChildren().add(lblHalf);
                setOnMouseEnteredHandler(lblHalf, gridPane);
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
     * @param gridPane  The GridPane being moused over
     * @param starIndex The rating index being moused over
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
    private void setOnMouseEnteredHandler(Label label, GridPane gridPane)
    {
        label.setOnMouseMoved(new EventHandler<MouseEvent>()
        {
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
    private void saveRatingChangesHandler(Label label, GridPane gridPane, String ratingType)
    {
        label.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                if (ratingType.equals("imdb"))
                {
                    try
                    {
                        Desktop.getDesktop().browse(new URI("https://www.imdb.com/registration/signin?u=http%3A%2F%2Fwww.imdb.com%2Ftitle%2Ftt0368226%2F"));
                    }
                    catch (URISyntaxException ex)
                    {

                    }
                    catch (IOException ex)
                    {

                    }
                }
                else
                {
                    Node node = (Node) e.getSource();
                    //Test movie id
                    int movieId = 1;
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
            }
        });
    }

}
