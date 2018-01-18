package movie.registraction.bll;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import movie.registraction.be.Movie;
import movie.registraction.bll.exception.BLLException;
import movie.registraction.dal.DALManager;
import movie.registraction.dal.exception.DALException;

/**
 *
 * @author Axl
 */
public class BLLManager
{

    // Same Layer objects
    OmdbSearch omdb;

    // DAL Layer manager
    DALManager dal;

    //Filter search
    Search search;

    //
    ChangeCategories categories;

    /**
     * Constructor
     *
     * @throws BLLException Throws a BLLException if any of the objects fail to
     *                      initiate
     */
    public BLLManager() throws BLLException
    {
        try
        {
            search = new Search();
            dal = new DALManager();
            omdb = new OmdbSearch();
            categories = new ChangeCategories();
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Creates a URL for OMDB
     * Takes the given text and creates a new URL for the OMDB website to make a
     * search for the title
     *
     * @param text The text tosearch for
     *
     * @return a URL for OMDB
     *
     * @throws BLLException Throws BLLException if it fails to access OMDB
     */
    public URL getOmdbTitleResult(String text) throws BLLException
    {
        return omdb.getOmdbTitleResult(text);
    }

    /**
     * Gets the OMDB search result
     *
     * @param url The URL to queue the search on the API
     *
     * @return Returns a String containing the search results
     *
     * @throws BLLException Throws BLLException if it fails to access OMDB
     */
    public String getSearchResult(URL url) throws BLLException
    {
        try
        {
            // Opens up a connection for us to READ from a buffered reader in an
            // inputstream from our API link.
            URLConnection con = url.openConnection();
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            String res = "";
            while ((inputLine = buffRead.readLine()) != null)
            {
                res += inputLine + "\n";
            }

            return res;
        }
        catch (IOException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Attempts to open a file in the desktop's standard application. May not
     * work on all Operating Systems
     *
     * @param file The tile to open
     *
     * @throws BLLException Throws BLLException if it fails to open the default
     *                      application
     */
    public void openFileInNative(File file) throws BLLException
    {
        if (Desktop.isDesktopSupported())
        {
            try
            {
                Desktop.getDesktop().open(file);
            }
            catch (IOException ex)
            {
                throw new BLLException();
            }
        }
        else
        {
            throw new UnsupportedOperationException("This feature is not supported on your platform");
        }
    }

    /**
     * Saves the library path to the root folder
     *
     * @param path The file name and or path of the file to be written to. If no
     *             path prefixes the file name, the file will be stored in the
     *             application's root folder
     *
     * @throws BLLException Throws BLLEXception if it fails to write (to) the
     *                      file
     */
    public void saveDirectory(String path) throws BLLException
    {
        try
        {
            dal.saveDirectory(path);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Reads the first line of the file
     *
     * @param path The path of the file to read
     *
     * @return Returns a String containing the comntent of the file's first line
     *
     * @throws BLLException Throws a BLLException if it failed to read the file
     */
    public String readFile(String path) throws BLLException
    {
        try
        {
            return dal.loadDirectory(path);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Gets the list of movies in the library
     * Return the list of movies in the library as a String ArrayList
     *
     * @param filter A list containing the file formats to allow through the
     *               filter
     *
     * @return Returns a List of Paths with all the files passed through the
     *         filter
     *
     * @throws BLLException Throws a BLLException if it fails to read the
     *                      folders and files
     */
    public ArrayList<Path> getMovieList(ArrayList<String> filter) throws BLLException
    {
        try
        {
            return dal.getMovieList(filter);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Sets a listener on the library directory
     */
    public void setDirectoryWatch()
    {
        dal.setDirectoryWatch();
    }

    /**
     * Sends metadata to dataaccess layer to insert a movie
     *
     * @param metadata The MetaData to send, as a String Array
     * @param path     The Path of the movie, as a String
     *
     * @throws BLLException Throws a BLLException of it failed to access to the
     *                      storage
     */
    public void addMovie(String[] metadata, String path) throws BLLException
    {
        try
        {
            dal.addMovie(metadata, path);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Tries to get all the movies
     *
     * @return Returns an ObservableList of Movies with all the movies from
     *         storage
     *
     * @throws BLLException Throws a BLLException if it fails to access the
     *                      storage
     */
    public ObservableList<Movie> getAllMovies() throws BLLException
    {
        try
        {
            return dal.getAllMovies();
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Sets the ImageView ID
     *
     * @param file      The Movie inside the ImageView
     * @param imageView The ImageView to set the ID of
     *
     * @throws BLLException Throws an exception if it fails to access the
     *                      storage
     */
    public void setImageId(File file, ImageView imageView) throws BLLException
    {
        for (Movie movie : getAllMovies())
        {
            // Better solution?
            // String fileName = splitDot(file.getName());
            //
            // Removing the dot and text after, so only the text is in the string.
            String fileName = file.getName().split("\\.")[0];

            // If database movie title matches chosenfile name.
            if (movie.getMovieTitle().equalsIgnoreCase(fileName))
            {
                // Changing integer to string, as imageview requires string.
                String idToString = Integer.toString(movie.getId());
                imageView.setId(idToString);
            }
        }
    }

    /**
     * Formats the metadata from searchresult, so its ready to be put in the db
     *
     * @param url The URL of the movie to get MetaData on
     *
     * @return Retuns a String Array with all the MetaData
     *
     * @throws BLLException Throws a BLLException if it fails to access the API
     */
    public String[] getSearchMetaData(URL url) throws BLLException
    {
        String searchResult = this.getSearchResult(url);

        searchResult = searchResult.replace("{", "")
                .replace("}", "")
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "");
        String[] meta = searchResult.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        System.out.println(searchResult);
        for (String meta1 : meta) //get title
        {
            if (meta1.contains("Title:"))
            {
                meta[0] = meta1;
            }
            //get year
            else if (meta1.contains("Year:"))
            {
                meta[1] = meta1;
            }
            //remove "min" after number of minutes
            else if (meta1.contains("Runtime:"))
            {
                meta[2] = meta1.substring(0, meta1.lastIndexOf(" "));
            }
            //get imdb rating
            else if (meta1.contains("imdbRating:"))
            {
                meta[3] = meta1;
            }
            //Poster
            else if (meta1.contains("Poster:"))
            {
                meta[4] = meta1;
            }
            //get all the categories by using substring with genre and director as start and end index
            else if (meta1.contains("Genre:"))
            {

                meta[5] = searchResult.substring(searchResult.indexOf("Genre"), searchResult.indexOf("Director"));
                meta[5] = meta[5].replace(",", "");
                System.out.println(meta[5]);
            }
            //imdb id
            else if (meta1.contains("imdbID:"))
            {
                meta[6] = meta1;
            }
        }

        //new array to hold final result
        String[] metaData = new String[7];
        for (int i = 0; i < metaData.length; i++)
        //remove metadata title until ":" appears and one index after
        {
            metaData[i] = meta[i].substring(meta[i].lastIndexOf(":") + 1);
        }
        return metaData;
    }

    /**
     * This method is to get a imgPath from a specific movie. So that it can be
     * thrown into the tilepane.
     *
     * @param title The title of the movie
     *
     * @return Returnd a String containing the URL for the Movie image
     *
     * @throws BLLException Throws an exception if it fails to acces the API
     */
    public String getSpecificMovieImage(String title) throws BLLException
    {
        try
        {
            return dal.getSpecificMovieImage(title);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Splits a String up every time it sees a . (peroid)
     *
     * @param string The String to split up
     *
     * @return Returns the same String, but now plit up
     */
    public String splitDot(String string)
    {
        return string.split("\\.")[0];
    }

    /**
     * If there is a movie last seen over 2 years ago and the movie i rated
     * under 6
     * ask the user if the movie should be deleted
     *
     * @throws BLLException Throws an exception if it fails to access the
     *                      storage
     */
    public void findOldAndBadMovies() throws BLLException
    {
        try
        {
            Date twoYearsBefore = new Date(System.currentTimeMillis() - (2 * 365 * 24 * 60 * 60 * 1000));
            for (Movie m : getAllMovies())
            {
                if (m.getLastView() != null)
                {
                    if (m.getLastView().after(twoYearsBefore) && m.getPersonalRating() < 6)
                    {
                        Alert alert = new Alert(AlertType.WARNING,
                                                "Det er over 2 år siden du sidst har set " + m.getMovieTitle() + ","
                                                + " og du har givet den en rating på " + m.getPersonalRating()
                                                + " , har du lyst til at slette den?",
                                                ButtonType.YES, ButtonType.NO);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.YES)
                        {
                            dal.removeMovie(m.getId());
                        }
                        else
                        {

                        }
                    }
                }

            }
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Finding the specific movie, which has same id as imageid
     * This is to find the object, that belongs to the image.
     *
     * @param imageView The ImageView which to look in
     *
     * @return Return the Movie object within the ImageView
     *
     * @throws BLLException Throws an exception if it fails to access storage
     */
    public Movie getMovieInfo(ImageView imageView) throws BLLException
    {
        Movie movieObject = null;
        for (Movie movie : getAllMovies())
        {
            //  Finding the ID that belongs to the movie.
            if (Integer.parseInt(imageView.getId()) == movie.getId())
            {
                movieObject = movie;
            }
        }
        return movieObject;
    }

    /**
     * Check if movie already exists in the database
     *
     * @param title The title of the Movie
     *
     * @return Returns true if it found a match, false if the movie doesn't
     *         exist in the database
     *
     * @throws BLLException Throws an excption if it fails to access the storage
     */
    public boolean movieAlreadyExisting(String title) throws BLLException
    {
        boolean isAlreadyInDataBase = false;

        for (Movie m : getAllMovies())
        {
            if (m.getMovieTitle().toLowerCase().equals(title.toLowerCase()))
            {
                isAlreadyInDataBase = true;
            }

        }
        return isAlreadyInDataBase;
    }

    /**
     * Removes a Movie from the database
     *
     * @param id The ID of the Movie
     *
     * @throws BLLException throws an exception if it fails to access the
     *                      database
     */
    public void removeMovie(int id) throws BLLException
    {
        try
        {
            dal.removeMovie(id);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Gets the list of files that's been moved and or altered.
     *
     * @see "The WatchService in the DAL layer"
     *
     * @return Returns an ObservableList of Paths containing the all the files
     *         that's been changed
     */
    public ObservableList<Path> getChangeList()
    {
        return dal.getChangeList();
    }

    /**
     * Gets all the files that's been added to the library
     *
     * @param movieList The list of all movies
     *
     * @return Returns a List of Strings containing the files added to the
     *         library
     *
     * @throws BLLException Throws an exception if it fails to access the
     *                      database
     */
    public List<String> getUpdateLibrary(ArrayList<Path> movieList) throws BLLException
    {
        List<String> databaseList = new ArrayList<>();
        List<String> localList = new ArrayList<>();

        for (Movie movy : getAllMovies())
        {
            databaseList.add(movy.getFilePath());
        }

        for (Path path : movieList)
        {
            localList.add(path.toString());
        }

        localList.removeAll(databaseList);

        return localList;
    }

    /**
     * Updates the last viewed date of a Movie
     *
     * @param id The ID if the Movie to update
     *
     * @throws BLLException Throws an exception if it fails to access the
     *                      storage
     */
    public void setLastView(int id) throws BLLException
    {
        try
        {
            dal.setLastView(id);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Search">
    public void setSearchText(String text)
    {
        search.setSearchText(text);
    }

    public void setSearchYears(String years)
    {
        search.setSearchYears(years);
    }

    public void setSearchCategories(String categories)
    {
        search.setSearchCategories(categories);
    }

    public void setOrder(String order)
    {
        search.setOrder(order);
    }

    public void setSort(String sort)
    {
        search.setSort(sort);
    }

    public void setRating(String rating)
    {
        search.setRating(rating);
    }

    public List<Movie> prepareSearch() throws BLLException
    {
        return search.prepareSearch();
    }

    public void clearFilters() throws BLLException
    {
        search.clearFilters();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Categories">
    public ObservableList<String> allCategories() throws BLLException
    {
        return categories.allCategories();
    }

    public ObservableList<String> loadCategories() throws BLLException
    {
        return categories.loadCategories();
    }

    public void addChosenCategory(String category)
    {
        categories.addChosenCategory(category);
    }

    public void removeChosenCategory(String category)
    {
        categories.removeChosenCategory(category);
    }

    public void saveCategories() throws BLLException
    {
        categories.saveCategories();
    }

    public ObservableList<String> loadChosenMovieCategories(Movie movie)
    {
        return categories.loadChosenMovieCategories(movie);
    }

    public void addChosenMovieCategory(String category)
    {
        categories.addChosenMovieCategory(category);
    }

    public void removeChosenMovieCategory(String category)
    {
        categories.removeChosenMovieCategory(category);
    }

    public void saveMovieCategories() throws BLLException
    {
        categories.saveMovieCategories();
    }
    //</editor-fold>

}
