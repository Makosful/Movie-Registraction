/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.bll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import movie.registraction.be.Movie;
import movie.registraction.bll.exception.BLLException;
import movie.registraction.dal.DALManager;
import movie.registraction.dal.exception.DALException;

/**
 *
 * @author B
 */
public class Search
{

    private DALManager dal;
    private List<String> categories = new ArrayList();
    private HashMap<String, String> year = new HashMap();
    private int rating;
    private String order;
    private String sort;
    private String searchText;

    public Search() throws DALException
    {
        try
        {
            dal = new DALManager();
        }
        catch (DALException ex)
        {
            throw new DALException();
        }

        //initialize the instance variables that hold the search criteria
        order = "Title";
        sort = "Decending";
        searchText = "";
        rating = -1;
    }

    /**
     * Sets the goven categories to List of strings categories if its not
     * already there,
     * if it already existing remove it from the list
     *
     * @param category TODO
     */
    public void setSearchCategories(String category)
    {
        if (!categories.contains(category))
        {
            categories.add(category);
        }
        else
        {
            categories.remove(category);
        }

    }

    /**
     * Sets the given decades to hashmap year, by splitting the string
     * if its already in the hashmap remove it
     *
     * @param years TODO
     */
    public void setSearchYears(String years)
    {
        String[] decade = years.split("-");
        if (!year.containsKey(decade[0]))
        {
            year.put(decade[0], decade[1]);
        }
        else
        {
            year.remove(decade[0], decade[1]);
        }

    }

    /**
     * Sets the given rating, by removing all charecters in string
     * and converting the string to int
     *
     * @param rating TODO
     */
    public void setRating(String rating)
    {
        if (rating.equals("All"))
        {
            this.rating = -1;
        }
        else
        {
            String number = rating.replaceAll("\\D+", "");
            this.rating = Integer.parseInt(number);
        }
    }

    /**
     * Sets the specific order type as string
     *
     * @param order TODO
     */
    public void setOrder(String order)
    {
        this.order = order;
    }

    /**
     * Sets the given sort type as string
     *
     * @param sort TODO
     */
    public void setSort(String sort)
    {
        this.sort = sort;
    }

    /**
     * Sets the given search string
     *
     * @param searchText TODO
     */
    public void setSearchText(String searchText)
    {
        this.searchText = searchText;
    }

    /**
     * TODO
     *
     * @return TODO
     *
     * @throws BLLException TODO
     */
    public List<Movie> prepareSearch() throws BLLException
    {
        //Init the sql queries
        String sqlSearchCategory = "";
        String sqlSearchYear = "";
        String sqlOrderBy = "";
        String sqlSearch = "";
        String sqlRating = "";
        boolean searchNumeric = false;

        sqlSearchCategory = setSqlSearchCategory(sqlSearchCategory);

        sqlSearchYear = setSqlSearchYear(sqlSearchYear);

        sqlOrderBy = setSqlSearchOrder(sqlOrderBy);

        sqlRating = setSqlSearchRating(sqlRating, sqlSearchCategory, sqlSearchYear);

        //Stitches together the sql string for the searchText filter
        if (!searchText.isEmpty())
        {
            //If its the first filter add where clause in first iteration
            if (sqlSearchCategory.isEmpty() && sqlSearchYear.isEmpty() && sqlRating.isEmpty())
            {
                sqlSearch += "WHERE ";
            }
            //if the seachtext is numeric only seach in years
            if (isNumeric(searchText))
            {
                sqlSearch += "Movie.year = ?";
                searchNumeric = true;
            }
            else // if the seachtext is not numeric only seach in categories and movie titles
            {
                sqlSearch += "(Category.name LIKE ? OR Movie.name LIKE ?)";
                searchNumeric = false;
            }
        }

        //Check if categories are added, and at least one of the other filters
        // are added too, if they are, set an "AND" keyword in the sql query
        if (!sqlSearchCategory.isEmpty()
            && (!sqlSearchYear.isEmpty()
                || !sqlSearch.isEmpty()
                || !sqlRating.isEmpty()))
        {
            sqlSearchCategory += " AND ";
        }

        //Check if years filters are added, and at least one of the other remainding filters
        // are added too, if they are, set an "AND" keyword in the sql query
        if (!sqlSearchYear.isEmpty() && (!sqlSearch.isEmpty() || !sqlRating.isEmpty()))
        {
            sqlSearchYear += " AND ";
        }

        //Check if the rating filter is added, and at least one of the other remainding filters
        // are added too, if they are, set an "AND" keyword in the sql query
        if (!sqlRating.isEmpty() && !sqlSearch.isEmpty())
        {
            sqlRating += " AND ";
        }

        //add all the filter search strings to one

        String sqlString = sqlSearchCategory+sqlSearchYear+sqlRating+sqlSearch+sqlOrderBy;

        try
        {   //send the stitched together sql string and corresponding filters/search criteria to dal
            return dal.searchMovies(sqlString, categories, year, rating, searchText, searchNumeric);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }

    }

