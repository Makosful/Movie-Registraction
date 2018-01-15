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
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
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

    OmdbSearch omdb;

    DALManager dal;

    public BLLManager() throws BLLException, DALException
    {
        try
        {
            dal = new DALManager();
            omdb = new OmdbSearch();
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
                res += inputLine + "\n";

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
            try
            {
                Desktop.getDesktop().open(file);
            }
            catch (IOException ex)
            {
                throw new BLLException();
            }
        else
            throw new UnsupportedOperationException("This feature is not supported on your platform");
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
     * Closes the menu incase the context menu is open
     * or else the user clicks normally.
     *
     * @param contextMenu
     */
    public void closeMenuOrClick(ContextMenu contextMenu)
    {
        if (!contextMenu.isShowing())
            System.out.println("You clicked on the picture.");
        else
            contextMenu.hide();
    }

    /**
     * Closes the contextmenu.
     *
     * @param contextMenu
     */
    public void closeMenu(ContextMenu contextMenu)
    {
        contextMenu.hide();
    }

    /**
     * Checks whether contextmenu is open or not, if yes, it closes.
     * Incase user dobbleclicks several times, so it doesnt stack.
     *
     * @param contextMenu
     */
    public void contextMenuOpenOrNot(ContextMenu contextMenu)
    {
        // So the contextMenu doesnt stack.
        if (contextMenu.isShowing())
        {
            closeMenu(contextMenu);
            System.out.println("closed menu");
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

    /**
     * Sends metadata to dataaccess layer to insert a movie
     * @param movieMetaData
     *
     * @throws DALException
     */
    public void addMovie(String[] movieMetaData) throws DALException
    {
        dal.addMovie(movieMetaData);
    }

    public ObservableList<Movie> getAllMovies() throws DALException
    {
        return dal.getAllMovies();
    }

    public void imageIdMovieId(File files, ImageView imageView) throws DALException
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

                //  Finding the ID that belongs to the movie.
                if (Integer.parseInt(imageView.getId()) == movie.getId()) 
                {
                    System.out.println("workeeeeeeeed");
                    System.out.println(movie.getFileImg());
                    System.out.println(movie.getPersonalRating());
                    System.out.println(movie.getYear());
                }
            }
        }
    }
    
    public Movie getMovieIdMatch(ImageView imageView) throws DALException
    {
        Movie movieMatch = null;
        
            for (Movie movie : getAllMovies()) 
            {
                if (Integer.parseInt(imageView.getId()) == movie.getId()) 
                {
                   return movieMatch = movie;
                }
            }
            return movieMatch;
    }

    
    /**
     * Formats the metadata from searchresult, so its ready to be put in the db
     * @param searchLink
     * @return
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
        
        for(int i = 0; i < meta.length; i++)
        {
           //get title
           if(meta[i].contains("Title:"))
           {
               meta[0] = meta[i];
           }
           //get year
           else if(meta[i].contains("Year:")){
               meta[1] = meta[i];
           }
           //remove "min" after number of minutes
           else if(meta[i].contains("Runtime:"))
           {
               meta[2] = meta[i].substring(0, meta[i].lastIndexOf(" "));
           }
           //get imdb rating
           else if(meta[i].contains("imdbRating:"))
           {
               meta[3] = meta[i];
           }
           //Poster
           else if(meta[i].contains("Poster:"))
           {
               meta[4] = meta[i];
           }
           //get all the categories by using substring with genre and director as start and end index
           else if(meta[i].contains("Genre:"))
           {
               meta[5] = searchResult.substring(searchResult.indexOf("Genre"), searchResult.indexOf("Director"));
               meta[5] = meta[i].replace(",", "");
           }
            
        
        }
        
        //new array to hold final result
        String[] metaData = new String[6];
        for(int i = 0; i < metaData.length; i++)
        {
            //remove metadata title until ":" appears and one index after
            metaData[i] = meta[i].substring(meta[i].lastIndexOf(":")+1);   
            
        }
        return metaData;
    }
    
    /**
     * This method is to get a imgPath from a specific movie. So that it can be
     * thrown into the tilepane.
     *
     * @param movieName
     * @return
     * @throws DALException
     */
    public String getSpecificMovieImage(String movieName) throws DALException
    {
        return dal.getSpecificMovieImage(movieName);
    }
    
    public String splitDot(String stringToSplit)
    {
        return stringToSplit = stringToSplit.split("\\.")[0];
    }
    
    public void findOldAndBadMovies() throws DALException
    {
        try {
            Date twoYearsBefore = new Date(System.currentTimeMillis() - (2 * 365 * 24 * 60 * 60 * 1000));
            for(Movie m : getAllMovies())
            {
                if(m.getLastView() != null){
                    if(m.getLastView().before(twoYearsBefore) && m.getPersonalRating() < 6)
                    {
                        System.out.println(m.getMovieTitle()); 
                    }
                }
                
            }
        } catch (DALException ex) {
            throw new DALException();
        }
    }
}
