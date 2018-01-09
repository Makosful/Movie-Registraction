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
    public void setPictures(AnchorPane anchorPane, TilePane tilePane, List<File> fileList)
    {
        tilePane.setHgap(0);
        tilePane.setPrefColumns(3);
        anchorPane.getChildren().add(tilePane);
        for(File files : fileList)
        {
            tilePane.getChildren().add(new ImageView(files.toURI().toString()));
        }
    }
}
