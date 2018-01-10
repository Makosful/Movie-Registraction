package movie.registraction.dal;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 *
 * @author Axl
 */
public class DALManager
{

    public DALManager()
    {
    }

    /**
     * Saves the path of the movie library
     *
     * Stores the path of the folder where the movie collection is saved as a
     * String
     *
     * @param path The String containing the path to the library
     *
     * @throws DALException
     */
    public void saveDirectory(String path) throws DALException
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("path.txt")))
        {
            bw.write(path);
            bw.newLine();
        }
        catch (IOException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Loads the saved library
     *
     * @param path
     *
     * @return A String object containing the path to the library
     *
     * @throws DALException
     */
    public String loadDirectory(String path) throws DALException
    {
        try (BufferedReader br = new BufferedReader(new FileReader(path)))
        {
            String s = br.readLine();

            return s;
        }
        catch (FileNotFoundException ex)
        {
            throw new DALException();
        }
        catch (IOException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Gets the list of Paths for all items in the library
     *
     * This method will look through the specified library folder and retrive
     * all items with the ending .jpg and .png
     *
     * @return An ArrayList containing paths
     *
     * @throws DALException
     */
    public ArrayList<Path> getMovieList() throws DALException
    {
        ArrayList<Path> list = new ArrayList();

        Path startPath = Paths.get(this.loadDirectory("path.txt"));

        try (Stream<Path> paths = Files.walk(startPath))
        {
            paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".jpg")
                                 || p.toString().endsWith(".png"))
                    .forEach(list::add);
        }
        catch (IOException ex)
        {
            throw new DALException();
        }

        return list;
    }
}
