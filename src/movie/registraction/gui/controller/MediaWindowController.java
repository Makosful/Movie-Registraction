/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
import javafx.util.Duration;
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
    private Slider volumeSlider;
    @FXML
    private Slider progressSlider;
    @FXML
    private Label lblTimer;

    private Media media;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private boolean mediaPlayable;
    private boolean mediaMuted;
    private double MovieLengthMillis;
    private Duration duration;

    private String movieLength;

    private MediaWindowModel wm;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private AnchorPane anchorMedia;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Label lblVolume;

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
        lblVolume.setDisable(true);

        volumeSlider.getParent().getParent().toFront();

        progressSliderSetup();

        MediaSetup();

        MediaDoubleClick();

        splitPane.setDividerPosition(0, 0.95);

        anchorPane.heightProperty().addListener(new ChangeListener<Number>()
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
        mediaPlayer.setVolume(1);

        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.setOnReady(() ->
        {
            duration = mediaPlayer.getMedia().getDuration();

            mediaPlayer.currentTimeProperty().addListener((Observable ov) ->
            {
                progressSliderUpdater();
            });

//            MovieLengthMillis = mediaPlayer.getTotalDuration().toMillis();
//
//            System.out.println("Length of movie (before format): " + MovieLengthMillis);
//
//            movieLength = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours((long) MovieLengthMillis),
//                                        TimeUnit.MILLISECONDS.toMinutes((long) MovieLengthMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long) MovieLengthMillis)),
//                                        TimeUnit.MILLISECONDS.toSeconds((long) MovieLengthMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) MovieLengthMillis)));
//
//            System.out.println("Length of movie: " + movieLength);
            mediaPlayable = true;
        });

        mediaPlayer.setOnEndOfMedia(() ->
        {
            mediaPlayer.stop();
            resetControls();
        });
    }

    @FXML
    private void moviePlayPause(ActionEvent event)
    {
        volumeSlider.setDisable(false);
        progressSlider.setDisable(false);
        lblTimer.setDisable(false);
        lblVolume.setDisable(false);

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
        lblTimer.setText("00:00:00 / " + movieLength);
        System.out.println(movieLength);
    }

    @FXML
    private void volumeDrag(MouseEvent event)
    {
        volumeSlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) ->
        {
            mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            volumeSlider.setValue(mediaPlayer.getVolume() * 100);
            System.out.println(volumeSlider.getValue());
            lblVolume.setText(String.format("%.0f", new_val) + "%");
        });
    }

    @FXML
    private void movieMute(ActionEvent event)
    {
        if (!mediaMuted)
        {
            mediaPlayer.setVolume(0);
            volumeSlider.setDisable(true);
            mediaMuted = !mediaMuted;
        }
        else if (mediaMuted)
        {
            mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            volumeSlider.setDisable(false);
            mediaMuted = !mediaMuted;
        }
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

    private void progressSliderSetup()
    {
        progressSlider.valueProperty().addListener((Observable ov) ->
        {
            if (progressSlider.isValueChanging())
            {
                mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(progressSlider.getValue() / 100));
            }
        });
    }

    private void progressSliderUpdater()
    {
        if (lblTimer != null && progressSlider != null && volumeSlider != null)
        {
            Platform.runLater(() ->
            {
                Duration currentTime = mediaPlayer.getCurrentTime();
                lblTimer.setText(formatTime(currentTime, duration));
                progressSlider.setDisable(duration.isUnknown());
                if (!progressSlider.isValueChanging() && !progressSlider.isDisabled() && duration.greaterThan(Duration.ZERO))
                {
                    progressSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                }
                if (!volumeSlider.isValueChanging())
                {
                    volumeSlider.setValue((int) Math.round(mediaPlayer.getVolume() * 100));
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration)
    {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0)
        {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                             - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO))
        {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0)
            {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                                  - durationMinutes * 60;
            if (durationHours > 0)
            {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                                     elapsedHours, elapsedMinutes, elapsedSeconds,
                                     durationHours, durationMinutes, durationSeconds);
            }
            else
            {
                return String.format("%02d:%02d/%02d:%02d",
                                     elapsedMinutes, elapsedSeconds, durationMinutes,
                                     durationSeconds);
            }
        }
        else
        {
            if (elapsedHours > 0)
            {
                return String.format("%d:%02d:%02d", elapsedHours,
                                     elapsedMinutes, elapsedSeconds);
            }
            else
            {
                return String.format("%02d:%02d", elapsedMinutes,
                                     elapsedSeconds);
            }
        }
    }
}
