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
     * @return A String object containing the path to the library
     *
     * @throws DALException
     */
    public String loadDirectory() throws DALException
    {
        try (BufferedReader br = new BufferedReader(new FileReader("path.txt")))
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

    public ArrayList<String> getMovieList() throws DALException
    {
        ArrayList<String> list = new ArrayList();

        Path startPath = Paths.get(this.loadDirectory());

        try (Stream<Path> paths = Files.walk(startPath))
        {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(System.out::println);
        }
        catch (IOException ex)
        {
            throw new DALException();
        }

        return list;
    }
}
