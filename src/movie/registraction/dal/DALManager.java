package movie.registraction.dal;

import java.io.*;

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
     * @throws DALExceptions
     */
    public void saveDirectory(String path) throws DALExceptions
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("path.txt")))
        {
            bw.write(path);
            bw.newLine();
        }
        catch (IOException ex)
        {
            throw new DALExceptions();
        }
    }

    public String loadDirectory() throws DALExceptions
    {
        try (BufferedReader br = new BufferedReader(new FileReader("path.txt")))
        {
            String s = br.readLine();

            return s;
        }
        catch (FileNotFoundException ex)
        {
            throw new DALExceptions();
        }
        catch (IOException ex)
        {
            throw new DALExceptions();
        }
    }
}
