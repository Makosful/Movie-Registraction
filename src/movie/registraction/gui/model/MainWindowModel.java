package movie.registraction.gui.model;

import com.jfoenix.controls.JFXCheckBox;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import movie.registraction.bll.changeCategories;

/**
 *
 * @author Axl
 */
public class MainWindowModel {

    private ObservableList<JFXCheckBox> genres;
    private ObservableList<JFXCheckBox> years;
    private ObservableList<JFXCheckBox> others;
    private changeCategories categories;

    public MainWindowModel() throws IOException {
        genres = FXCollections.observableArrayList();
        years = FXCollections.observableArrayList();
        others = FXCollections.observableArrayList();
        categories = new changeCategories();
        
        for (int i = 0; i < 10; i++) {
            int j = i + 1;
            String s = "CheckBox" + j;

            JFXCheckBox cb = new JFXCheckBox(s);
            //genres.add(cb);
        }

        for (int i = 0; i < 10; i++) {
            int y = 1990;
            int y2 = y + i;

            JFXCheckBox cb = new JFXCheckBox(String.valueOf(y2));
            years.add(cb);
        }
    }

    public void fxmlTitleSearch(String text) {
        System.out.println(text);
    }

    public void fxmlFilterSearch() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fxmlCleatFilters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gets the list of Genres
     *
     * @return
     */
    public ObservableList<JFXCheckBox> getGenreList() {
        try {
            for(String category :  categories.allCategories())
            {
                JFXCheckBox cb = new JFXCheckBox(category);
                genres.add(cb);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainWindowModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       return genres;
    }

    public ObservableList<JFXCheckBox> getYearList() {
        return years;
    }

    public ObservableList<JFXCheckBox> getOtherList() {
        return others;
    }

    public ObservableList<String> getAllCategories() throws SQLException
    {
        return categories.allCategories();
    }

    public void addChosenCategory(String category) {
        categories.addChosenCategory(category);
    }

    public void removeChosenCategory(String category) {
        categories.removeChosenCategory(category);
    }

    public void saveCategories() throws SQLException {
        categories.saveCategories();
    }
    
}