    /**
     * Checks if a given string is numeric
     *
     * @param str TODO
     *
     * @return TODO
     */
    public boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    /**
     * Stitches together the sql string for the categories filter
     *
     * @param sqlSearchCategory TODO
     *
     * @return TODO
     */
    private String setSqlSearchCategory(String sqlSearchCategory)
    {
        for (String criteria : categories)
        {

            //If its the first filter add where clause in first iteration
            if (sqlSearchCategory.isEmpty())
            {
                sqlSearchCategory += "WHERE ";
            }
            else //in between the subselects in the query
            {
                sqlSearchCategory += " AND ";
            }
            //subselect to select movies that contains all the chosen categories
            //and not only one of the categories
            sqlSearchCategory += "EXISTS( SELECT CatMovie.categoryId, CatMovie.movieId "
                                 + "FROM CatMovie JOIN Category ON Category.id = CatMovie.categoryId"
                                 + " WHERE Movie.id = CatMovie.movieId AND Category.name IN (?) )";
        }

        return sqlSearchCategory;
    }

    /**
     * Stitches together the sql string for the years(DECADES) filter
     *
     * @param sqlSearchYear TODO
     *
     * @return TODO
     */
    private String setSqlSearchYear(String sqlSearchYear)
    {
        for (Map.Entry<String, String> entry : year.entrySet())
        {

            //If its the first filter add where clause in first iteration
            if (categories.isEmpty() && sqlSearchYear.isEmpty())
            {
                sqlSearchYear = "WHERE (";
            }
            else if (sqlSearchYear.isEmpty())
            {
                sqlSearchYear = "(";
            }
            else
            {
                sqlSearchYear += " OR ";
            }

            sqlSearchYear += "(Movie.year > ? AND Movie.year < ?)";
        }

        if (!sqlSearchYear.isEmpty())
        {
            sqlSearchYear += ")";
        }

        return sqlSearchYear;
    }

    /**
     * Stitches together the sql string for the order filter
     *
     * @param sqlOrderBy TODO
     *
     * @return TODO
     */
    private String setSqlSearchOrder(String sqlOrderBy)
    {

        if (order.equals("Title"))
        {
            sqlOrderBy = " ORDER BY Movie.name";
        }
        if (order.equals("Rating"))
        {
            sqlOrderBy = " ORDER BY Movie.personalRating";
        }
        if (sort.equals("Descending"))
        {
            sqlOrderBy += " DESC";
        }
        else if (sort.equals("Ascending"))
        {
            sqlOrderBy += " ASC";
        }
        return sqlOrderBy;
    }

    /**
     * Stitches together the sql string for the rating filter
     *
     * @param sqlRating         TODO
     * @param sqlSearchYear     TODO
     * @param sqlSearchCategory TODO
     *
     * @return TODO
     */
    private String setSqlSearchRating(String sqlRating, String sqlSearchYear, String sqlSearchCategory)
    {
        if (rating != -1)
        {
            //If its the first filter add where clause in first iteration
            if (sqlSearchCategory.isEmpty() && sqlSearchYear.isEmpty())
            {
                sqlRating += "WHERE ";
            }
            //gets the movies with a rating above the chosen value
            sqlRating += "Movie.personalRating > ?";
        }
        return sqlRating;
    }

    /**
     * Clear the search filters
     *
     * @throws BLLException TODO
     */
    public void clearFilters() throws BLLException
    {
        System.out.println("HDWHD");
        categories.clear();
        year.clear();
        order = "Title";
        sort = "Decending";
        searchText = "";
        rating = -1;

    }

}
