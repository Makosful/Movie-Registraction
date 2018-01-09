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
public class Categories {
    
    private MovieDAO mDAO;
    private List<String> removeCategory;
    ObservableList<String> categories = FXCollections.observableArrayList();
    
    public Categories() throws IOException
    {
        mDAO = new MovieDAO();
        removeCategory = new ArrayList();
    }
    
    public ObservableList<String> allCategories() throws SQLException
    {
        categories.addAll(mDAO.getAllCategories());
        return categories; 
    }
    
    
    public void addChosenCategory(String category) {
        if(!categories.contains(category)){
            categories.add(category);
        }
    }

    public void removeChosenCategory(String category) {        
        Iterator<String> i = categories.iterator();
        while (i.hasNext())
        {
            
            String cat = i.next();

            if (cat.equals(category))
            {
                i.remove();
            }
        }
    }
    
    
    
    public void saveCategories() throws SQLException 
    {
        Iterator<String> in = mDAO.getAllCategories().iterator();
        while (in.hasNext())
        {
            String cat = in.next();

            if (!categories.contains(cat))
            {
                removeCategory.add(cat);
            }
            else
            {
                categories.remove(cat);
            }

        }
        
        for(String cat : removeCategory){
            mDAO.removeCategory(cat);
        }
        
        for(String cat : categories)
        {
             mDAO.addCategory(cat);
        }
        
        removeCategory.clear();
        categories.clear();
    }
    
}
