/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.bll;

import java.io.File;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;

/**
 *
 * @author Hussain
 */
public class MovieTilePane 
{
    private ContextMenu contextMenu;
    // Image to be viewed.
    private ImageView imageView;
    // Finals for the image's height and width.
    private final int IMAGE_HEIGHT;
    private final int IMAGE_WIDTH;
    
    
    public MovieTilePane()
    {
        IMAGE_HEIGHT = 200;
        IMAGE_WIDTH = 150;
    }
    
    // Setting the tile setup.
    public void setPictures(TilePane tilePane, List<File> fileList, ContextMenu contextMenu)
    {        
        tilePane.setHgap(20);
        tilePane.setPrefColumns(4);
        for(File files : fileList)
        {
            imageView = new ImageView(files.toURI().toString());
            imageClick(tilePane, contextMenu);
            
            imageView.setFitHeight(IMAGE_HEIGHT);
            imageView.setFitWidth(IMAGE_WIDTH);
            
            tilePane.getChildren().add(imageView);
        }
    }
    /*
    Code so you can click or right click on an image and soemthing happens.
    Mouse event.
    */  
    private void imageClick(TilePane tilePane, ContextMenu contextMenu)
    {
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() 
        {
            @Override
            public void handle(MouseEvent event) 
            {
                MouseButton mouseButton = event.getButton();
                if (mouseButton == MouseButton.PRIMARY) 
                {
                    if(!contextMenu.isShowing())
                    {
                    System.out.println(imageView.toString());
                    }
                    else
                    {
                    closeMenu(contextMenu);
                    }
                }

                if (mouseButton == MouseButton.SECONDARY) 
                {
                    // So the contextMenu doesnt stack.
                    if(contextMenu != null)
                    {
                        closeMenu(contextMenu);
                    }
                    System.out.println("rightclick");
                    contextMenu.show(tilePane, event.getScreenX(), event.getScreenY());
                }
            }

        });
    }
    // Closing menu.
    public void closeMenu(ContextMenu contextMenu)
    {
        contextMenu.hide();
    }
}
