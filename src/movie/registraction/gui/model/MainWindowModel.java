package movie.registraction.gui.model;

import com.jfoenix.controls.JFXCheckBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Axl
 */
public class MainWindowModel
{

    private ObservableList<JFXCheckBox> genres;
    private ObservableList<JFXCheckBox> years;
    private ObservableList<JFXCheckBox> others;

    public MainWindowModel()
    {
        genres = FXCollections.observableArrayList();
        years = FXCollections.observableArrayList();
        others = FXCollections.observableArrayList();

        for (int i = 0; i < 10; i++)
        {
            int j = i + 1;
            String s = "CheckBox" + j;

            JFXCheckBox cb = new JFXCheckBox(s);
            genres.add(cb);
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
        System.out.println(text);
    }

    public void fxmlFilterSearch()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fxmlCleatFilters()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gets the list of Genres
     *
     * @return
     */
    public ObservableList<JFXCheckBox> getGenreList()
    {
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
}
