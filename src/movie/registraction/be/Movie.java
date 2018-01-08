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
public class Movie {
    
    List<String> categories = new ArrayList();

    public List<String> getCategories() 
    {
        return categories;
    }

    public void setCategories(List<String> categories) 
    {
        this.categories = categories;
    }
}
