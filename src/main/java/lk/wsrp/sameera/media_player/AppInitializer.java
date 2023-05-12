package lk.wsrp.sameera.media_player;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.script.Bindings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class AppInitializer extends Application {
    private MediaPlayer mediaPlayer;
    private boolean isLooped = false;
    private File audioFile;
    private SimpleBooleanProperty isMute = new SimpleBooleanProperty(false);


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        mediaPlayer(primaryStage);

    }

    private void mediaPlayer(Stage stage) {
        Image icnOpen = new Image(this.getClass().getResource("/icons/folder.png").toString());
        Image icnStop = new Image(this.getClass().getResource("/icons/stop.png").toString());
        Image icnPlay = new Image(this.getClass().getResource("/icons/play.png").toString());
        Image icnPause = new Image(this.getClass().getResource("/icons/pause.png").toString());
        Image icnLoop = new Image(this.getClass().getResource("/icons/loop2.png").toString());
        Image icnShuffle = new Image(this.getClass().getResource("/icons/shuffle.png").toString());
        Image icnVolume = new Image(this.getClass().getResource("/icons/high-volume.png").toString());
        Image icnMute = new Image(this.getClass().getResource("/icons/no-sound.png").toString());


        ImageView imgOpenIcon = new ImageView();
        Label lblSongName = new Label("Song Name");
        HBox hBox1 = new HBox(imgOpenIcon, lblSongName);
        hBox1.setSpacing(40);
        hBox1.setAlignment(Pos.CENTER);
        imgOpenIcon.setImage(icnOpen);
        imgOpenIcon.setFitWidth(30);
        imgOpenIcon.setFitHeight(30);
        imgOpenIcon.setCursor(Cursor.HAND);
        lblSongName.setFont(Font.font(16));
        lblSongName.setTextFill(Color.WHITE);

        ImageView imgStop = new ImageView();
        ImageView imgPlayPause = new ImageView();
        ImageView imgLoop = new ImageView();
        HBox hBox2 = new HBox(imgStop, imgPlayPause, imgLoop);
        hBox2.setSpacing(50);
        hBox2.setAlignment(Pos.CENTER);
        imgStop.setImage(icnStop);
        imgStop.setFitHeight(40);
        imgStop.setFitWidth(40);
        imgStop.setCursor(Cursor.HAND);
        imgPlayPause.setImage(icnPlay);
        imgPlayPause.setFitWidth(70);
        imgPlayPause.setFitHeight(70);
        imgPlayPause.setCursor(Cursor.HAND);
        imgLoop.setImage(icnShuffle);
        imgLoop.setFitHeight(40);
        imgLoop.setFitWidth(40);
        imgLoop.setCursor(Cursor.HAND);

        ImageView imgVolumeMute = new ImageView();
        Slider sldVolume = new Slider(0,1,0.5);

        HBox hBox3 = new HBox(imgVolumeMute, sldVolume);
        hBox3.setSpacing(20);
        sldVolume.setMinWidth(200);
        hBox3.setAlignment(Pos.CENTER);
        imgVolumeMute.setImage(icnVolume);
        imgVolumeMute.setFitWidth(30);
        imgVolumeMute.setFitHeight(30);
        imgVolumeMute.setCursor(Cursor.HAND);


        Slider sldPlayLine = new Slider();
        Label lblCurrentTime = new Label("00:00");
        lblCurrentTime.setTextFill(Color.LIGHTBLUE);
        lblCurrentTime.setFont(Font.font(14));
        Label lblTotalDuration = new Label("00:00");
        lblTotalDuration.setTextFill(Color.BLACK);
        lblTotalDuration.setFont(Font.font(14));


        sldVolume.setBackground(Background.fill(Color.LIGHTBLUE));
        HBox hBox4 = new HBox(lblCurrentTime,sldPlayLine,lblTotalDuration);
        hBox4.setSpacing(20);
        hBox4.setAlignment(Pos.CENTER);
        HBox.setHgrow(sldPlayLine, Priority.ALWAYS);

        VBox root = new VBox(hBox1, hBox2, hBox3, hBox4);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(30);
        root.setPadding(new Insets(10));
        root.setPrefSize(600, 300);
        Image backgroundImage = new Image(this.getClass().getResource("/icons/player-background.jpg").toString());
        BackgroundImage backgroundImagePlayer = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT
        );

        Background background = new Background(backgroundImagePlayer);
        root.setBackground(background);

        Scene scene = new Scene(root);


        stage.setHeight(300);
        stage.setWidth(600);
        stage.setScene(scene);
        stage.setTitle("JavaFX Audio Player");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();

        root.setOnDragOver(event->{
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        });

        root.setOnDragDropped(event->{
            event.setDropCompleted(true);
            File droppedFile = event.getDragboard().getFiles().get(0);
            if (droppedFile != null) {
                Media media = new Media(droppedFile.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) mediaPlayer.stop();
                mediaPlayer.play();
                imgPlayPause.setImage(icnPause);
            }
            functionalities(mediaPlayer, lblSongName, sldVolume, sldPlayLine, lblCurrentTime, lblTotalDuration);
        });

        for (Node image : new ImageView[]{imgOpenIcon, imgStop, imgPlayPause, imgVolumeMute, imgLoop}){
            image.setOnMouseEntered(event->{
                image.setOpacity(0.8);
                image.setScaleX(1.5);
                image.setScaleY(1.5);
            });
            image.setOnMouseExited(event->{
                image.setOpacity(1);
                image.setScaleX(1);
                image.setScaleY(1);
            });
            image.setOnMousePressed(event-> image.setEffect(new InnerShadow(10, Color.BLACK)));
        }

        imgOpenIcon.setOnMouseReleased(event -> {
            imgOpenIcon.setEffect(null);
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.mp4"));
            fileChooser.setTitle("Open an audio file");
            audioFile = fileChooser.showOpenDialog(imgOpenIcon.getScene().getWindow());

            if (audioFile != null){
                Media media = new Media(audioFile.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) mediaPlayer.stop();
                mediaPlayer.play();
                imgPlayPause.setImage(icnPause);

            }else{
                mediaPlayer = null;
            }
            functionalities(mediaPlayer, lblSongName, sldVolume, sldPlayLine, lblCurrentTime, lblTotalDuration);
        });

        imgStop.setOnMouseReleased(event -> {
            imgStop.setEffect(null);
            if (mediaPlayer != null) {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.stop();
                    imgPlayPause.setImage(icnPlay);
                }
            }
        });

        imgPlayPause.setOnMouseReleased(event -> {
            imgPlayPause.setEffect(null);

            if (mediaPlayer != null) {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    imgPlayPause.setImage(icnPlay);
                    mediaPlayer.pause();
                } else {
                    imgPlayPause.setImage(icnPause);
                    mediaPlayer.play();
                }
            }
        });

        imgLoop.setOnMouseReleased(event -> {
            imgLoop.setEffect(null);
            isLooped = !isLooped;
            if (mediaPlayer != null) {
                if (isLooped) {
                    mediaPlayer.setCycleCount(Animation.INDEFINITE);
                    isLooped = true;
                    imgLoop.setImage(icnLoop);
                } else {
                    mediaPlayer.setCycleCount(1);
                    isLooped = false;
                    imgLoop.setImage(icnShuffle);
                }
            }
        });

        imgVolumeMute.setOnMouseReleased(event -> {
            imgVolumeMute.setEffect(null);
            isMute.set(!isMute.get());
            if (isMute.get()) {
                imgVolumeMute.setImage(icnMute);
                sldVolume.setValue(0);
            } else {
                imgVolumeMute.setImage(icnVolume);
                sldVolume.setValue(0.5);
            }
        });

        sldVolume.valueProperty().addListener((ov,previous,current)->{
            if (sldVolume.getValue() == 0) {
                imgVolumeMute.setImage(icnMute);
            } else {
                imgVolumeMute.setImage(icnVolume);
            }
        });

    }

    private void functionalities(MediaPlayer mediaPlayer, Label lblSongName, Slider sldVolume,
                                 Slider sldPlayLine, Label lblCurrentTime, Label lblTotalDuration) {
        mediaPlayer.volumeProperty().bind(sldVolume.valueProperty());
        mediaPlayer.muteProperty().bind(isMute);

        sldPlayLine.setMin(0);
        mediaPlayer.setOnReady(() -> {
            Duration totalDuration = mediaPlayer.getMedia().getDuration();
            sldPlayLine.setMax(totalDuration.toSeconds());
        });

        mediaPlayer.currentTimeProperty().addListener((ov, previous, current) -> {
            if (!sldPlayLine.isValueChanging()) {
                sldPlayLine.setValue(current.toSeconds());
            }
        });

        sldPlayLine.valueChangingProperty().addListener((ov, previous, current) -> {
            if (current) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.seek(Duration.seconds(sldPlayLine.getValue()));
                mediaPlayer.play();
            }
        });

        mediaPlayer.totalDurationProperty().addListener((ov, previous, current) -> {
            if (current != null) {
                int minutes = (int) current.toMinutes();
                int seconds = (int) (current.toSeconds()) - (minutes*60);
                String formattedTime = String.format("%02d:%02d", minutes, seconds);
                lblTotalDuration.setText(formattedTime);
            }
        });

        mediaPlayer.currentTimeProperty().addListener((ov, previous, current) -> {
            if (current != null) {
                int minutes = (int) current.toMinutes();
                int seconds = (int) (current.toSeconds()) - (minutes*60);
                String formattedTime = String.format("%02d:%02d", minutes, seconds);
                lblCurrentTime.setText(formattedTime);
                if (lblCurrentTime.getText().equals(lblTotalDuration.getText())) {
                    lblCurrentTime.setText("00:00");
                }
            }
        });

        lblSongName.setText(audioFile.getAbsolutePath().toString());
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.3), e -> {
                    String fullPath = lblSongName.getText();
                    String firstChar = fullPath.substring(0, 1);
                    String restChars = fullPath.substring(1) + firstChar;
                    lblSongName.setText(restChars);
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
