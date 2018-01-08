/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.be;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author B
 */
public class Movie 
{
    
    private String movieName;
    private int year;
    private double rating;
    
    List<String> categories = new ArrayList();

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
}
