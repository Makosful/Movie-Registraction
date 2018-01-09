/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.bll;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import movie.registraction.be.Movie;
import movie.registraction.dal.MovieDAO;

/**
 *
 * @author B
 */
public class changeCategories
{

    private Movie movie;
    private MovieDAO mDAO;
    private List<String> removeCategory;
    private List<String> removeMovieCategory;
    ObservableList<String> categories = FXCollections.observableArrayList();
    ObservableList<String> chosenMovieCategories = FXCollections.observableArrayList();

    public changeCategories() throws BLLException
    {
        try
        {
            movie = new Movie();
            mDAO = new MovieDAO();
            removeCategory = new ArrayList();
        }
        catch (IOException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Gets all the previously chosen categories from the movie object and adds
     * them an observablelist
     *
     * @return ObservableList
     */
    public ObservableList<String> loadChosenMovieCategories()
    {
        chosenMovieCategories.addAll(movie.getCategories());
        return chosenMovieCategories;
    }

    /**
     * Add a new category to the chosenMovieCategories observablelist
     *
     * @param category
     */
    public void addChosenMovieCategory(String category)
    {
        if (!chosenMovieCategories.contains(category))
            chosenMovieCategories.add(category);
    }

    /**
     * Remove a specific category from the chosenMovieCategories observablelist
     *
     * @param category
     */
    public void removeChosenMovieCategory(String category)
    {
        Iterator<String> i = chosenMovieCategories.iterator();
        while (i.hasNext())
        {
            String cat = i.next();

            if (cat.equals(category))
                i.remove();
        }
    }

    /**
     * Prepares the newly added or removed categories to be inserted or deleted
     * in the database.
     * The method compares the current list of categories to the new list of
     * chosen categories.
     * If the new chosenMovieCategories list does not contain the specific
     * current movie category,
     * it is added to the removeMovieCategory list.
     * If the new chosenMovieCategories list does contain the specific current
     * movie category,
     * it is removed from the removeMovieCategory list since it should not be
     * added again.
     * The removeMovieCategory list and removeMovieCategory list is then looped
     * through,
     * adding/removing the categories in the database to this specific movie.
     */
    public void saveMovieCategories() throws SQLException
    {
        Iterator<String> in = movie.getCategories().iterator();
        while (in.hasNext())
        {
            String cat = in.next();

            if (!chosenMovieCategories.contains(cat))
                removeMovieCategory.add(cat);
            else
                chosenMovieCategories.remove(cat);

        }

        for (String cat : removeMovieCategory)
            mDAO.removeMovieCategory(1, cat);

        for (String cat : chosenMovieCategories)
            mDAO.addMovieCategory(1, cat);

        removeMovieCategory.clear();
        chosenMovieCategories.clear();
    }

    /**
     * Gets all categories from the db
     *
     * @return
     *
     * @throws SQLException
     */
    public ObservableList<String> allCategories() throws SQLException
    {
        categories.addAll(mDAO.getAllCategories());
        return categories;
    }

    /**
     * Adds a category to the observableList "categories"
     *
     * @param category
     */
    public void addChosenCategory(String category)
    {
        if (!categories.contains(category))
            categories.add(category);
    }

    /**
     * Removes a category from the observableList "categories" by iterating
     * through
     * the list and finds the specific category and removes it
     *
     * @param category
     */
    public void removeChosenCategory(String category)
    {
        Iterator<String> i = categories.iterator();
        while (i.hasNext())
        {

            String cat = i.next();

            if (cat.equals(category))
                i.remove();
        }
    }

    public void saveCategories() throws SQLException
    {
        Iterator<String> in = mDAO.getAllCategories().iterator();
        while (in.hasNext())
        {
            String cat = in.next();

            if (!categories.contains(cat))
                removeCategory.add(cat);
            else
                categories.remove(cat);

        }

        for (String cat : removeCategory)
            mDAO.removeCategory(cat);

        for (String cat : categories)
            mDAO.addCategory(cat);

        removeCategory.clear();
        categories.clear();
    }

}
