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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import movie.registraction.be.Movie;
import movie.registraction.dal.DALException;
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
            mDAO = new MovieDAO();
            removeCategory = new ArrayList();
            removeMovieCategory = new ArrayList();
        }
        catch (IOException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Gets all the previously chosen categories from the movie object and adds
     * them an observablelist
     * @param movie
     * @return ObservableList
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
     * movie object category,
     * it is removed from the chosenMovieCategory list since it should not be
     * added again.
     * The removeMovieCategory list and chosenMovieCategory list is then looped
     * through,
     * adding/removing the categories in the database to this specific movie.
     * @throws movie.registraction.dal.DALException
     */
    public void saveMovieCategories() throws DALException
    {
       
        Iterator<String> in = movie.getCategories().iterator();
        while (in.hasNext())
        {
            String cat = in.next();

            if (!chosenMovieCategories.contains(cat)){
                removeMovieCategory.add(cat);
            }
            else{
                chosenMovieCategories.remove(cat);
            }
        }

        for (String cat : removeMovieCategory){
            try {
                mDAO.removeMovieCategory(5, cat);
            } catch (DALException ex) {
                throw new DALException();
            }
        }
        
        for (String cat : chosenMovieCategories){
            try {
                mDAO.addMovieCategory(5, cat);
            } catch (DALException ex) {
                throw new DALException();
            }
        }
        removeMovieCategory.clear();
        chosenMovieCategories.clear();
    }

    /**
     * Gets all categories from the db
     * @return ObservableList of strings
     * @throws movie.registraction.dal.DALException
     */
    public ObservableList<String> allCategories() throws DALException 
    {
        try {
            categories.addAll(mDAO.getAllCategories());
        } catch (DALException ex) {
                throw new DALException();
        }
        return categories;
    }

    /**
     * Adds a category to the observableList "categories"
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

    /**
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
     */
    public void saveCategories() throws DALException
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

        for (String cat : removeCategory){
            
            try {
                 mDAO.removeCategory(cat);
            } catch (DALException ex) {
                throw new DALException();
            }
        }
        for (String cat : categories){
            try {
                mDAO.addCategory(cat);
            } catch (DALException ex) {
                throw new DALException();
            }
        }
        removeCategory.clear();
        categories.clear();
    }

}
