package com.example.executablelauncher;

import com.example.executablelauncher.entities.Category;
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
    List<Category> categories = new ArrayList<>();
    public Category currentCategory = null;

    //region LOAD/SAVE DATA
    public void loadData(){
        try{
            JsonReader reader = new JsonReader(new FileReader(LOCAL_FILE));
            Type type = new TypeToken<List<Category>>() {}.getType();
            categories = new Gson().fromJson(reader, type);
        } catch (FileNotFoundException e) {
            System.err.println("loadData: Json files not found");
        }
    }
    public void saveData(){
        try (Writer writer = new FileWriter(LOCAL_FILE)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(categories, writer);
        } catch (IOException e) {
            System.err.println("saveData: Error saving data");
        }
    }
    //endregion

    public void createCategory(Category category){
        categories.add(category);
    }
    public List<Category> getCategories(boolean fullscreen){
        if (!fullscreen)
            return categories;

        List<Category> categoriesList = new ArrayList<>();

        for (Category cat : categories)
            if (cat.isShowOnFullscreen())
                categoriesList.add(cat);

        return categoriesList;
    }

    public Category getCategory(String name){
        for (Category category : categories){
            if (category.name.equals(name))
                return category;
        }
        return null;
    }

    public boolean categoryExist(String name){
        for (Category category : categories){
            if (category.name.equals(name))
                return true;
        }
        return false;
    }

    //region DELETE
    public void deleteCategory(Category category){
        if (category == null)
            return;

        for (Series series : category.getSeries())
            deleteSeriesData(series);

        categories.remove(category);
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

        currentCategory.analyzedFolders.remove(series.folder);
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

        currentCategory.seasonFolders.remove(season.folder);
        currentCategory.analyzedFolders.remove(season.folder);
    }
    public void deleteEpisodeData(Episode episode){
        try{
            FileUtils.deleteDirectory(new File("src/main/resources/img/discCovers/" + episode.getId()));
        } catch (IOException e) {
            System.err.println("App.removeDisc: Error deleting directory: src/main/resources/img/discCovers/" + episode.getId());
        }

        currentCategory.analyzedFiles.remove(episode.videoSrc);
    }
    //endregion
}
