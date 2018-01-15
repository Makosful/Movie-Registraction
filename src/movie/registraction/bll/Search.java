/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.bll;


import java.util.List;


/**
 *
 * @author B
 */
public class Search
{
    
    public void prepareSearch(List<String> criterias, List<String> values)
    {
        String sqlSearchCategory = "";
        String sqlSearchYear = "";
        String sqlOrderBy = "";
        String sqlSearch = "";
        
        
        for(String criteria : criterias) {

            if(criteria.equals("Categories"))
            {
               
                if(sqlSearchCategory.isEmpty())
                {
                    sqlSearchCategory = "(";
                }
                else
                {   
                    sqlSearchCategory += " OR ";
                }
                
                sqlSearchCategory += "Categories.name = ?" ;
            }
            else if(criteria.equals("Year"))
            {
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
            else if(criteria.equals("Title"))
            {
                sqlOrderBy = " ORDER BY Movie.name";

            }
            else if(criteria.equals("Rating"))
            {
                sqlOrderBy = " ORDER BY Movie.personalRating";
            }
            else if(criteria.equals("DESC"))
            {
                sqlOrderBy += " DESC";
            }
            else if(criteria.equals("ASC"))
            {
                sqlOrderBy += " ASC";
            }
            else if(criteria.equals("Search"))
            {
                sqlSearch = "(Category.name LIKE ? OR Movie.name LIKE ? OR Movie.year = ?)";
              
            }
            
            
        }
        
        sqlSearchCategory += ")";
        sqlSearchYear += ")";
                
        if(!sqlSearchCategory.isEmpty() && !sqlSearchYear.isEmpty() || !sqlOrderBy.isEmpty() || !sqlSearch.isEmpty())
        {
            sqlSearchCategory += " AND ";
        }
        if(!sqlSearchYear.isEmpty() && !sqlOrderBy.isEmpty() || !sqlSearch.isEmpty())
        {
            sqlSearchYear += " AND ";
        }

        
        System.out.println(sqlSearchCategory+sqlSearchYear+sqlSearch+sqlOrderBy);
    }
    
}
