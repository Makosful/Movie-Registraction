/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.gui.controller;

import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import movie.registraction.gui.model.MainWindowModel;

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
    private Duration duration;

    private boolean mediaMuted;
    private boolean isPlaying;
    private boolean mediaPlayable;

    private Image playImg;
    private Image pauseImg;
    private Image stopImg;
    private Image speakerOnImg;
    private Image speakerOffImg;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private AnchorPane anchorMedia;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Label lblVolume;
    private ImageView imageView;
    @FXML
    private JFXButton btnStop;
    @FXML
    private JFXButton btnMute;
    @FXML
    private JFXButton btnFullscreen;

    /**
     * Initializes the controller class.
     *
     * @param url TODO
     * @param rb  TODO
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        setupMediaControls();

        volumeSlider.setValue(100);

        volumeSlider.getParent().getParent().toFront();

        progressSliderSetup();

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

    public void MediaSetup(MainWindowModel mdl)
    {
        isPlaying = false;

        DoubleProperty mvw = mediaView.fitWidthProperty();
        DoubleProperty mvh = mediaView.fitHeightProperty();
        mvw.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        mvh.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(false); //disables the "perfect" stretch ratio

        String path = mdl.getMovieInfo(imageView).getFilePath();

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
            mediaPlayable = true;
        });

        mediaPlayer.setOnEndOfMedia(() ->
        {
            resetControls();
            mediaPlayer.pause();
        });
    }

    @FXML
    private void moviePlayPause(ActionEvent event)
    {
        if (!isPlaying && mediaPlayable)
        {
            mediaPlayer.play();
            btnPlayPause.setGraphic(new ImageView(pauseImg));
            isPlaying = !isPlaying;
        }
        else if (isPlaying)
        {
            mediaPlayer.pause();
            btnPlayPause.setGraphic(new ImageView(playImg));
            isPlaying = !isPlaying;
        }
    }

    @FXML
    private void movieStop(ActionEvent event)
    {
        mediaPlayer.pause();
        mediaPlayer.seek(mediaPlayer.getStartTime());
        resetControls();
    }

    public void movieTerminate()
    {
        mediaPlayer.stop();
    }

    private void resetControls()
    {
        lblTimer.setText(formatTime(mediaPlayer.getStartTime(), duration));
        System.out.println(duration);
        progressSlider.setValue(0.0);
        btnPlayPause.setText("Play");
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
            mediaPlayer.setMute(true);
            btnMute.setGraphic(new ImageView(speakerOffImg));
            volumeSlider.setOpacity(0.5);
            mediaMuted = !mediaMuted;
        }
        else if (mediaMuted)
        {
            mediaPlayer.setMute(false);
            btnMute.setGraphic(new ImageView(speakerOnImg));
            volumeSlider.setOpacity(1);

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

    void setImageView(ImageView imageView)
    {
        this.imageView = imageView;
    }

    private void setupMediaControls()
    {
        playImg = new Image(getClass().getResourceAsStream("/movie/registraction/rsx/play-button.png"));
        pauseImg = new Image(getClass().getResourceAsStream("/movie/registraction/rsx/pause-button.png"));
        stopImg = new Image(getClass().getResourceAsStream("/movie/registraction/rsx/stop-button.png"));
        speakerOnImg = new Image(getClass().getResourceAsStream("/movie/registraction/rsx/speaker-on.png"));
        speakerOffImg = new Image(getClass().getResourceAsStream("/movie/registraction/rsx/speaker-off.png"));

        btnPlayPause.setGraphic(new ImageView(playImg));
        btnPlayPause.setText("");

        btnStop.setGraphic(new ImageView(stopImg));
        btnStop.setText("");

        btnMute.setGraphic(new ImageView(speakerOnImg));
        btnMute.setText("");
    }
}
