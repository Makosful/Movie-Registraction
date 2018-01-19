package movie.registraction.dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import movie.registraction.be.Movie;
import movie.registraction.dal.exception.DALException;

/**
 * @author B
 */
public class MovieDAO
{

    DataBaseConnector db;

    public MovieDAO()
    {
        db = new DataBaseConnector();
    }

    /**
     * Adds the provided category
     *
     * @param category The category to add, as a String
     *
     * @throws DALException Throws and exception if it fails to access the
     *                      database
     */
    public void addCategory(String category) throws DALException
    {
        try (Connection con = db.getConnection())
        {

            String sql = "INSERT INTO Category Values (?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, category);
            preparedStatement.executeUpdate();
            
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Removes the provided category
     *
     * @param category The category to remove, as a String
     *
     * @throws DALException Throws and exception if it fails to access the
     *                      database
     */
    public void removeCategory(String category) throws DALException
    {
        try (Connection con = db.getConnection())
        {
            
            String sql = "DELETE FROM Category WHERE name = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, category);
            preparedStatement.executeUpdate();
            
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Adds a category to a specific movie
     *
     * @param id       The ID of the Movie
     * @param category The category to add
     *
     * @throws DALException Throws and exception if it fails to access the
     *                      database
     */
    public void addMovieCategory(int id, String category) throws DALException
    {
        int categoryId;
        categoryId = getCategoryId(category);

        try (Connection con = db.getConnection())
        {
            
            String sql = "INSERT INTO CatMovie (categoryId, movieId) Values (?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, categoryId);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
            
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }

    }

    /**
     * Removes a category from a specific movie
     *
     * @param id       Th ID of the Movie
     * @param category The category to remove
     *
     * @throws DALException Throws and exception if it fails to access the
     *                      database
     */
    public void removeMovieCategory(int id, String category) throws DALException
    {
        int categoryId;
        categoryId = getCategoryId(category);

        try (Connection con = db.getConnection())
        {
            
            String sql = "DELETE FROM CatMovie WHERE movieId = ? AND categoryId = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, categoryId);
            preparedStatement.executeUpdate();
            
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }

    }

    /**
     * Returns all categories
     *
     * @return List of Strings containing all the categories
     *
     * @throws DALException Throws and exception if it fails to access the
     *                      database
     */
    public List<String> getAllCategories() throws DALException
    {
        try (Connection con = db.getConnection())
        {
            String sql = "SELECT name FROM Category";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            List<String> categories = new ArrayList();

            while (rs.next())
            {
                categories.add(rs.getString("name"));
            }

            return categories;
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Gets the id for the provided category
     *
     * @param category
     *
     * @return The ID of the category as an int
     *
     * @throws SQLException Throws and exception if it fails to access the
     *                      database
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

        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Gets all movies from the database. Because there is more categories for
     * each movie, multiple rows of the same movies come from the database, and
     * this method
     * sorts it, so each movie can have several categories. If the movieid is
     * the same
     * as the previous movie in the while loop it adds a category instead of a
     * new
     * movie instance
     *
     * @return Returns an ObservableList of all Movies
     *
     * @throws DALException Throws and exception if it fails to access the
     *                      database
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
                         + "Movie.lastView, "
                         + "Movie.movieLength, "
                         + "Movie.imdbLink, "
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

        }
        catch (SQLException ex)
        {
            throw new DALException();
        }

    }

    /**
     * Creates movie a movie object if it has not been creatd previously, if it
     * has been created before, the resultsets movie-category is added to the
     * previous movie object. This procedure is necessary because movies with
     * several
     * categories appear several times in the reultsets, when using LEFT/RIGHT
     * or INNER JOIN.
     *
     * @param rs            The ResultSet retrievd from the DataBase
     * @param previousMovie the movieobject passed is the returned value of this method
     *                      if the movie object is the same as the iteration before
     *                      only add category, else create a new movie object, in both
     *                      cases it is returned and used as parameter in next call.
     *
     * @return Returns a Movie created from the DataBase
     *
     * @throws SQLException Throws and exception if it fails to access the
     *                      database
     */
    private Movie createMovieFromDB(ResultSet rs, Movie previousMovie) throws SQLException
    {

        if (previousMovie.getId() == rs.getInt("id"))
        {
            previousMovie.setCategories(rs.getString("categoryName"));
            return previousMovie;
        }
        else
        {

            Movie movie = new Movie();
            movie.setId(rs.getInt("id"));
            movie.setMovieTitle(rs.getString("name"));
            movie.setYear(rs.getInt("year"));
            movie.setPersonalRating(rs.getDouble("personalRating"));
            movie.setImdbRating(rs.getDouble("imdbRating"));
            movie.setLastView(rs.getDate("lastView"));
            movie.setFilePath(rs.getString("filePath"));
            movie.setImgPath(rs.getString("imgPath"));
            movie.setMovieLength(rs.getInt("movieLength"));
            movie.setImdbLink(rs.getString("imdbLink"));
            movie.setCategories(rs.getString("categoryName"));

            return movie;

        }
    }

    /**
     * Add a new movie to the database
     *
     * @param metadata A String Array containing the MetaData of a Movie
     * @param filePath The local file path of the Movie
     *
     * @return Returns the dataBase ID of the Movie
     *
     * @throws DALException Throws and exception if it fails to access the
     *                      database
     */
    public int addMovie(String[] metadata, String filePath) throws DALException
    {
        try (Connection con = db.getConnection())
        {
            int id;

            String sqlInsert = "INSERT INTO Movie "
                               + "(name, filePath, imgPath, imdbLink, personalRating, imdbRating, year, movieLength) "
                               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, metadata[0]);
            preparedStatement.setString(2, filePath);
            preparedStatement.setString(3, metadata[4]);
            preparedStatement.setString(4, metadata[6]);
            preparedStatement.setDouble(5, -1);
            preparedStatement.setDouble(6, Double.parseDouble(metadata[3]));
            preparedStatement.setInt(7, Integer.parseInt(metadata[1]));
            preparedStatement.setInt(8, Integer.parseInt(metadata[2]));
            
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

    /**
     * Sets the users rating for a specific movie in the database
     *
     * @param id             The ID of the Movie to rate
     * @param personalRating The value of the personal rating
     *
     * @throws DALException Throws and exception if it fails to access the
     *                      database
     */
    public void setPersonalRating(int id, int personalRating) throws DALException
    {
        try (Connection con = db.getConnection())
        {

            String sqlInsert = "UPDATE Movie "
                               + "SET Movie.personalRating = ? "
                               + "WHERE id = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sqlInsert);
            preparedStatement.setInt(1, personalRating);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Remove a specific movie and its categories depending in the movie id
     *
     * @param id The ID of the Movie
     *
     * @throws DALException Throws and exception if it fails to access the
     *                      database
     */
    public void removeMovie(int id) throws DALException
    {
        try (Connection con = db.getConnection())
        {
            System.out.println(id);

            String sql = "DELETE Movie FROM Movie "
                         + "LEFT JOIN CatMovie ON Movie.id = CatMovie.movieId "
                         + "WHERE Movie.id = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();

        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * When playing a movie this mehtod is called,
     * it sets the last view of the specified movie with the given movie ID
     *
     * @param id The ID of the Movie
     *
     * @throws DALException Throws and exception if it fails to access the
     *                      database
     */
    public void setLastView(int id) throws DALException
    {
        try (Connection con = db.getConnection())
        {
            String sqlInsert = "UPDATE Movie "
                               + "SET Movie.lastView = GETDATE() "
                               + "WHERE id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sqlInsert);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * This method is to get a imgPath from a specific movie.
     * So that it can be thrown into the tilepane.
     *
     * @param title Thge title of the Movie
     *
     * @return Returns a String containing the URL to the Movie's image
     *
     * @throws DALException Throws and exception if it fails to access the
     *                      database
     */
    public String getSpecificMovieImage(String title) throws DALException
    {
        String imageLink = null;
        try (Connection con = db.getConnection())
        {
            String sqlInsert = "SELECT imgPath FROM Movie WHERE name = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sqlInsert);
            preparedStatement.setString(1, title);
            ResultSet rs = preparedStatement.executeQuery();
            
            if (rs.next())
            {
                imageLink = rs.getString("imgPath");
            }
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
        return imageLink;
    }

    /**
     * Adds the given sql String to the sql query.
     * Then it dynamically creates the prepared statemens according to the
     * criterias
     * Such as the list of categories. Then it calls the createMovieFromDB to
     * instantiate
     * Movie objects from the resultset and returns a list of the resulting
     * movies.
     *
     * @param sqlString     String containing the sql query
     * @param categories    List of categories as criteria for the search
     * @param year          HashMap of years as criteria
     * @param rating        the rating number movies should be above in the search
     * @param searchText    The text String to search for
     * @param searchNumeric boolean if true the searchtext is for years only
     *                      else title and category
     *
     * @return Returns an ArrayList of Movies containing the search results
     *
     * @throws DALException Throws and exception if it fails to access the
     *                      database
     */
    public List<Movie> searchMovies(String sqlString,
                                    List<String> categories,
                                    HashMap<String, String> year,
                                    int rating,
                                    String searchText,
                                    boolean searchNumeric) throws DALException
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
                         + "Movie.lastView, "
                         + "Movie.movieLength, "
                         + "Movie.imdbLink, "
                         + "Category.name AS categoryName "
                         + "FROM Movie "
                         + "LEFT JOIN CatMovie ON Movie.id = CatMovie.movieId "
                         + "LEFT JOIN Category ON CatMovie.categoryId = Category.id "
                         + sqlString;

            PreparedStatement preparedStatement = con.prepareStatement(sql);

            int i = 0;
            
            for (String category : categories)
            {
                i++;
                preparedStatement.setString(i, category);
            }

            for (Map.Entry<String, String> y : year.entrySet())
            {
                String key = y.getKey();
                String value = y.getValue();
                
                i++;
                preparedStatement.setInt(i, Integer.parseInt(key));

                i++;
                preparedStatement.setInt(i, Integer.parseInt(value));
            }

            if (rating != -1)
            {
                i++;
                preparedStatement.setInt(i, rating);
            }

            if (!searchText.isEmpty())
            {
                if (searchNumeric)
                {
                    i++;
                    preparedStatement.setInt(i, Integer.parseInt(searchText));
                }
                else
                {
                    i++;
                    preparedStatement.setString(i, "%" + searchText + "%");
                    
                    i++;
                    preparedStatement.setString(i, "%" + searchText + "%");
                }
            }

            ResultSet rs = preparedStatement.executeQuery();

            List<Movie> movies = new ArrayList();
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

        }
        catch (SQLException ex)
        {
            throw new DALException();
        }

    }

}
