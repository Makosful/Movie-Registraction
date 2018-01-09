/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.gui.view;

import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Hussain
 */
public class TestingController implements Initializable {

    @FXML
    private JFXTextField txtSearch;
    Stage stage;
    
    TilePane t = new TilePane();
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private StackPane stackPane;
    
    List<String> movies;
    
    private  int gap = 8;
    private  int p = 4;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // TODO
        
         movies = new ArrayList();
         movies.add("a");
         movies.add("a");
         movies.add("a");
         movies.add("a");
         movies.add("a");
         movies.add("a");
         movies.add("a");
        listen();
    }
    
    public void start(Stage primaryStage)
    {
        File file;
        anchorPane.getChildren().add(t);
        if(!txtSearch.getText().isEmpty())
        {
            for(int i = 0; i < movies.size();i++)
            {
            t.setHgap(-20);
            t.setPrefColumns(3);
                file = new File("C:\\Users\\Hussain\\Documents\\uiBBu.png");
                t.getChildren().add(new ImageView(file.toURI().toString()));
                System.out.println("testest"+i);
                System.out.println(t.getChildren().get(i));
                
            }
        }
    }
    
    private void listen()
    {
        txtSearch.textProperty().addListener((observable, oldValue, newValue)
                -> {
                start(stage);
                for (int i = 0; i < 10; i++) {
                System.out.println("test"+i);
            }
                
                });
    }
    
}
