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
public class Movie
{

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
    private List<String> categories;

    /**
     * The constructor
     */
    public Movie()
    {
        categories = new ArrayList();
    }

    //<editor-fold defaultstate="collapsed" desc="ID">
    /**
     * Gets this Movie's ID
     *
     * @return Returnd this Movie's ID as an int
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets this Movie's ID
     *
     * @param id The ID of this Movie as an int
     */
    public void setId(int id)
    {
        this.id = id;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Title">
    /**
     * Gets this Movie's title
     *
     * @return Returns a String containing the title of this Movie
     */
    public String getMovieTitle()
    {
        return movieTitle;
    }

    /**
     * Sets the title of this Movie
     *
     * @param title The title of this Movie, as a String
     */
    public void setMovieTitle(String title)
    {
        this.movieTitle = title;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Year">
    /**
     * Gets the release year of this Movie
     *
     * @return Returns an int containing the release year of this Movie
     */
    public int getYear()
    {
        return year;
    }

    /**
     * Sets the year of this Movie
     *
     * @param year The year of this Movie, as an int
     */
    public void setYear(int year)
    {
        this.year = year;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="IMDB Rating">
    /**
     * Gets the IMDB rating
     *
     * @return Returns a double containing this Movie's IMDB rating
     */
    public double getImdbRating()
    {
        return imdbRating;
    }

    /**
     * Sets the IMDB rating
     *
     * @param rating The IMDB rating as a double
     */
    public void setImdbRating(double rating)
    {
        this.imdbRating = rating;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Personal Rating">
    /**
     * Gets this Movie's personal rating
     *
     * @return Returns a double containing this Movie's personal rating
     */
    public double getPersonalRating()
    {
        return personalRating;
    }

    /**
     * Sets the personal rating
     *
     * @param rating The rating of this Movie, as a double
     */
    public void setPersonalRating(double rating)
    {
        this.personalRating = rating;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Image Path">
    /**
     * Gets the path to this Movie's cover art
     *
     * @return Returns a String containing the path to this Movie's cover art
     */
    public String getImgPath()
    {
        return imgPath;
    }

    /**
     * Sets the path of this Movie's cover art
     *
     * @param path The path of the Movie's cover art
     */
    public void setImgPath(String path)
    {
        this.imgPath = path;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="File Path">
    /**
     * Get this m´Movie's local file path
     *
     * @return Returns a String containing this Movie's local file path
     */
    public String getFilePath()
    {
        return filePath;
    }

    /**
     * Sets the Movie's local path
     *
     * @param path The local path of the Movie, as a String
     */
    public void setFilePath(String path)
    {
        this.filePath = path;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Length">
    /**
     * Gets this Movie's length
     *
     * @return Returns an int containing this Movie's length in minutes
     */
    public int getMovieLength()
    {
        return movieLength;
    }

    /**
     * Sets the length of the movie
     *
     * @param minutes The length of this Movie in minutes
     */
    public void setMovieLength(int minutes)
    {
        this.movieLength = minutes;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Last View">
    /**
     * Gets the date this Movie was last viewed
     *
     * @return Returns a Date containing the time this Movie was last viewed
     */
    public Date getLastView()
    {
        return lastView;
    }

    /**
     * Sets the latest Date this movie was watched
     *
     * @param date The Date of this Movie's last viewing
     */
    public void setLastView(Date date)
    {
        this.lastView = date;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="IMDB Link">
    /**
     * Gets the link to IMDB
     *
     * @return Returns a String containing the link to this movies IMDB
     */
    public String getImdbLink()
    {
        return imdbLink;
    }

    /**
     * Sets this movies IMDB link
     *
     * @param link The IMDB link to set, as a String
     */
    public void setImdbLink(String link)
    {
        this.imdbLink = link;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Categories">
    /**
     * Gets this Movie's categories
     *
     * @return Returns a List of Strings with this Movie's categories
     */
    public List<String> getCategories()
    {
        return categories;
    }

    /**
     * Adds a category to this Movie
     *
     * @param category The category to add, as a String
     */
    public void setCategories(String category)
    {
        this.categories.add(category);
    }
//</editor-fold>

}
