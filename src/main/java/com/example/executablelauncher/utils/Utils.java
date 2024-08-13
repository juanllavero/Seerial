package com.example.executablelauncher.utils;

import com.example.executablelauncher.App;
import com.example.executablelauncher.DataManager;
import com.example.executablelauncher.entities.*;
import com.example.executablelauncher.fileMetadata.AudioTrack;
import com.example.executablelauncher.fileMetadata.MediaInfo;
import com.example.executablelauncher.fileMetadata.SubtitleTrack;
import com.example.executablelauncher.fileMetadata.VideoTrack;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Format;
import com.github.kokorin.jaffree.ffprobe.Stream;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static class NameYearContainer {
        public String name;
        public String year;

        public NameYearContainer(String name, String year){
            this.name = name;
            this.year = year;
        }
    }
    public static class SeriesComparator implements Comparator<Series> {
        @Override
        public int compare(Series s1, Series s2) {
            return Integer.compare(s1.getOrder(), s2.getOrder());
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
            int year1 = 0;
            int year2 = 0;

            if (!s1.getYear().isEmpty())
                year1 = Integer.parseInt(s1.getYear());

            if (!s2.getYear().isEmpty())
                year2 = Integer.parseInt(s2.getYear());

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
            return Integer.compare(a.getOrder(), b.getOrder());
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
    public static WritableImage getCroppedImage(ImageView img){
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double targetAspectRatio = screenWidth / screenHeight;

        double originalWidth = img.getImage().getWidth();
        double originalHeight = img.getImage().getHeight();
        double originalAspectRatio = originalWidth / originalHeight;

        double newWidth, newHeight;
        if (originalAspectRatio > targetAspectRatio) {
            newWidth = originalHeight * targetAspectRatio;
            newHeight = originalHeight;
        } else {
            newWidth = originalWidth;
            newHeight = originalWidth / targetAspectRatio;
        }

        double xOffset = 0;
        double yOffset = 0;

        PixelReader pixelReader = img.getImage().getPixelReader();

        if (newWidth == 0 || newHeight == 0)
            return null;

        return new WritableImage(pixelReader, 0, 0, (int) newWidth, (int) newHeight);
    }

    public static void markPreviousAndNextEpisodes(Series selectedSeries, Season selectedSeason, Episode selectedEpisode){
        if (DataManager.INSTANCE.currentLibrary.getType().equals("Shows")){
            //Mark as watched every episode before the current one
            for (Season season : selectedSeries.getSeasons()){
                if (season == selectedSeason){
                    for (Episode episode : season.getEpisodes()){
                        if (episode == selectedEpisode)
                            break;

                        episode.setWatched();
                    }
                    break;
                }

                if (season.getSeasonNumber() != 0)
                    for (Episode episode : season.getEpisodes())
                        episode.setWatched();
            }

            //Mark as unwatched every episode after the current one
            for (int i = selectedSeason.getEpisodes().indexOf(selectedEpisode); i < selectedSeason.getEpisodes().size(); i++){
                Episode episode = selectedSeason.getEpisodes().get(i);

                if (episode != selectedEpisode)
                    episode.setUnWatched();
            }
            for (int i = selectedSeries.getSeasons().indexOf(selectedSeason); i < selectedSeries.getSeasons().size(); i++){
                Season season = selectedSeries.getSeasons().get(i);

                if (season.getSeasonNumber() != 0 && season != selectedSeason){
                    for (Episode episode : season.getEpisodes())
                        episode.setUnWatched();
                }
            }
        }

        //Set currently watching episode
        int currentSeason = selectedSeries.getSeasons().indexOf(selectedSeason);
        if (selectedEpisode.isWatched()){
            if (selectedSeason.getEpisodes().indexOf(selectedEpisode) < selectedSeason.getEpisodes().size() - 1){
                selectedSeason.setCurrentlyWatchingEpisode(selectedSeason.getEpisodes().indexOf(selectedEpisode) + 1);
            }else if (currentSeason + 1 < selectedSeries.getSeasons().size() - 1){
                selectedSeason.setCurrentlyWatchingEpisode(-1);
                selectedSeries.setCurrentlyWatchingSeason(currentSeason + 1);
                selectedSeries.getSeasons().get(currentSeason + 1).setCurrentlyWatchingEpisode(0);
            }else{
                selectedSeries.setCurrentlyWatchingSeason(-1);
                selectedSeason.setCurrentlyWatchingEpisode(-1);
            }
        }else{
            selectedSeries.setCurrentlyWatchingSeason(currentSeason);
            selectedSeason.setCurrentlyWatchingEpisode(selectedSeason.getEpisodes().indexOf(selectedEpisode));
        }
    }

    /**
     * Takes an image and crops it to match the aspect ratio given as parameter.
     * @param originalImage Original image as a BufferedImage
     * @param targetWidth Width of the target aspect ratio
     * @param targetHeight Height of the target aspect ratio
     * @return
     */
    public static BufferedImage cropToAspectRatio(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double targetAspectRatio = (double) targetWidth / targetHeight;
        double originalAspectRatio = (double) originalWidth / originalHeight;

        int newWidth = originalWidth;
        int newHeight = originalHeight;

        if (originalAspectRatio > targetAspectRatio) {
            newWidth = (int) (originalHeight * targetAspectRatio);
        } else {
            newHeight = (int) (originalWidth / targetAspectRatio);
        }

        int x = (originalWidth - newWidth) / 2;
        int y = (originalHeight - newHeight) / 2;

        return originalImage.getSubimage(x, y, newWidth, newHeight);
    }

    //region EFFECTS
    public static Rectangle setRoundedBorders(String imageSrc, double width, double height, double roundValue){
        Rectangle rectangle = new Rectangle(0, 0, width, height);
        rectangle.setArcWidth(roundValue);
        rectangle.setArcHeight(roundValue);

        File imgFile = new File(imageSrc);

        if (!imgFile.exists())
            return null;

        ImagePattern pattern = new ImagePattern(
                new Image("file:" + imageSrc, width, height, false, false)
        );

        rectangle.setFill(pattern);

        return rectangle;
    }
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
    public static FadeTransition fadeInEffect(Node node, float seconds){
        node.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(seconds), node);
        fadeIn.setFromValue(0);
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
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), img);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        img.setVisible(false);
    }

    public static void fadeOutEffect(Node node, float delay){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(delay), node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        node.setVisible(false);
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
        int h = time / 3600;
        int m = (time % 3600) / 60;
        int s = time % 60;

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
    public static boolean compareImages(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
    public static void hideNode(Node node){
        node.setVisible(false);
        node.setManaged(false);
    }
    public static void showNode(Node node){
        node.setVisible(true);
        node.setManaged(true);
    }
    public static String getFolderSrc(String src) {
        String[] parts = src.split("[/\\\\]");

        String lastPart = parts[parts.length - 1];

        if (lastPart.contains("."))
            return src.substring(0, src.lastIndexOf(lastPart));

        return src;
    }
    //endregion

    //region MEDIA INFO EXTRACTION
    public static void getMediaInfo(Episode episode) {
        File video = new File(episode.getVideoSrc());

        if (!video.exists())
            return;

        try {
            FFprobeResult result = FFprobe.atPath()
                    .setShowStreams(true)
                    .setShowFormat(true)
                    .setShowChapters(true)
                    .setInput(episode.getVideoSrc())
                    .execute();

            Format format = result.getFormat();

            //Media Info
            MediaInfo mediaInfo = new MediaInfo(
                    video.getName(),
                    video.getAbsolutePath(),
                    format.getBitRate() / Math.pow(10, 3),
                    format.getDuration(),
                    format.getSize(),
                    video.getName().substring(video.getName().lastIndexOf(".") + 1).toUpperCase()
            );

            episode.setMediaInfo(mediaInfo);

            //Set real runtime
            if (format.getDuration() != null)
                episode.setRuntimeInSeconds(format.getDuration());

            //Remove previous tracks
            episode.getVideoTracks().clear();
            episode.getAudioTracks().clear();
            episode.getSubtitleTracks().clear();

            //Boolean to stop the loop after the last subtitle track
            boolean subtitleSectionStarted = false;

            //Tracks
            List<Stream> streams = result.getStreams();
            for (Stream stream : streams) {
                if (stream.getCodecType().equals(StreamType.SUBTITLE))
                    subtitleSectionStarted = true;
                else if (subtitleSectionStarted)
                    break;

                if (stream.getCodecType().equals(StreamType.VIDEO))
                    processVideoData(stream, episode);
                else if (stream.getCodecType().equals(StreamType.AUDIO))
                    processAudioData(stream, episode);
                else if (stream.getCodecType().equals(StreamType.SUBTITLE))
                    processSubtitleData(stream, episode);
            }

            //Chapters
            List<com.github.kokorin.jaffree.ffprobe.Chapter> chapters = result.getChapters();
            episode.getChapters().clear();
            if (chapters != null && chapters.size() > 1) {
                //DataManager.INSTANCE.createFolder("resources/img/chaptersCovers/" + episode.getId() + "/");
                for (com.github.kokorin.jaffree.ffprobe.Chapter chapter : chapters) {
                    double milliseconds = chapter.getStart() / 1_000_000.0;

                    Chapter newChapter = new Chapter(chapter.getTag("title"), milliseconds);
                    episode.addChapter(newChapter);
                }
            }
        } catch (Exception e) {
            System.err.println("getMediaInfo: could not get local metadata from file " + video.getAbsolutePath());
        }
    }

    private static void processVideoData(Stream stream, Episode episode) {
        String resolution = "", hdr = "", codec = "", profile = "";
        VideoTrack videoTrack = new VideoTrack(stream.getIndex());

        if (stream.getCodecName() != null) {
            codec = stream.getCodecName().toUpperCase();
            videoTrack.setCodec(stream.getCodecName().toUpperCase());
        }

        if (stream.getCodecLongName() != null)
            videoTrack.setCodecExt(stream.getCodecLongName());

        if (stream.getTag("BPS-eng") != null)
            videoTrack.setBitrate(Math.round(Double.parseDouble(stream.getTag("BPS-eng")) / Math.pow(10, 3)));

        if (stream.getAvgFrameRate() != null)
            videoTrack.setFramerate(stream.getAvgFrameRate().floatValue());

        if (stream.getCodedWidth() != null && stream.getCodedHeight() != null) {
            resolution = formatResolution(stream.getCodedWidth(), stream.getCodedHeight());
            videoTrack.setCodedHeight(stream.getCodedHeight());
            videoTrack.setCodedWidth(stream.getCodedWidth());
        }

        if (stream.getChromaLocation() != null)
            videoTrack.setChromaLocation(stream.getChromaLocation());

        if (stream.getColorSpace() != null) {
            if (stream.getColorSpace().equals("bt2020nc"))
                hdr = " HDR10";
            videoTrack.setColorSpace(stream.getColorSpace());
        }

        if (stream.getDisplayAspectRatio() != null)
            videoTrack.setAspectRatio(stream.getDisplayAspectRatio().floatValue());

        if (stream.getProfile() != null) {
            profile = stream.getProfile();
            videoTrack.setProfile(stream.getProfile());
        }

        if (stream.getRefs() != null)
            videoTrack.setRefFrames(stream.getRefs());

        if (stream.getColorRange() != null)
            videoTrack.setColorRange(stream.getColorRange());

        videoTrack.setDisplayTitle(resolution + hdr + " (" + codec + " " + profile + ")");
        episode.getVideoTracks().add(videoTrack);
    }

    private static void processAudioData(Stream stream, Episode episode) {
        String channels = "", language = "", codecDisplayName;
        AudioTrack audioTrack = new AudioTrack(stream.getIndex());

        if (stream.getCodecName() != null)
            audioTrack.setCodec(stream.getCodecName().toUpperCase());

        if (stream.getCodecLongName() != null)
            audioTrack.setCodecExt(stream.getCodecLongName());

        if (stream.getChannels() != null) {
            channels = formatAudioChannels(stream.getChannels());
            audioTrack.setChannels(channels);
        }

        if (stream.getChannelLayout() != null)
            audioTrack.setChannelLayout(stream.getChannelLayout());

        if (stream.getTag("BPS-eng") != null)
            audioTrack.setBitrate(Math.round(Double.parseDouble(stream.getTag("BPS-eng")) / Math.pow(10, 3)));

        if (stream.getTag("language") != null) {
            language = Locale.of(stream.getTag("language")).getDisplayName();
            audioTrack.setLanguageTag(stream.getTag("language"));
            audioTrack.setLanguage(language);
        }

        if (stream.getBitsPerRawSample() != null)
            audioTrack.setBitDepth(String.valueOf(stream.getBitsPerRawSample()));

        if (stream.getProfile() != null) {
            if (stream.getProfile().equals("DTS-HD MA"))
                audioTrack.setProfile("ma");
            else if (stream.getProfile().equals("LC"))
                audioTrack.setProfile(("lc"));
        }

        if (stream.getSampleRate() != null)
            audioTrack.setSamplingRate(String.valueOf(stream.getSampleRate()));

        if (stream.getProfile() != null && stream.getProfile().equals("DTS-HD MA"))
            codecDisplayName = stream.getProfile();
        else
            codecDisplayName = stream.getCodecName().toUpperCase();

        audioTrack.setDisplayTitle(language + " (" + codecDisplayName + " " + channels + ")");
        episode.getAudioTracks().add(audioTrack);
    }

    private static void processSubtitleData(Stream stream, Episode episode) {
        String title = "", language = "", codecDisplayName = "";
        SubtitleTrack subtitleTrack = new SubtitleTrack(stream.getIndex());

        if (stream.getCodecName() != null) {
            codecDisplayName = stream.getCodecName().toUpperCase();

            if (codecDisplayName.equals("HDMV_PGS_SUBTITLE"))
                codecDisplayName = "PGS";

            subtitleTrack.setCodec(codecDisplayName);
        }

        if (stream.getCodecLongName() != null)
            subtitleTrack.setCodecExt(stream.getCodecLongName());

        if (stream.getTag("language") != null) {
            language = Locale.of(stream.getTag("language")).getDisplayName();
            subtitleTrack.setLanguageTag(stream.getTag("language"));
            subtitleTrack.setLanguage(language);
        }

        if (stream.getTag("title") != null) {
            title = stream.getTag("title");
            subtitleTrack.setTitle(title);
        }

        subtitleTrack.setDisplayTitle(title + " (" + language + " " + codecDisplayName + ")");
        episode.getSubtitleTracks().add(subtitleTrack);
    }

    private static String formatResolution(int width, int height) {
        return switch (width) {
            case 7680 -> "8K";
            case 3840 -> "4K";
            case 2560 -> "QHD";
            case 1920 -> "1080p";
            case 1280 -> "720p";
            case 854 -> "480p";
            case 640 -> "360p";
            default -> height + "p";
        };
    }

    private static String formatAudioChannels(int channels) {
        return switch (channels) {
            case 1 -> "MONO";
            case 2 -> "STEREO";
            case 3 -> "2.1";
            case 4 -> "3.1";
            case 5 -> "4.1";
            case 6 -> "5.1";
            case 7 -> "6.1";
            case 8 -> "7.1";
            case 9 -> "7.2";
            case 10 -> "9.1";
            case 11 -> "10.1";
            case 12 -> "11.1";
            default -> String.valueOf(channels);
        };
    }
    //endregion

    //region FILE NAME PARSING

    public static List<Integer> detectSeasonEpisode(String filename) {
        List<Integer> seasonEpisode = new ArrayList<>();
        String regex = "(?i)" +  //Case insensitive
                "(?<!\\d)" + //Avoid resolutions as (1080p)
                "((?:s?(\\d{1,2})[ex-](\\d{1,2}))|(?:II?|III?|IV?|V?)\\s?-\\s?(\\d{1,2}))" +
                "(?!\\d)"; //Avoid resolutions as (1080p)

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filename);

        if (matcher.find()) {
            String season = matcher.group(2);
            String episode = matcher.group(3);

            //Roman number to decimal number
            if (season == null && matcher.group(4) != null) {
                season = String.valueOf(romanToInt(matcher.group(1).split("-")[0].trim()));

                if (season.equals("0"))
                    season = null;

                episode = matcher.group(4);
            }

            if (season != null && episode != null) {
                seasonEpisode.add(Integer.parseInt(season));
                seasonEpisode.add(Integer.parseInt(episode));
            }

            return seasonEpisode;
        }
        return null;
    }

    public static int romanToInt(String roman) {
        return switch (roman.toUpperCase()) {
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            case "V" -> 5;
            case "VI" -> 6;
            case "VII" -> 7;
            case "VIII" -> 8;
            case "IX" -> 9;
            case "X" -> 10;
            default -> 0;
        };
    }
    //endregion
}
