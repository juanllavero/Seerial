package com.example.executablelauncher;

import com.example.executablelauncher.entities.Library;
import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataManager {
    public static final DataManager INSTANCE = new DataManager();
    final String LOCAL_FILE = "data.json";
    List<Library> libraries = new ArrayList<>();
    public Library currentLibrary = null;

    //region LOAD/SAVE DATA
    public void loadData(){
        //Create empty folders
        createFolder("resources/music");
        createFolder("resources/downloadedMediaCache");
        createFolder("resources/img/chaptersCovers");
        createFolder("resources/img/backgrounds");
        createFolder("resources/img/discCovers");
        createFolder("resources/img/logos");
        createFolder("resources/img/seriesCovers");
        createFolder("resources/img/DownloadCache");

        try{
            JsonReader reader = new JsonReader(new FileReader(LOCAL_FILE));
            Type type = new TypeToken<List<Library>>() {}.getType();
            libraries = new Gson().fromJson(reader, type);
        } catch (FileNotFoundException e) {
            createEmptyJson();
        }

        //If data.json is empty a [] has to be added to avoid an exception in the next loop
        if (libraries == null){
            try (Writer writer = new FileWriter("data.json")) {
                Gson gson = new GsonBuilder().create();
                gson.toJson("[]", writer);
            } catch (IOException e) {
                System.err.println("loadData: Error writing in data.json");
            }
        }

        for (Library library : libraries) {
            currentLibrary = library;
            scanForMissingFiles(library);
        }

        if (!libraries.isEmpty()){
            currentLibrary = libraries.getFirst();
        }
    }
    public void saveData(){
        try (Writer writer = new FileWriter(LOCAL_FILE)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(libraries, writer);
            writer.flush();                     //To make sure all data is saved in the file before it is closed
        } catch (IOException e) {
            System.err.println("saveData: Error saving data");
        }
    }
    public void createEmptyJson(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String jsonContent = "[]";

        try (FileWriter fileWriter = new FileWriter("data.json")) {
            gson.toJson(jsonContent, fileWriter);
        } catch (IOException e) {
            System.err.println("createEmptyJson: data.json could not be created");
        }
    }
    public void createFolder(String dir){
        File folder = new File(dir);

        if (!folder.exists())
            folder.mkdirs();
    }
    //endregion

    public void scanForMissingFiles(Library library){
        Iterator<Series> seriesIterator = library.getSeries().iterator();
        while (seriesIterator.hasNext()) {
            Series show = seriesIterator.next();
            show.setAnalyzingFiles(false);

            Iterator<Season> temporadaIterator = show.getSeasons().iterator();
            while (temporadaIterator.hasNext()) {
                Season season = temporadaIterator.next();

                //Mark episodes to remove
                List<Episode> episodesToRemove = new ArrayList<>();

                for (Episode episode : season.getEpisodes()) {
                    File file = new File(episode.getVideoSrc());

                    //Check if drive is connected and the file is missing
                    if (App.checkIfDriveIsConnected(episode.getVideoSrc()) && !file.exists()) {
                        episodesToRemove.add(episode);
                    } else if (episode.getImgSrc().isEmpty()) {
                        File imageFolder = new File("resources/img/discCovers/" + episode.getId() + "/");

                        if (!imageFolder.exists()) {
                            System.out.println("Image folder does not exist for episode: " + episode.getId());
                            continue;
                        }

                        File[] images = imageFolder.listFiles();

                        if (images == null)
                            continue;

                        episode.setImgSrc("resources/img/discCovers/" + episode.getId() + "/" + images[0].getName());
                    }
                }

                //Remove episodes
                for (Episode episode : episodesToRemove) {
                    season.getEpisodes().remove(episode);
                    deleteEpisodeData(episode);
                }

                //Remove season if it has no episodes
                if (season.getEpisodes().isEmpty()) {
                    temporadaIterator.remove();
                    deleteSeasonData(season);
                }
            }

            //Remove show if it has no seasons
            if (show.getSeasons().isEmpty()) {
                seriesIterator.remove();
                deleteSeriesData(show);
            }
        }
    }
    public void createLibrary(Library library){
        libraries.add(library);
    }
    public List<Library> getLibraries(boolean fullscreen){
        if (!fullscreen)
            return libraries;

        List<Library> categoriesList = new ArrayList<>();

        for (Library cat : libraries)
            if (cat.isShowOnFullscreen())
                categoriesList.add(cat);

        return categoriesList;
    }

    public Library getLibrary(String name){
        for (Library library : libraries){
            if (library.getName().equals(name))
                return library;
        }
        return null;
    }

    public Library getLibrary(Series series){
        for (Library library : libraries){
            for (Series s : library.getSeries()){
                if (s == series)
                    return library;
            }
        }
        return currentLibrary;
    }

    public boolean libraryExists(String name){
        for (Library library : libraries){
            if (library.getName().equals(name))
                return true;
        }
        return false;
    }

    //region DELETE
    public void deleteLibrary(Library library){
        if (library == null)
            return;

        for (Series series : library.getSeries())
            deleteSeriesData(series);

        libraries.remove(library);
    }
    public void deleteSeriesData(Series series){
        try{
            FileUtils.deleteDirectory(new File("resources/img/seriesCovers/" + series.getId()));
            FileUtils.deleteDirectory(new File("resources/img/logos/" + series.getId()));
        } catch (IOException e) {
            System.err.println("removeCollection: Error deleting cover images directory");
        }

        for (Season season : series.getSeasons())
            deleteSeasonData(season);

        currentLibrary.getAnalyzedFolders().remove(series.getFolder());
    }
    public void deleteSeasonData(Season season){
        try{
            FileUtils.deleteDirectory(new File("resources/img/backgrounds/" + season.getId()));
            FileUtils.deleteDirectory(new File("resources/img/logos/" + season.getId()));
            FileUtils.deleteDirectory(new File("resources/img/seriesCovers/" + season.getId()));
            if (!season.getMusicSrc().isEmpty())
                Files.delete(FileSystems.getDefault().getPath(season.getMusicSrc()));
            if (!season.getVideoSrc().isEmpty())
                Files.delete(FileSystems.getDefault().getPath(season.getVideoSrc()));
        } catch (IOException e) {
            System.err.println("removeSeason: Error deleting images files and directories");
        }

        for (Episode episode : season.getEpisodes())
            deleteEpisodeData(episode);

        currentLibrary.getSeasonFolders().remove(season.getFolder());
    }
    public void deleteEpisodeData(Episode episode){
        try{
            FileUtils.forceDelete(new File("resources/img/discCovers/" + episode.getId()));
        } catch (IOException e) {
            System.err.println("removeDisc: Error deleting directory: resources/img/discCovers/" + episode.getId());
        }

        currentLibrary.getAnalyzedFiles().remove(episode.getVideoSrc());
    }
    //endregion
}
