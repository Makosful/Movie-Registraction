/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author B
 */
public class MovieDAO {
    
    DataBaseConnector db;

    
    public MovieDAO() throws IOException
    {
        db = new DataBaseConnector();
    }
    

   

    
    public void addCategory(String category) throws SQLException
    {
        System.out.println("Add:"+category);
    }
    
    
    public void removeCategory(String category) throws SQLServerException, SQLException
    {
        try(Connection con = db.getConnection())
        {
        System.out.println("Remove:"+category);
        
        String sql = "INSERT INTO Category Values (?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        preparedStatement.setString(1, category);
        preparedStatement.executeUpdate();
        }
    }


    public void addMovieCategory(int movieId, String category) 
    {
        System.out.println("Add:"+movieId+category);
        
    }
    
    public void removeMovieCategory(int movieId, String category) 
    {

        System.out.println("Remove:"+movieId+category);
        
    }

    public ObservableList<String> getAllCategories() throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            String sql = "SELECT name FROM Category";
            
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            ObservableList<String> categories = FXCollections.observableArrayList();
            while(rs.next())
            {
                categories.add(rs.getString("name"));
            }
            
            return categories;
        }
    }
    
    
}
