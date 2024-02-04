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
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static DataManager INSTANCE = new DataManager();
    final String LOCAL_FILE = "data.json";
    List<Library> libraries = new ArrayList<>();
    List<Series> seriesToRemove = new ArrayList<>();
    public Library currentLibrary = null;

    //region LOAD/SAVE DATA
    public void loadData(){
        try{
            JsonReader reader = new JsonReader(new FileReader(LOCAL_FILE));
            Type type = new TypeToken<List<Library>>() {}.getType();
            libraries = new Gson().fromJson(reader, type);
        } catch (FileNotFoundException e) {
            System.err.println("loadData: Json files not found");
        }

        for (Library library : libraries){
            for (Series series : library.getSeries())
                checkEmptySeasons(library, series, false);

            for (Series series : seriesToRemove) {
                deleteSeriesData(series);
                library.removeSeries(series);
            }

            seriesToRemove.clear();
        }
    }
    public void saveData(){
        try (Writer writer = new FileWriter(LOCAL_FILE)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(libraries, writer);
        } catch (IOException e) {
            System.err.println("saveData: Error saving data");
        }
    }
    //endregion

    public void createCategory(Library library){
        libraries.add(library);
    }
    public List<Library> getCategories(boolean fullscreen){
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
            if (library.name.equals(name))
                return library;
        }
        return null;
    }

    public boolean libraryExists(String name){
        for (Library library : libraries){
            if (library.name.equals(name))
                return true;
        }
        return false;
    }

    public void checkEmptySeasons(Library library, Series s, boolean removeHere){
        List<Season> seasonsToDelete = new ArrayList<>();
        for (Season season : s.getSeasons()){
            if (season.getEpisodes().isEmpty())
                seasonsToDelete.add(season);
        }

        for (Season season : seasonsToDelete)
            s.removeSeason(season);

        if (removeHere && s.getSeasons().isEmpty()) {
            deleteSeriesData(s);
            library.removeSeries(s);
        }else if (s.getSeasons().isEmpty())
            seriesToRemove.add(s);
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
            FileUtils.deleteDirectory(new File("src/main/resources/img/seriesCovers/" + series.getId()));
            FileUtils.deleteDirectory(new File("src/main/resources/img/logos/" + series.getId()));
        } catch (IOException e) {
            System.err.println("App.removeCollection: Error deleting cover images directory");
        }

        for (Season season : series.getSeasons())
            deleteSeasonData(season);

        currentLibrary.analyzedFolders.remove(series.folder);
    }
    public void deleteSeasonData(Season season){
        try{
            FileUtils.deleteDirectory(new File("src/main/resources/img/backgrounds/" + season.getId()));
            FileUtils.deleteDirectory(new File("src/main/resources/img/logos/" + season.getId()));
            FileUtils.deleteDirectory(new File("src/main/resources/img/seriesCovers/" + season.getId()));
            if (!season.getMusicSrc().isEmpty())
                Files.delete(FileSystems.getDefault().getPath(season.getMusicSrc()));
            if (!season.getVideoSrc().isEmpty())
                Files.delete(FileSystems.getDefault().getPath(season.getVideoSrc()));
        } catch (IOException e) {
            System.err.println("App.removeSeason: Error deleting images files and directories");
        }

        for (Episode episode : season.getEpisodes())
            deleteEpisodeData(episode);

        currentLibrary.seasonFolders.remove(season.folder);
        currentLibrary.analyzedFolders.remove(season.folder);
    }
    public void deleteEpisodeData(Episode episode){
        try{
            FileUtils.deleteDirectory(new File("src/main/resources/img/discCovers/" + episode.getId()));
        } catch (IOException e) {
            System.err.println("App.removeDisc: Error deleting directory: src/main/resources/img/discCovers/" + episode.getId());
        }

        currentLibrary.analyzedFiles.remove(episode.videoSrc);
    }
    //endregion
}
