package movie.registraction.bll;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import movie.registraction.be.Movie;
import movie.registraction.bll.exception.BLLException;
import movie.registraction.dal.DALManager;
import movie.registraction.dal.exception.DALException;

/**
 *
 * @author B
 */
public class ChangeCategories
{

    private Movie movie;
    private DALManager dal;

    private final List<String> removeCategory;
    private final List<String> removeMovieCategory;
    private final ObservableList<String> categories;
    private final ObservableList<String> chosenMovieCategories;

    /**
     * Constructor
     *
     * @throws movie.registraction.bll.exception.BLLException
     */
    public ChangeCategories() throws BLLException 
    {
        try
        {
            dal = new DALManager();
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }

        this.removeCategory = new ArrayList();
        this.removeMovieCategory = new ArrayList();
        this.chosenMovieCategories = FXCollections.observableArrayList();
        this.categories = FXCollections.observableArrayList();
    }

    /**
     * Gets all the previously chosen categories from the movie object and adds
     * them an observablelist
     *
     * @param movie The Movie object which categories to load
     *
     * @return Returns an ObservableList of Strings containing all the
     *         categories
     */
    public ObservableList<String> loadChosenMovieCategories(Movie movie)
    {
        this.movie = movie;
        chosenMovieCategories.addAll(movie.getCategories());
        return chosenMovieCategories;
    }

    /**
     * Add a new category to the chosenMovieCategories observablelist
     *
     * @param category The new category to add
     */
    public void addChosenMovieCategory(String category)
    {
        if (!chosenMovieCategories.contains(category))
        {
            chosenMovieCategories.add(category);
        }
    }

    /**
     * Remove a specific category from the chosenMovieCategories observablelist
     *
     * @param category The category to remove
     */
    public void removeChosenMovieCategory(String category)
    {
        chosenMovieCategories.remove(category);
    }

    /**
     * Saves the categories
     *
     * Prepares the newly added or removed categories to be inserted or deleted
     * in the database.
     * The method compares the current list of categories to the new list of
     * chosen categories.
     * If the new chosenMovieCategories list does not contain the specific
     * current movie category,
     * it is added to the removeMovieCategory list.
     * If the new chosenMovieCategories list does contain the specific current
     * movie object category,
     * it is removed from the chosenMovieCategory list since it should not be
     * added again.
     * The removeMovieCategory list and chosenMovieCategory list is then looped
     * through,
     * adding/removing the categories in the database to this specific movie.
     *
     * @throws BLLException Throws an exception if it fails to access the
     *                      database
     */
    public void saveMovieCategories() throws BLLException
    {

        for (String cat : movie.getCategories())
        {

            if (!chosenMovieCategories.contains(cat))
            {
                removeMovieCategory.add(cat);
            }
            else
            {
                chosenMovieCategories.remove(cat);
            }
        }

        for (String cat : removeMovieCategory)
        {
            try
            {
                dal.removeMovieCategory(movie.getId(), cat);
            }
            catch (DALException ex)
            {
                throw new BLLException();
            }
        }

        for (String cat : chosenMovieCategories)
        {
            try
            {
                dal.addMovieCategory(movie.getId(), cat);
            }
            catch (DALException ex)
            {
                throw new BLLException();
            }
        }
        removeMovieCategory.clear();
        chosenMovieCategories.clear();
    }

    /**
     * Gets all categories from the database
     *
     * @return ObservableList of Strings containing all the categories
     *
     * @throws BLLException throws an exception if it fails to access the
     *                      database
     */
    public ObservableList<String> allCategories() throws BLLException
    {
        try
        {
            categories.addAll(dal.getAllCategories());
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
        return categories;
    }

    /**
     * Adds a category to the observableList "categories"
     *
     * @param category The category to add
     */
    public void addChosenCategory(String category)
    {
        if (!categories.contains(category))
        {
            categories.add(category);
        }
    }

    /**
     * Removes a category from the observableList "categories" by iterating
     * through
     * the list and finds the specific category and removes it
     *
     * @param category The category to remove
     */
    public void removeChosenCategory(String category)
    {
        categories.remove(category);
    }

    /**
     * Save the categories
     *
     * Prepares the newly added or removed categories to be inserted or deleted
     * in the database.
     * The method compares the current list of categories to the new list of
     * chosen categories.
     * If the new categories list does not contain the specific
     * current movie category,
     * it is added to the removeMovieCategory list.
     * If the new categories list does contain the specific current
     * movie object category,
     * it is then removed from the category list since it should not be
     * added again.
     * The removeMovieCategory list and category list is then looped
     * through,
     * adding/removing the categories in the database to this specific movie.
     *
     * @throws BLLException Throws an exception if it fails to access the
     *                      database
     */
    public void saveCategories() throws BLLException
    {
        try
        {
            for (String cat : dal.getAllCategories())
            {

                if (!categories.contains(cat))
                {
                    removeCategory.add(cat);
                }
                else
                {
                    categories.remove(cat);
                }

            }

            for (String cat : removeCategory)
            {

                dal.removeCategory(cat);
            }
            for (String cat : categories)
            {
                dal.addCategory(cat);
            }

            removeCategory.clear();
            categories.clear();

        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Gets all categories and adds them to an observableList to be returned and
     * used when initializing Edit Categories
     *
     * @return Returns an ObservableList of String of the categories
     *
     * @throws BLLException Throws an exception if it fails to access the
     *                      database
     */
    public ObservableList<String> loadCategories() throws BLLException
    {
        try
        {
            categories.addAll(dal.getAllCategories());
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
        return categories;
    }
}
