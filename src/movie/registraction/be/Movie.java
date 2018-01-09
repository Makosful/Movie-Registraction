/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.be;

import java.io.File;
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
    private File file;
    
    List<String> categories;

    public Movie()
    {
        categories = new ArrayList();
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
    
    public void setFile(File file) 
    {
        this.file = file;
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

    public File getFile() {
        return file;
    }
    
}
