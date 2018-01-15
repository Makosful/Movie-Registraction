/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.be;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author B
 */
public class Movie {

    private int id;
    private String movieTitle;
    private int year;
    private double imdbRating;
    private double personalRating;
    private String imgPath; //Movie poster
    private String filePath;    
    private int movieLength; //Minutes
    private Date lastView;
    private String imdbLink;

    public String getImdbLink()
    {
        return imdbLink;
    }

    public void setImdbLink(String imdbLink)
    {
        this.imdbLink = imdbLink;
    }
    List<String> categories;

    public Movie() {
        categories = new ArrayList();
    }

    public int getId() {
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


    public void setCategories(String category) 
    {
        this.categories.add(category);
    }

    public void setMovieName(String movieTitle) 
    {
        this.movieTitle = movieTitle;
    }

    public void setMovieYear(int year) 
    {
        this.year = year;
    }

    public void setImdbRating(double imdbRating) 
    {
        this.imdbRating = imdbRating;
    }

    public void setPersonalRating(double personalRating) 
    {
        this.personalRating = personalRating;
    }

    public void setFilePath(String filePath) 
    {
        this.filePath = filePath;
    }
    
    public void setFileImg(String fileImg) 
    {
        this.imgPath = imgPath;
    }
    
    public void setMovieLength(int movieLength) 
    {
        this.movieLength = movieLength;
    }

    public String getFileImg() 
    {
        return imgPath;
    }
    
    public int getMovieLength() 
    {
        return movieLength;
    }
    
    public String getMovieTitle() 
    {
        return movieTitle;
    }

    public int getYear() 
    {
        return year;
    }

    public double getImdbRating() 
    {
        return imdbRating;
    }

    public double getPersonalRating() 
    {
        return personalRating;
    }

    public String getFilePath() 
    {
        return filePath;
    }
    
    public String getImgPath() {
        return imgPath;
    }

    public Date getLastView() {
        return lastView;
    }

    public void setLastView(Date lastView) {
        this.lastView = lastView;
    }
    
    
}
