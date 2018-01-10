/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.bll;

import java.io.File;
import java.util.List;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

/**
 *
 * @author Hussain
 */
public class MovieTilePane 
{
    public void setPictures(TilePane tilePane, List<File> fileList)
    {        
        tilePane.setHgap(20);
        tilePane.setPrefColumns(4);
        for(File files : fileList)
        {
            ImageView imageView = new ImageView(files.toURI().toString());
            imageView.setFitHeight(200);
            imageView.setFitWidth(150);
            tilePane.getChildren().add(imageView);
        }
    }
}
