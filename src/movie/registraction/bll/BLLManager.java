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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import movie.registraction.be.Movie;
import movie.registraction.dal.DALException;
import movie.registraction.dal.DALManager;

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
    public BLLManager() throws BLLException, DALException
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
     * @throws BLLException
     */
    public URL getOmdbTitleResult(String text) throws BLLException
    {
        return omdb.getOmdbTitleResult(text);
    }

    public String getSearchResult(URL searchLink) throws BLLException
    {
        try
        {
            // Opens up a connection for us to READ from a buffered reader in an
            // inputstream from our API link.
            URLConnection con = searchLink.openConnection();
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
     * @throws BLLException
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
     * @throws BLLException
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
     * Loads the saved library directory
     *
     * @param path
     *
     * @return
     *
     * @throws BLLException
     */
    public String loadDirectory(String path) throws BLLException
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
     *
     * Return the list of movies in the library as a String ArrayList
     *
     * @param filter
     *
     * @return
     *
     * @throws BLLException
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

    public void setDirectoryWatch()
    {
        dal.setDirectoryWatch();
    }

    /**
     * Sends metadata to dataaccess layer to insert a movie
     *
     * @param movieMetaData
     * @param filePath
     *
     * @throws movie.registraction.bll.BLLException
     */
    public void addMovie(String[] movieMetaData, String filePath) throws BLLException
    {
        try
        {
            dal.addMovie(movieMetaData, filePath);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

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

    public void setImageId(File files, ImageView imageView) throws BLLException
    {
        for (Movie movie : getAllMovies())
        {
            // Removing the dot and text after, so only the text is in the string.
            String fileName = files.getName().split("\\.")[0];

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
     * @param searchLink
     *
     * @return
     *
     * @throws BLLException
     */
    public String[] getSearchMetaData(URL searchLink) throws BLLException
    {
        String searchResult = this.getSearchResult(searchLink);

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
     * @param movieName
     *
     * @return
     *
     * @throws movie.registraction.bll.BLLException
     */
    public String getSpecificMovieImage(String movieName) throws BLLException
    {
        try
        {
            return dal.getSpecificMovieImage(movieName);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    public String splitDot(String stringToSplit)
    {
        return stringToSplit.split("\\.")[0];
    }

    /**
     * If there is a movie last seen over 2 years ago and the movie i rated
     * under 6
     * ask the user if the movie should be deleted
     *
     * @throws movie.registraction.bll.BLLException
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
     * Check if movie already exists in the db
     *
     * @param movieTitle
     *
     * @return
     *
     * @throws movie.registraction.bll.BLLException
     */
    public boolean movieAlreadyExisting(String movieTitle) throws BLLException
    {
        boolean isAlreadyInDataBase = false;

        for (Movie m : getAllMovies())
        {
            if (m.getMovieTitle().toLowerCase().equals(movieTitle.toLowerCase()))
            {
                isAlreadyInDataBase = true;
            }

        }
        return isAlreadyInDataBase;
    }

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
     * Gets the list holding the changed files
     *
     * @return
     */
    public ObservableList<Path> getChangeList()
    {
        return dal.getChangeList();
    }

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
    
    public void setLastView(int movieId) throws BLLException
    {
        try
        {
            dal.setLastView(movieId);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

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

}
