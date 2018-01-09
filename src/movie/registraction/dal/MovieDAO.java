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
import java.util.logging.Level;
import java.util.logging.Logger;
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

    /**
     * Adds the provided category
     * @param category
     * @throws SQLException 
     */
    public void addCategory(String category) throws SQLException
    {
        System.out.println("Add:"+category);
        try(Connection con = db.getConnection())
        {
        
        String sql = "INSERT INTO Category Values (?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        
        preparedStatement.setString(1, category);
        preparedStatement.executeUpdate();
        }
    }
    
    /**
     * Removes the provided category
     * @param category
     * @throws SQLServerException
     * @throws SQLException 
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

    /**
     * Adds a category to a specific movie
     * @param movieId
     * @param category
     * @throws SQLServerException
     * @throws SQLException 
     */
    public void addMovieCategory(int movieId, String category) throws SQLServerException, SQLException 
    {
        System.out.println("Add:"+movieId+category);
        
        int categoryId = getCategoryId(category);
        
        try(Connection con = db.getConnection())
        {
            String sql = "INSERT INTO CatMovie (categoryId, movieId) Values (?, ?)";
            
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            
            preparedStatement.setInt(1, categoryId);
            preparedStatement.setInt(2, movieId);
            
            preparedStatement.executeUpdate();
        }
        
    }
    
    /**
     * Removes a category from a specific movie
     * @param movieId
     * @param category
     * @throws SQLServerException
     * @throws SQLException 
     */
    public void removeMovieCategory(int movieId, String category) throws SQLServerException, SQLException 
    {

        System.out.println("Remove:"+movieId+category);
        
        int categoryId = getCategoryId(category);
        
        try(Connection con = db.getConnection())
        {
            String sql = "DELETE FROM CatMovie WHERE movieId = ? AND categoryId = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            
            preparedStatement.setInt(1, movieId);
            preparedStatement.setInt(2, categoryId);
            
            preparedStatement.executeUpdate();
        }
        
    }

    
    /**
     * Returns all categories
     * @return
     * @throws SQLServerException
     * @throws SQLException 
     */
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
    
    
    /**
     * Gets the id for the provided category
     * @param category
     * @return
     * @throws SQLException 
     */
    private int getCategoryId(String category) throws SQLException
    {
        try (Connection con = db.getConnection())
        {
            String sql = "SELECT id FROM Category WHERE name = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, category);
            ResultSet rs = preparedStatement.executeQuery();

            rs.next();
            
            return rs.getInt("id");
            

        }
    }
    
}
