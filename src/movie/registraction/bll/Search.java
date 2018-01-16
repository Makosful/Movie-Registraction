/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.bll;


import java.util.List;
import javafx.collections.ObservableList;
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
    }
    
    public ObservableList<Movie> prepareSearch(List<String> categories, List<String> year,  String order, String sort, String searchText) throws DALException
    {
        String sqlSearchCategory = "";
        String sqlSearchYear = "";
        String sqlOrderBy = "";
        String sqlSearch = "";
        boolean searchNumeric = false;
        
        for(String criteria : categories) {

            if(sqlSearchCategory.isEmpty())
            {
                sqlSearchCategory = "(";
            }
            else
            {   
                sqlSearchCategory += " OR ";
            }

            sqlSearchCategory += "Category.name = ?" ;
        }
        for(String criteria : year){

            if(sqlSearchYear.isEmpty())
            {
                sqlSearchYear = "(";
            }
            else
            {   
                sqlSearchYear += " OR ";
            }

            sqlSearchYear += "Movie.year = ?" ;
        }
        if(order.equals("Title"))
        {
            sqlOrderBy = " ORDER BY Movie.name";
        }
        
        if(order.equals("Rating"))
        {
            sqlOrderBy = " ORDER BY Movie.personalRating";
        }
        if(sort.equals("DESC"))
        {
            sqlOrderBy += " DESC";
        }
        else if(sort.equals("ASC"))
        {
            sqlOrderBy += " ASC";
        }
        
        if(!searchText.isEmpty())
        {  
            if(isNumeric(searchText))
            {
                sqlSearch = "Movie.year = ?";
                searchNumeric = true;
            }
            else
            {
                sqlSearch = "(Category.name LIKE ? OR Movie.name LIKE ?)";
                searchNumeric = false;
            }
        }
            

        
        sqlSearchCategory += ")";
        sqlSearchYear += ")";
                
        if(!sqlSearchCategory.isEmpty() && !sqlSearchYear.isEmpty() || !sqlSearch.isEmpty())
        {
            sqlSearchCategory += " AND ";
        }
        
        if(!sqlSearchYear.isEmpty() && !sqlSearch.isEmpty())
        {
            sqlSearchYear += " AND ";
        }
        
        String sqlString = sqlSearchCategory+sqlSearchYear+sqlSearch+sqlOrderBy;
        return dal.searchMovies(sqlString, categories, year, searchText, searchNumeric);
 
        
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
    
}
