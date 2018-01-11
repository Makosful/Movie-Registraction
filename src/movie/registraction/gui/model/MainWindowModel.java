package movie.registraction.gui.model;

import com.jfoenix.controls.JFXCheckBox;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.DirectoryChooser;
import movie.registraction.be.Movie;
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
    List<ImageView> imageViewList;
    ImageView imageView;

    private BLLManager bll;

    private final ObservableList<JFXCheckBox> genres;
    private final ObservableList<JFXCheckBox> years;
    private final ObservableList<JFXCheckBox> others;
    private final ObservableList<String> movies;
    private final ObservableList<String> allCategories;
    
    private final int IMAGE_HEIGHT;
    private final int IMAGE_WIDTH;
        
    private final ArrayList<String> extensionList;

    private changeCategories categories;
    private ContextMenu contextMenu;

    public MainWindowModel()
    {
        IMAGE_HEIGHT = 200;
        IMAGE_WIDTH = 150;

        try
        {
            bll = new BLLManager();
        }
        catch (BLLException ex)
        {
        }

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

        extensionList = new ArrayList();
//        extensionList.add(".jpg");
//        extensionList.add(".png");
        extensionList.add(".mp4");
        extensionList.add(".mpeg4");
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
     * @param movie
     *
     * @return Observable list of category strings
     */
    public ObservableList<String> loadChosenMovieCategories(Movie movie)
    {
        return categories.loadChosenMovieCategories(movie);
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
    public void fxmlSetLibrary()
    {
        DirectoryChooser dc = new DirectoryChooser();
        File dir = dc.showDialog(null);

        if (dir.exists())
            try
            {
                // Save this path to storage
                String path = dir.getAbsolutePath();
                bll.saveDirectory(path);
                this.loadMovieList();

            }
            catch (BLLException ex)
            {
                System.out.println("Could not save path");
                System.out.println(dir.getAbsolutePath());
            }
    }
        
    // Setting the tile setup.
    public void setPictures(TilePane tilePane, List<File> fileList)
    {
        imageViewList = new ArrayList();
        setupMenu(tilePane);
        tilePane.setHgap(20);
        tilePane.setPrefColumns(4);
        for(File files : fileList)
        {
            imageView = new ImageView(files.toURI().toString());
            
            imageView.setFitHeight(IMAGE_HEIGHT);
            imageView.setFitWidth(IMAGE_WIDTH);
            imageViewList.add(imageView);
            
            tilePane.getChildren().add(imageView);
        }
    }
    /**
     * Sets up the contextmenu with the choices user get.
     * @param tilePane 
     */
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
     /**
     *Closes the menu incase the context menu is open
     * or else the user clicks normally.
     * @param contextMenu 
     */
    public void closeMenuOrClick(ContextMenu contextMenu)
    {
        bll.closeMenuOrClick(contextMenu);
    }
     /**
     * Closes the contextmenu.
     * @param contextMenu 
     */
    public void closeMenu(ContextMenu contextMenu)
    {
        contextMenu.hide();
    }
        /**
     *Checks whether contextmenu is open or not, if yes, it closes.
     Incase user dobbleclicks several times, so it doesnt stack.
     * @param contextMenu 
     */
    public void contextMenuOpenOrNot(ContextMenu contextMenu)
    {
        bll.contextMenuOpenOrNot(contextMenu);
    }

    private void loadMovieList()
    {
        try
        {
            String lib = bll.loadDirectory("path.txt");

            if (lib.isEmpty())
            {
            }
            else
            {
                movies.setAll(bll.getMovieList(extensionList));
                System.out.println("Successfully added library");
                movies.forEach((movy) ->
                {
                    System.out.println(movy);
                });
            }
        }
        catch (BLLException ex)
        {
            System.out.println("Failed to load library path");
        }
    }

    public ObservableList<String> getMovieList()
    {
        return movies;
    }
    /*
    Returns list of the imageviews. // The images the user puts in.
    */
    public List<ImageView> GetImageViewList()
    {
     return imageViewList;   
    }
    /*
    Returns the contextmenu for the imageviews.
    */
    public ContextMenu getContextMenu()
    {
        return contextMenu;
    }

}
