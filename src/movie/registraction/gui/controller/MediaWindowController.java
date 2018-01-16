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
import java.util.concurrent.TimeUnit;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import static javafx.util.Duration.millis;
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
    private boolean isPlaying;
    private boolean mediaPlayable;

    private String HourMinSecond;

    private MediaWindowModel wm;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private AnchorPane anchorMedia;
    @FXML
    private SplitPane splitPane;

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
        MediaSetup();
        
        MediaDoubleClick();

        splitPane.setDividerPosition(0, 0.95);

        anchorPane.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth)
            {
                splitPane.setDividerPosition(0, 0.95);
            }
        });

    }

    private void MediaSetup()
    {
        isPlaying = false;

        DoubleProperty mvw = mediaView.fitWidthProperty();
        DoubleProperty mvh = mediaView.fitHeightProperty();
        mvw.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        mvh.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(false); //disables the "perfect" stretch ratio

        String path = new File("src/movie/registraction/rsx/battlerite.mp4").getAbsolutePath();
        media = new Media(new File(path).toURI().toString());

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(false);
        mediaView.setMediaPlayer(mediaPlayer);

        double MovieLengthMillis = mediaPlayer.getTotalDuration().toMillis();

        HourMinSecond = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours((long) MovieLengthMillis),
                                      TimeUnit.MILLISECONDS.toMinutes((long) MovieLengthMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long) MovieLengthMillis)),
                                      TimeUnit.MILLISECONDS.toSeconds((long) MovieLengthMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) MovieLengthMillis)));

        System.out.println(HourMinSecond);

        mediaPlayer.setOnReady(() ->
        {
            mediaPlayable = true;
        });
    }

    @FXML
    private void moviePlayPause(ActionEvent event)
    {
        volumeSlider.setDisable(false);
        progressSlider.setDisable(false);
        lblTimer.setDisable(false);

        if (!isPlaying && mediaPlayable)
        {
            mediaPlayer.play();
            btnPlayPause.setText("Pause");
            isPlaying = !isPlaying;
        }
        else if (isPlaying && !mediaPlayable)
        {
            mediaPlayer.pause();
            btnPlayPause.setText("Play");
            isPlaying = !isPlaying;
        }
    }

    @FXML
    private void movieStop(ActionEvent event)
    {
        mediaPlayer.stop();
        resetControls();
    }

    private void resetControls()
    {
        progressSlider.setValue(0.0);
        btnPlayPause.setText("Play");
        lblTimer.setText("00:00:00 / " + HourMinSecond);
        System.out.println(HourMinSecond);
    }

    @FXML
    private void volumeMixer(MouseEvent event)
    {
    }

    @FXML
    private void movieMute(ActionEvent event)
    {

    }

    @FXML
    private void movieResize(ActionEvent event)
    {
        FullScreenCheck();
    }

    private void FullScreenCheck()
    {
        Stage stage = (Stage) anchorPane.getScene().getWindow();

        if (stage.isFullScreen())
        {
            stage.setFullScreen(false);
        }
        else if (!stage.isFullScreen())
        {
            stage.setFullScreen(true);
        }
    }

    private void MediaDoubleClick()
    {
        mediaView.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
                {
                    if (mouseEvent.getClickCount() == 2)
                    {
                        FullScreenCheck();
                    }
                }
            }
        });
    }
}
