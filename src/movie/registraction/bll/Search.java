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
import movie.registraction.dal.DALException;
import movie.registraction.dal.DALManager;


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
        
        order = "Title";
        sort = "Decending";
        searchText = "";
        rating = -1;
    }
    
    public void setSearchCategories(String category)
    {
        if(!categories.contains(category))
        {
            categories.add(category);
        }
        else
        {
            categories.remove(category);
        }

    }
    
    public void setSearchYears(String years)
    {
        String[] decade = years.split("-");
        if(!year.containsKey(decade[0])){
            year.put(decade[0], decade[1]);
        }
        else
        {
            year.remove(decade[0], decade[1]);
        }
        
    }
    
    public void setRating(String rating)
    {
        String number = rating.replaceAll("\\D+","");
        this.rating = Integer.parseInt(number);
    }
    
    public void setOrder(String order)
    {
        this.order = order;
    }
    
    public void setSort(String sort)
    {
        this.sort = sort;
    }
    
    public void setSearchText(String searchText)
    {
        this.searchText = searchText;
    }
    
    
    
    public List<Movie> prepareSearch() throws DALException
    {
        String sqlSearchCategory = "";
        String sqlSearchYear = "";
        String sqlOrderBy = "";
        String sqlSearch = "";
        String sqlRating = "";
        boolean searchNumeric = false;
        
        
        sqlSearchCategory = setSqlSearchCategory(sqlSearchCategory);
        
        sqlSearchYear = setSqlSearchYear(sqlSearchYear);

        sqlOrderBy = setSqlSearchOrder(sqlOrderBy);
        
        sqlRating = setSqlSearchRating(sqlRating);
        
        if(!searchText.isEmpty())
        {  
            if(sqlSearchCategory.isEmpty() && sqlSearchYear.isEmpty())
            {
                sqlSearch += "WHERE ";
            }
            
            if(isNumeric(searchText))
            {
                sqlSearch += "Movie.year = ?";
                searchNumeric = true;
            }
            else
            {
                sqlSearch += "(Category.name LIKE ? OR Movie.name LIKE ?)";
                searchNumeric = false;
            }
        }
        
        
        if(!sqlSearchCategory.isEmpty() && (!sqlSearchYear.isEmpty() || !sqlSearch.isEmpty() || !sqlRating.isEmpty()))
        {
            sqlSearchCategory += " AND ";
        }
        
        if(!sqlSearchYear.isEmpty() && (!sqlSearch.isEmpty() || !sqlRating.isEmpty()))
        {
            sqlSearchYear += " AND ";
        }
        
        if(!sqlRating.isEmpty() && !sqlSearch.isEmpty())
        {
            sqlRating += " AND ";
        }
        
        String sqlString = sqlSearchCategory+sqlSearchYear+sqlRating+sqlSearch+sqlOrderBy;
        System.out.println(sqlString);
        return dal.searchMovies(sqlString, categories, year, rating, searchText, searchNumeric);

    }
    
    /**
     * Checks if a given string is numeric
     *
     * @param str
     *
     * @return
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
    
    private String setSqlSearchCategory(String sqlSearchCategory)
    {
        for(String criteria : categories) {

            if(sqlSearchCategory.isEmpty())
            {
                sqlSearchCategory += "WHERE (";
            }
            else
            {   
                sqlSearchCategory += " OR ";
            }

            sqlSearchCategory += "Category.name = ?" ;
        }
        
        if(!sqlSearchCategory.isEmpty())
        {
            sqlSearchCategory += ")";
        }
        
        return sqlSearchCategory; 
    }
    
    
    private String setSqlSearchYear(String sqlSearchYear)
    {
        for(Map.Entry<String, String> entry : year.entrySet()) {

            if(categories.isEmpty() && sqlSearchYear.isEmpty())
            {
                sqlSearchYear = "WHERE (";
            }
            else if(sqlSearchYear.isEmpty())
            {
                sqlSearchYear = "(";
            }
            else
            {   
                sqlSearchYear += " OR ";
            }

            sqlSearchYear += "(Movie.year > ? AND Movie.year < ?)" ;
        }
        
        if(!sqlSearchYear.isEmpty())
        {
            sqlSearchYear += ")";
        }

        return sqlSearchYear;
    }
    
    private String setSqlSearchOrder(String sqlOrderBy)
    {
        
        if(order.equals("Title"))
        {
            sqlOrderBy = " ORDER BY Movie.name";
        }
        if(order.equals("Rating"))
        {
            sqlOrderBy = " ORDER BY Movie.personalRating";
        }
        if(sort.equals("Descending"))
        {
            sqlOrderBy += " DESC";
        }
        else if(sort.equals("Ascending"))
        {
            sqlOrderBy += " ASC";
        }
        return sqlOrderBy;
    }
    
    private String setSqlSearchRating(String sqlRating)
    {
        if(rating != -1)
        {
            sqlRating = "Movie.personalRating > ?";
        }
        return sqlRating; 
    }
   
}
