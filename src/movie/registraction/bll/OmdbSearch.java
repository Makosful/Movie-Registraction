package movie.registraction.bll;

import java.net.MalformedURLException;
import java.net.URL;
import movie.registraction.bll.exception.BLLException;

/**
 *
 * @author Axl
 */
public class OmdbSearch
{

    /**
     * Creates a URL for OMDB
     * Takes the given text and uses it to create a URL for OMDB to make a
     * search for the title of a movie
     *
     * @param text The title of the movie to search for
     *
     * @return An URL for the OMDB website
     *
     * @throws BLLException Throws an exception if it fails to access the API
     */
    public URL getOmdbTitleResult(String text) throws BLLException
    {
        try
        {
            return new URL("http://www.omdbapi.com/?apikey=872a80a7&t=" + text);
        }
        catch (MalformedURLException ex)
        {
            throw new BLLException();
        }
    }
}
