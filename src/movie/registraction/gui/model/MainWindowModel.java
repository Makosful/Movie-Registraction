package movie.registraction.gui.model;

import com.jfoenix.controls.JFXCheckBox;
import java.io.File;
import java.net.URL;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.DirectoryChooser;
import movie.registraction.bll.BLLException;
import movie.registraction.bll.BLLManager;
import movie.registraction.bll.changeCategories;
import movie.registraction.dal.DALException;

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
    private final ObservableList<String> movies;
    private final ObservableList<String> allCategories;
    private changeCategories categories;
    private ContextMenu contextMenu;

    public MainWindowModel()
    {
        bll = new BLLManager();

        genres = FXCollections.observableArrayList();
        years = FXCollections.observableArrayList();
        others = FXCollections.observableArrayList();
        movies = FXCollections.observableArrayList();
        allCategories = FXCollections.observableArrayList(); 

        try
        {
            categories = new changeCategories();
        }
        catch (BLLException ex)
        {
        }

        for (int i = 0; i < 10; i++)
        {
            int y = 1990;
            int y2 = y + i;

            JFXCheckBox cb = new JFXCheckBox(String.valueOf(y2));
            years.add(cb);
        }

        loadMovieList();
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
     * @return Observablelist of checkboxes
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
        catch (DALException ex)
        {
            System.out.println("Could not get the list of categories");
        }

        return genres;
    }

    /**
     *
     * @return
     */
    public ObservableList<JFXCheckBox> getYearList()
    {
        return years;
    }

    /**
     *
     * @return
     */
    public ObservableList<JFXCheckBox> getOtherList()
    {
        return others;
    }

    /**
     * Gets all categories from changeCategories class
     *
     * @return
     *
     */
    public ObservableList<String> getAllCategories() 
    {
        try
        {
            allCategories.addAll(categories.allCategories());
        }
        catch (DALException ex)
        {
            System.out.println("Could not get the list of categories");
        }
        return allCategories;
    }

    /**
     * Sends the category string to chosenCategories class to be added
     *
     * @param category
     */
    public void addChosenCategory(String category)
    {
        categories.addChosenCategory(category);
    }

    /**
     * Sends the category string to chosenCategories class to be removed
     *
     * @param category
     */
    public void removeChosenCategory(String category)
    {
        categories.removeChosenCategory(category);
    }

    /**
     * Save the category changes in changeCategories class
     */
    public void saveCategories()
    {
        try
        {
            categories.saveCategories();
        }
        catch (DALException ex)
        {
            System.out.println("Could not save the category changes");
        }

    }

    /**
     * Gets the allready exsisting categories for a specific movie
     *
     * @return Observable list of category strings
     */
    public ObservableList<String> loadChosenMovieCategories()
    {
        return categories.loadChosenMovieCategories();
    }

    /**
     * Sends the category string to chosenCategories class to be added for a
     * movie
     *
     * @param category
     */
    public void addChosenMovieCategory(String category)
    {
        categories.addChosenMovieCategory(category);
    }

    /**
     * Sends the category string to chosenCategories class to be removed for a
     * movie
     *
     * @param category
     */
    public void removeChosenMovieCategory(String category)
    {
        categories.removeChosenMovieCategory(category);
    }

    /**
     * Save the movie category changes in changeCategories class
     */
    public void saveMovieCategories()
    {
        try
        {
            categories.saveMovieCategories();
        }
        catch (DALException ex)
        {
            System.out.println("Could not save the movie categories");
        }
    }

    /**
     * Choses the library path
     *
     * Defaults to the Windows Videos library
     */
    public void fxmlUploadFiles()
    {
        DirectoryChooser dc = new DirectoryChooser();
        File dir = dc.showDialog(null);

        if (dir.exists())
            try
            {
                // Save this path to storage
                String path = dir.getAbsolutePath();
                bll.saveDirectory(path);
                updateMovieList();

            }
            catch (BLLException ex)
            {
                System.out.println("Could not save path");
                System.out.println(dir.getAbsolutePath());
            }
    }

    public void setPictures(TilePane tilePane, List<File> fileList)
    {
        setupMenu(tilePane);
        bll.setPictures(tilePane, fileList, contextMenu);
    }
    
    private void setupMenu(TilePane tilePane)
    {
        contextMenu = new ContextMenu();
        MenuItem test1 = new MenuItem("1");
        MenuItem test2 = new MenuItem("2");
        MenuItem test3 = new MenuItem("3");
        
        //<editor-fold defaultstate="collapsed" desc="setOnAction">
test1.setOnAction(new EventHandler<ActionEvent>()
{
    @Override
    public void handle(ActionEvent event)
    {
        System.out.println("1");
        bll.closeMenu(contextMenu);
    }
});

test2.setOnAction(new EventHandler<ActionEvent>()
{
    @Override
    public void handle(ActionEvent event)
    {
        System.out.println("2");
        bll.closeMenu(contextMenu);
    }
});

test3.setOnAction(new EventHandler<ActionEvent>()
{
    @Override
    public void handle(ActionEvent event)
    {
        System.out.println("3");
        bll.closeMenu(contextMenu);
    }
});
//</editor-fold>
                
        contextMenu.getItems().addAll(test1, test2, test3);
    }
    

    private void updateMovieList()
    {
        try
        {
            movies.setAll(bll.getMovieList());
            System.out.println("Successfully updated movie list");
        }
        catch (BLLException ex)
        {
            System.out.println("Failed to update movie list");
            ex.printStackTrace();
        }
    }

    private void loadMovieList()
    {
        try
        {
            String lib = bll.loadDirectory("path.txt");

            if (lib.isEmpty())
                return;
            else
            {
                movies.setAll(bll.getMovieList());
                System.out.println("Successfully added library");
            }
        }
        catch (BLLException ex)
        {
            System.out.println("Failed to load library path");
            //ex.printStackTrace();
        }
    }

    public ObservableList<String> getMovieList()
    {
        return movies;
    }

}
