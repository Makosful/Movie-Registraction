package movie.registraction.gui.model;

import com.jfoenix.controls.JFXCheckBox;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import movie.registraction.bll.BLLException;
import movie.registraction.bll.BLLManager;
import movie.registraction.bll.changeCategories;

/**
 *
 * @author Axl
 */
public class MainWindowModel
{

    private BLLManager bll;

    private final ObservableList<JFXCheckBox> genres;
    private final ObservableList<JFXCheckBox> years;
    private final ObservableList<JFXCheckBox> others;
    private changeCategories categories;

    public MainWindowModel()
    {
        bll = new BLLManager();

        genres = FXCollections.observableArrayList();
        years = FXCollections.observableArrayList();
        others = FXCollections.observableArrayList();
        try
        {
            categories = new changeCategories();
        }
        catch (BLLException ex)
        {
        }

        for (int i = 0; i < 10; i++)
        {
            int j = i + 1;
            String s = "CheckBox" + j;

            JFXCheckBox cb = new JFXCheckBox(s);
            //genres.add(cb);
        }

        for (int i = 0; i < 10; i++)
        {
            int y = 1990;
            int y2 = y + i;

            JFXCheckBox cb = new JFXCheckBox(String.valueOf(y2));
            years.add(cb);
        }
    }

    public void fxmlTitleSearch(String text)
    {
        // Replace all the whitespaces with plus signs to make it URL friendly
        text = text.replaceAll(" ", "+");

        // Uses the API url + our fixed search index to display us all the
        // metadata of the movie searched for - if possible.
        URL searchLink;
        try
        {
            searchLink = bll.getOmdbTitleResult(text);

            System.out.println(bll.getSearchResult(searchLink));
        }
        catch (BLLException ex)
        {
            System.out.println("Could not get search result");
        }

    }

    public void fxmlClearFilters()
    {
    }

    /**
     * Gets the list of Genres
     *
     * @return
     */
    public ObservableList<JFXCheckBox> getGenreList()
    {
        try
        {
            for (String category : categories.allCategories())
            {
                JFXCheckBox cb = new JFXCheckBox(category);
                genres.add(cb);
            }
        }
        catch (SQLException ex)
        {
            Logger.getLogger(MainWindowModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return genres;
    }

    public ObservableList<JFXCheckBox> getYearList()
    {
        return years;
    }

    public ObservableList<JFXCheckBox> getOtherList()
    {
        return others;
    }

    public ObservableList<String> getAllCategories() throws SQLException
    {
        return categories.allCategories();
    }

    public void addChosenCategory(String category)
    {
        categories.addChosenCategory(category);
    }

    public void removeChosenCategory(String category)
    {
        categories.removeChosenCategory(category);
    }


    public void saveCategories(){
        try {
            categories.saveCategories();
        } catch (SQLException ex) {
            Logger.getLogger(MainWindowModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ObservableList<String> loadChosenMovieCategories() {
        return categories.loadChosenMovieCategories();
    }

    public void addChosenMovieCategory(String category) {
        categories.addChosenMovieCategory(category);
    }

    public void removeChosenMovieCategory(String category) {
        categories.removeChosenMovieCategory(category);
    }

    public void saveMovieCategories() {
        try {
            categories.saveMovieCategories();
        } catch (SQLException ex) {
            Logger.getLogger(MainWindowModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    public void fxmlUploadFiles()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
            

}
