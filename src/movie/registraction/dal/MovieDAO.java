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
import java.util.ArrayList;
import java.util.List;
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

    /*
        Adding a category.
    */
    public void addCategory(String category) throws SQLException
    {
        System.out.println("Add:"+category);
        try(Connection con = db.getConnection())
        {
        
        String sql = "INSERT INTO Category Values (?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        preparedStatement.setString(1, category);
        preparedStatement.executeUpdate();
        }
    }
    
    /*
        Removing a category.
    */
    public void removeCategory(String category) throws SQLServerException, SQLException
    {
        System.out.println("Remove:"+category);
        
        try(Connection con = db.getConnection())
        {
            String sql = "DELETE FROM Category WHERE name = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, category);
            preparedStatement.executeUpdate();
        }
    }

    /*
        Method to add a category to a specific movie.
    */
    public void addMovieCategory(int movieId, String category) throws SQLServerException, SQLException 
    {
        System.out.println("Add:"+movieId+category);
        try(Connection con = db.getConnection())
        {
            String sql = "INSERT INTO CatMovie Values (?), (?)";
            
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            preparedStatement.setString(1, category);
            preparedStatement.setInt(2, movieId);
            
            preparedStatement.executeUpdate();
        }
        
    }
    /*
        Method to remove a genre from a movie.
    */
    public void removeMovieCategory(int movieId, String category) throws SQLServerException, SQLException 
    {

        System.out.println("Remove:"+movieId+category);
        
        try(Connection con = db.getConnection())
        {
            String sql = "DELETE FROM CatMovie WHERE movieId = ? AND categoryId = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            
            preparedStatement.setInt(1, movieId);
            preparedStatement.setString(2, category);
            
            preparedStatement.executeUpdate();
        }
        
    }

    public List<String> getAllCategories() throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            String sql = "SELECT name FROM Category";
            
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            List<String> categories = new ArrayList();
           
            while(rs.next())
            {
                categories.add(rs.getString("name"));
            }
            
            return categories;
        }
    }
    
    
}
