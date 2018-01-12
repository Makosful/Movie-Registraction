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
                        + "LEFT JOIN CatMovie ON Movie.id = CatMovie.movieId "
                        + "LEFT JOIN Category ON CatMovie.categoryId = Category.id";
            
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            ObservableList<Movie> movies = FXCollections.observableArrayList();
            Movie movie = new Movie();
            while (rs.next())
            {
                
                movie = createMovieFromDB(rs, movie);
              
                
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

    
    /**
     * Creates movie a movie object if it has not been creatd previously, if it 
     * has been created before, the resultsets movie-category is added to the 
     * previous movie object. This procedure is necessary because movies with several
     * categories appear several times in the reultsets, when using LEFT/RIGHT or INNER JOIN. 
     * @param rs
     * @param previousMovie
     * @return
     * @throws SQLException 
     */
     private Movie createMovieFromDB(ResultSet rs, Movie previousMovie) throws SQLException
     {
         
        if(previousMovie.getId() == rs.getInt("id"))
        {
            previousMovie.setCategories(rs.getString("categoryName"));
            return previousMovie;
        }
        else
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
            if(rs.getString("categoryName") != null){
                movie.setCategories(rs.getString("categoryName"));
            }

            return movie;
        
        }
     }
     
     
     /**
      * Add a new movie to the database
      * @param movieMetaData
      * @return
      * @throws DALException 
      */
     public int addMovie(String[] movieMetaData) throws DALException
     {
      
            
            try (Connection con = db.getConnection())
            {
                int id;
                java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
                String sqlInsert = "INSERT INTO Movie "
                                 + "(name, filePath, imgPath, lastView, personalRating, imdbRating, year, movieLength) "
                                 + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
                PreparedStatement preparedStatement = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, movieMetaData[0]);
                preparedStatement.setString(2, movieMetaData[1]);
                preparedStatement.setString(3, movieMetaData[2]);
                preparedStatement.setDate(4, null);
                preparedStatement.setDouble(5, Double.parseDouble(movieMetaData[3]));
                preparedStatement.setDouble(6, Double.parseDouble(movieMetaData[4]));
                preparedStatement.setInt(7, Integer.parseInt(movieMetaData[5]));
                preparedStatement.setInt(8, Integer.parseInt(movieMetaData[6]));
                
                preparedStatement.executeUpdate();

                ResultSet rsi = preparedStatement.getGeneratedKeys();

                rsi.next();

                id = rsi.getInt(1);

                return id;
            }
            catch (SQLException ex)
            {
                throw new DALException();
            }
            
            
            
            
            
     }

    public void setPersonalRating(int movieId, int personalRating) throws DALException {
       
        try (Connection con = db.getConnection())
        {

            String sqlInsert = "UPDATE Movie "
                             + "SET Movie.personalRating = ? "
                             + "WHERE id = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sqlInsert);
            preparedStatement.setInt(1, personalRating);
            preparedStatement.setInt(2, movieId);
            preparedStatement.executeUpdate();

        
            
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    
    /**
     * Remove a specific movie and its categories depending in the movie id
     * @param movieId
     * @throws DALException 
     */
    public void removeMovie(int movieId) throws DALException
    {
        try (Connection con = db.getConnection())
        {

            String sql = "DELETE Movie FROM Movie "
                         + "INNER JOIN CatMovie ON Movie.id = CatMovie.movieId "
                         + "WHERE Movie.id = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, movieId);
            preparedStatement.execute();

        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }
    
}
