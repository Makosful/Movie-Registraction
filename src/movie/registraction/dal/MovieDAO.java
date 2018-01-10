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
import movie.registraction.be.Movie;

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
     */
    public void addCategory(String category) throws DALException 
    {
        System.out.println("Add:"+category);
        try(Connection con = db.getConnection())
        {
        
        String sql = "INSERT INTO Category Values (?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        
        preparedStatement.setString(1, category);
        preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DALException();
        }
    }
    
    /**
     * Removes the provided category
     * @param category
     * @throws movie.registraction.dal.DALException
     */
    public void removeCategory(String category) throws DALException
    {
        System.out.println("Remove:"+category);
        
        try(Connection con = db.getConnection())
        {
            String sql = "DELETE FROM Category WHERE name = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, category);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DALException();
        }
    }

    /**
     * Adds a category to a specific movie
     * @param movieId
     * @param category
     * @throws movie.registraction.dal.DALException
     */
    public void addMovieCategory(int movieId, String category) throws DALException 
    {
        System.out.println("Add:"+movieId+category);
        
        int categoryId;
        categoryId = getCategoryId(category);
        
        try(Connection con = db.getConnection())
        {
            String sql = "INSERT INTO CatMovie (categoryId, movieId) Values (?, ?)";
            
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            
            preparedStatement.setInt(1, categoryId);
            preparedStatement.setInt(2, movieId);
            
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DALException();
        }
        
    }
    
    /**
     * Removes a category from a specific movie
     * @param movieId
     * @param category
     */
    public void removeMovieCategory(int movieId, String category) throws DALException 
    {

        System.out.println("Remove:"+movieId+category);
        
        int categoryId;
        categoryId = getCategoryId(category);
        
        try(Connection con = db.getConnection())
        {
            String sql = "DELETE FROM CatMovie WHERE movieId = ? AND categoryId = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            
            preparedStatement.setInt(1, movieId);
            preparedStatement.setInt(2, categoryId);
            
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DALException();
        }
        
    }

    
    /**
     * Returns all categories
     * @return List of strings
     * @throws movie.registraction.dal.DALException
     */
    public List<String> getAllCategories() throws DALException 
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
        } catch (SQLException ex) {
            throw new DALException();
        }
    }
    
    
    /**
     * Gets the id for the provided category
     * @param category
     * @return int id
     * @throws SQLException 
     */
    private int getCategoryId(String category) throws DALException
    {
        try (Connection con = db.getConnection())
        {
            String sql = "SELECT id FROM Category WHERE name = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, category);
            ResultSet rs = preparedStatement.executeQuery();

            rs.next();
            
            return rs.getInt("id");
            
        } catch (SQLException ex) {
            throw new DALException();
        }
    }
    
    /**
     * Gets all movies from the database. Because there is more categories for 
     * each movie, multiple rows of the same movies come from the database, and this method 
     * sorts it, so each movie can have several categories. If the movieid is the same 
     * as the previous movie in the while loop it adds a category instead of a new
     * movie instance
     * @return
     * @throws DALException 
     */
    public ObservableList<Movie> getAllMovies() throws DALException
    {
        try (Connection con = db.getConnection())
        {
            String sql = "SELECT "
                        + "Movie.id, "
                        + "Movie.name, "
                        + "Movie.filePath, "
                        + "Movie.imgPath, "
                        + "Movie.personalRating, "
                        + "Movie.imdbRating, "
                        + "Movie.year, "
                        + "Movie.lastView,"
                        + "Movie.movieLength, "
                        + "Category.name AS categoryName "
                        + "FROM Movie "
                        + "INNER JOIN CatMovie ON Movie.id = CatMovie.movieId "
                        + "INNER JOIN Category ON CatMovie.categoryId = Category.id";
            
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            ObservableList<Movie> movies = FXCollections.observableArrayList();
            Movie movie = new Movie();
            while (rs.next())
            {
                
                if(movie.getId() == rs.getInt("id"))
                {
                    movie.setCategories(rs.getString("categoryName"));
                }
                else
                {
                    movie = createMovieFromDB(rs);
              
                }    
                
                if (!movies.contains(movie))
                {
                    movies.add(movie);
                }

            }
            
            return movies;
        } catch (SQLException ex) {
            throw new DALException();
        }

        
    }

    
     private Movie createMovieFromDB(ResultSet rs) throws SQLException
     {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setMovieName(rs.getString("name"));
        movie.setMovieYear(rs.getInt("year"));
        movie.setPersonalRating(rs.getDouble("personalRating"));
        movie.setImdbRating(rs.getDouble("imdbRating"));
        movie.setLastView(rs.getString("lastView"));
        movie.setFilePath(rs.getString("filePath"));
        movie.setFileImg(rs.getString("imgPath"));
        movie.setMovieLength(rs.getInt("movieLength"));
        movie.setCategories(rs.getString("categoryName"));
        
        return movie;
     }

    
}
