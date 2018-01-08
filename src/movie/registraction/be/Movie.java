/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.be;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import movie.registraction.dal.MovieDAO;


/**
 *
 * @author B
 */
public class Movie 
{
    private int id;
    private String movieName;
    private int year;
    private double rating;
    private List<String> removeMovieCategory;
    private MovieDAO mDAO;
    List<String> categories;

    ObservableList<String> chosenMovieCategories = FXCollections.observableArrayList();
    
    
    public Movie() throws IOException
    {
        mDAO = new MovieDAO();
        categories = new ArrayList();
        removeMovieCategory = new ArrayList();
    }

     public int getId() 
     {
        return id;
    }

    public void setId(int id) 
    {
        this.id = id;
    }
    
    public List<String> getCategories() 
    {
        return categories;
    }

    public void setCategories(List<String> categories) 
    {
        this.categories = categories;
    }
    
    public void setMovieName(String movieName)
    {
        this.movieName = movieName;
    }
    
    public void setMovieYear(int year)
    {
        this.year = year;
    }
    
    public void setRating(double rating)
    {
        this.rating = rating;
    }
    
    public String getMovieName()
    {
        return movieName;
    }

    public int getYear() 
    {
        return year;
    }

    public double getRating() 
    {
        return rating;
    }
    
        /**
     * Gets all the previously chosen categories from the movie object and adds them an observablelist
     * @return ObservableList  
     */
    public ObservableList<String> loadChosenMovieCategories() {
       chosenMovieCategories.addAll(getCategories());
       return chosenMovieCategories;
    }

    /**
     * Add a new category to the chosenMovieCategories observablelist
     * @param category 
     */
    public void addChosenMovieCategory(String category) {
        if(!chosenMovieCategories.contains(category)){
            chosenMovieCategories.add(category);
        }
    }

    /**
     * Remove a specific category from the chosenMovieCategories observablelist
     * @param category 
     */
    public void removeChosenMovieCategory(String category) {
        Iterator<String> i = chosenMovieCategories.iterator();
        while (i.hasNext())
        {
            String cat = i.next();

            if (cat.equals(category))
            {
                i.remove();
            }
        }
    }

    /**
     * Prepares the newly added or removed categories to be inserted or deleted in the database. 
     * The method compares the current list of categories to the new list of chosen categories. 
     * If the new chosenMovieCategories list does not contain the specific current movie category, 
     * it is added to the removeMovieCategory list.
     * If the new chosenMovieCategories list does contain the specific current movie category, 
     * it is removed from the removeMovieCategory list since it should not be added again.
     * The removeMovieCategory list and removeMovieCategory list is then looped through, 
     * adding/removing the categories in the database to this specific movie.
     */
    public void saveMovieCategories() throws SQLException 
    {
        Iterator<String> in = getCategories().iterator();
        while (in.hasNext())
        {
            String cat = in.next();

            if (!chosenMovieCategories.contains(cat))
            {
                removeMovieCategory.add(cat);
            }
            else
            {
                chosenMovieCategories.remove(cat);
            }

        }
        
        for(String cat : removeMovieCategory){
            mDAO.removeMovieCategory(1, cat);
        }
        
        for(String cat : chosenMovieCategories)
        {
            mDAO.addMovieCategory(1, cat);
        }
        
        removeMovieCategory.clear();
        chosenMovieCategories.clear();
    }
    
    
}
