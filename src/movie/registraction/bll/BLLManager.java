package movie.registraction.bll;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
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

    MovieTilePane mtPane;

    public BLLManager()
    {
        dal = new DALManager();

        omdb = new OmdbSearch();
        mtPane = new MovieTilePane();
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

    public void setPictures(TilePane tilePane, List<File> fileList)
    {
        mtPane.setPictures(tilePane, fileList);
    }

    /**
     * Gets the list of movies in the library
     *
     * Return the list of movies in the library as a String ArrayList
     *
     * @return
     *
     * @throws BLLException
     */
    public ArrayList<String> getMovieList() throws BLLException
    {
        try
        {
            ArrayList<Path> moviePaths = dal.getMovieList();
            ArrayList<String> movieStrings = new ArrayList();

            for (int i = 0; i < moviePaths.size(); i++)
                movieStrings.add(moviePaths.get(i).toString());

            return movieStrings;
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }
}
