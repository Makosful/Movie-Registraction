/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.gui.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import movie.registraction.gui.model.MediaWindowModel;

/**
 * FXML Controller class
 *
 * @author Storm
 */
public class MediaWindowController implements Initializable
{
    @FXML
    private MediaView mediaView;
    @FXML
    private FlowPane playbackPanel;
    @FXML
    private JFXButton btnPlayPause;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private Slider progressSlider;
    @FXML
    private Label lblTimer;

    private Media media;
    private MediaPlayer mediaPlayer;

    private MediaWindowModel wm;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        wm = new MediaWindowModel();

        volumeSlider.setValue(100);
        volumeSlider.setDisable(true);
        progressSlider.setDisable(true);
        lblTimer.setDisable(true);

        volumeSlider.getParent().getParent().toFront();

        //dragListener();
    }

    private void MediaSetup()
    {
        DoubleProperty width = mediaView.fitWidthProperty();
        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));

        DoubleProperty height = mediaView.fitWidthProperty();
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

        String path = new File("registraction/rsx/battlerite.mp4").getAbsolutePath();
        media = new Media(new File(path).toURI().toString());

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaView.setMediaPlayer(mediaPlayer);
    }

    @FXML
    private void moviePlayPause(ActionEvent event)
    {
        mediaPlayer.play();
    }

    @FXML
    private void movieStop(ActionEvent event)
    {
    }

    @FXML
    private void volumeMixer(MouseEvent event)
    {
    }

    @FXML
    private void movieMute(ActionEvent event)
    {
    }

}
