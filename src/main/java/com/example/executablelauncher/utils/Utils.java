package com.example.executablelauncher.utils;

import com.example.executablelauncher.DataManager;
import com.example.executablelauncher.entities.Library;
import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
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
    public static void fadeOutEffect(Pane pane){
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
    //endregion
}
