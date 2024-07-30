package com.example.executablelauncher.utils;

import com.example.executablelauncher.App;
import com.example.executablelauncher.DataManager;
import com.example.executablelauncher.entities.Library;
import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static class SeriesComparator implements Comparator<Series> {
        @Override
        public int compare(Series s1, Series s2) {
            if (s1.getOrder() != 0 && s2.getOrder() != 0) {
                return Integer.compare(s1.getOrder(), s2.getOrder());
            }

            return s1.getName().compareTo(s2.getName());
        }
    }

    public static class SeasonComparator implements Comparator<Season> {
        @Override
        public int compare(Season s1, Season s2) {
            if (s1.getOrder() != 0 && s2.getOrder() != 0) {
                return Integer.compare(s1.getOrder(), s2.getOrder());
            }

            if (DataManager.INSTANCE.currentLibrary.getType().equals("Shows")){
                if (s1.getSeasonNumber() == 0 || s2.getSeasonNumber() == 0) {
                    //Check if there is a season with seasonNumber == 0, for it has to be at the end of the seasons list
                    if (s1.getSeasonNumber() == 0 && s2.getSeasonNumber() != 0) {
                        return 1;
                    } else if (s1.getSeasonNumber() != 0 && s2.getSeasonNumber() == 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                }else{
                    return Integer.compare(s1.getSeasonNumber(), s2.getSeasonNumber());
                }
            }

            //If the current library is for movies, then we compare the year of each season (movie)
            int year1 = Integer.parseInt(s1.getYear());
            int year2 = Integer.parseInt(s2.getYear());
            return Integer.compare(year1, year2);
        }
    }

    public static class EpisodeComparator implements Comparator<Episode> {
        @Override
        public int compare(Episode a, Episode b) {
            if (a.getOrder() != 0 && b.getOrder() != 0) {
                return Integer.compare(a.getOrder(), b.getOrder());
            }

            return Integer.compare(a.getEpisodeNumber(), b.getEpisodeNumber());
        }
    }

    public static class LibraryComparator implements Comparator<Library> {
        @Override
        public int compare(Library a, Library b) {
            return a.getName().compareTo(b.getName());
        }
    }

    public static String episodeDateFormat(String originalDate, String language) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag(language));

        try {
            Date date = originalFormat.parse(originalDate);
            return newFormat.format(date);
        } catch (ParseException e) {
            System.err.println("episodeDateFormat: Error applying date format");
            return originalDate;
        }
    }

    public static class LocaleStringConverter extends javafx.util.StringConverter<Locale> {
        @Override
        public String toString(Locale object) {
            if (object != null)
                return object.getDisplayName();

            return null;
        }

        @Override
        public Locale fromString(String string) {
            return Locale.forLanguageTag(string);
        }
    }

    //region EFFECTS
    public static ParallelTransition createParallelTransition(Node node1, Node node2, Node node3, Node node4, float seconds){
        FadeTransition mainImages = fadeOutEffect(node1, seconds, 0);
        FadeTransition mainPane = fadeOutEffect(node2, seconds, 0);

        //mainImages.setOnFinished(e -> node1.setVisible(false));
        //mainPane.setOnFinished(e -> node2.setVisible(false));

        ParallelTransition mainTransition = new ParallelTransition(mainImages, mainPane);
        mainTransition.setOnFinished(e -> {
            //node3.setVisible(true);
            //node4.setVisible(true);
            FadeTransition scrollTransition = fadeInEffect(node3, seconds, 0);
            FadeTransition backgroundTransition = fadeInEffect(node4, seconds, 0);

            ParallelTransition showLibraries = new ParallelTransition(scrollTransition, backgroundTransition);
            showLibraries.play();
        });

        return mainTransition;
    }
    public static FadeTransition fadeInEffect(Node node, float seconds, float fromValue){
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(seconds), node);
        fadeIn.setFromValue(fromValue);
        fadeIn.setToValue(1.0);

        return fadeIn;
    }
    public static FadeTransition fadeOutEffect(Node node, float seconds, float toValue){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(seconds), node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(toValue);

        return fadeOut;
    }
    public static void fadeOutEffect(ScrollPane pane){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.6), pane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        pane.setVisible(false);
    }
    public static void fadeOutEffect(ImageView img){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.6), img);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        img.setVisible(false);
    }
    public static void fadeInEffect(Pane pane){
        pane.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.6), pane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    public static void fadeInEffect(ScrollPane pane){
        pane.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.6), pane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    public static void fadeInEffect(ImageView img){
        img.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.6), img);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    public static void fadeInEffect(MediaView mv){
        mv.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), mv);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    public static void fadeOutEffect(Button btn){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), btn);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        btn.setVisible(false);
    }
    public static void fadeOutEffect(MediaView mv){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), mv);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        mv.setVisible(false);
    }
    public static void fadeInEffect(Button btn){
        btn.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), btn);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    public static String formatTime(int time){
        int h = (int) (time / 3600);
        int m = (int) ((time % 3600) / 60);
        int s = (int) (time % 60);

        if (h > 0)
            return String.format("%02d:%02d:%02d", h, m, s);

        return String.format("%02d:%02d", m, s);
    }
    public static void playInteractionSound() {
        File file = new File("resources/audio/interaction.wav");
        Media media = new Media(file.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(1);
        player.seek(player.getStartTime());
        player.play();
    }
    public static void playCategoriesSound() {
        File file = new File("resources/audio/categories.wav");
        Media media = new Media(file.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(1);
        player.seek(player.getStartTime());
        player.play();
    }
    public static int getVisibleButtonCountGlobal(ScrollPane continueWatchingScroll, HBox continueWatchingBox) {
        double scrollPaneWidth = continueWatchingScroll.getWidth();
        double scrollPaneX = continueWatchingScroll.getLayoutX();

        int visibleButtons = 0;

        for (Node button : continueWatchingBox.getChildren()) {
            Bounds buttonBounds = button.localToScene(button.getBoundsInLocal());
            double buttonX = buttonBounds.getMinX();

            if (buttonX >= scrollPaneX && buttonX + buttonBounds.getWidth() <= scrollPaneX + scrollPaneWidth) {
                visibleButtons++;
            }
        }

        return visibleButtons;
    }
    public static String setRuntime(int runtime){
        int h = runtime / 60;
        int m = runtime % 60;

        if (h == 0)
            return (m + "m");

        return (h + "h " + m + "m");
    }
    public static void setTimeLeft(HBox timeLeftBox, Label timeLeftField, Episode episode){
        if (episode.getTimeWatched() > 5){
            timeLeftBox.setVisible(true);
            int leftTime = (int) (episode.getRuntimeInSeconds() - episode.getTimeWatched());

            int hours = leftTime / 3600;
            int minutes = (leftTime % 3600) / 60;

            String timeLeft;
            if (App.globalLanguage != Locale.forLanguageTag("es-ES")) {
                if (hours > 0) {
                    timeLeft = hours + " " + App.textBundle.getString("hours") + " " + minutes + " " + App.textBundle.getString("minutes") + " " + App.textBundle.getString("timeLeft");
                } else {
                    timeLeft = minutes + " min left";
                }
            } else {
                if (hours > 0) {
                    timeLeft = App.textBundle.getString("timeLeft") + " " + hours + " " + App.textBundle.getString("hours") + " " + minutes + " " + App.textBundle.getString("minutes");
                } else {
                    timeLeft = "Quedan " + minutes + " minutos";
                }
            }

            timeLeftField.setText(timeLeft);
        }else{
            timeLeftBox.setVisible(false);
        }
    }
    public static void processCurrentlyWatching(Series series, Season season, Episode episode){
        List<Season> seasons = series.getSeasons();

        season.setCurrentlyWatchingEpisode(-1);
        series.setCurrentlyWatchingSeason(-1);

        if (episode.isWatched()){
            if (season.getEpisodes().indexOf(episode) == season.getEpisodes().size() - 1){
                if (seasons.indexOf(season) < seasons.size() - 1){
                    Season nextSeason = seasons.get(seasons.indexOf(season) + 1);

                    if (nextSeason != null){
                        nextSeason.setCurrentlyWatchingEpisode(0);
                        series.setCurrentlyWatchingSeason(seasons.indexOf(nextSeason));
                    }
                }
            }else{
                season.setCurrentlyWatchingEpisode(season.getEpisodes().indexOf(episode) + 1);
                series.setCurrentlyWatchingSeason(seasons.indexOf(season));
            }
        }else{
            season.setCurrentlyWatchingEpisode(season.getEpisodes().indexOf(episode));
            series.setCurrentlyWatchingSeason(seasons.indexOf(season));
        }
    }
    public static void smoothScrollToHvalue(ScrollPane scrollPane, double targetHvalue) {
        //Validate value
        if (targetHvalue < 0.0) targetHvalue = 0.0;
        if (targetHvalue > 1.0) targetHvalue = 1.0;

        //Create animation
        Timeline timeline = new Timeline();

        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(0.1),
                new KeyValue(scrollPane.hvalueProperty(), targetHvalue)
        );

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }
    public static MFXProgressSpinner getCircularProgress(int size){
        MFXProgressSpinner loadingIndicator = new MFXProgressSpinner();
        loadingIndicator.setPrefSize(size, size);
        loadingIndicator.setPadding(new Insets(0, 5, 0, 0));
        loadingIndicator.setColor1(Color.web("#8EDCE6"));
        loadingIndicator.setColor2(Color.web("#8EDCE6"));
        loadingIndicator.setColor3(Color.web("#8EDCE6"));
        loadingIndicator.setColor4(Color.web("#8EDCE6"));

        return loadingIndicator;
    }
    public static InputStream getFileAsIOStream(final String fileName)
    {
        InputStream ioStream = Utils.class
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }
    //endregion
}
