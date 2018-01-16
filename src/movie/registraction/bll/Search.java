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
    
    public void prepareSearch(List<String> categories, List<String> year,  String order, String sort, String searchText)
    {
        String sqlSearchCategory = "";
        String sqlSearchYear = "";
        String sqlOrderBy = "";
        String sqlSearch = "";
        
        
        for(String criteria : categories) {

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
            sqlSearch = "(Category.name LIKE ? OR Movie.name LIKE ? OR Movie.year = ?)";

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
