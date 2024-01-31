package com.example.executablelauncher;

import com.example.executablelauncher.entities.Category;
import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import org.apache.commons.io.FileUtils;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.repository.ObjectRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;

import static org.dizitart.no2.filters.FluentFilter.where;

public class DBManager {
    public static DBManager INSTANCE = new DBManager();
    private static Nitrite database;

    ObjectRepository<Category> categoriesRepository;
    ObjectRepository<Series> seriesRepository;
    ObjectRepository<Season> seasonRepository;
    ObjectRepository<Episode> episodesRepository;

    public void openDB(){
        if (database != null)
            database.close();

        //DB Initialization
        MVStoreModule storeModule = MVStoreModule.withConfig()
                .filePath("data.db")
                .compress(true)
                .build();

        database = Nitrite.builder()
                .loadModule(storeModule)
                .openOrCreate();

        //Repositories Initialization
        categoriesRepository = database.getRepository(Category.class);
        seriesRepository = database.getRepository(Series.class);
        seasonRepository = database.getRepository(Season.class);
        episodesRepository = database.getRepository(Episode.class);
    }
    public void closeDB(){
        database.close();
    }

    //region CREATE
    public Category createCategory(Category category){
        categoriesRepository.insert(category);

        return getCategory(category.getId());
    }
    public void createSeries(Series series){
        seriesRepository.insert(series);
    }
    public void createSeason(Season season){
        seasonRepository.insert(season);
    }
    public void createEpisode(Episode episode){
        episodesRepository.insert(episode);
    }
    //endregion

    //region READ
    public Category getCategory(String id){ return categoriesRepository.getById(id); }
    public Series getSeries(String id){ return seriesRepository.getById(id); }
    public Season getSeason(String id){ return seasonRepository.getById(id); }
    public Episode getEpisode(String id){ return episodesRepository.getById(id); }
    public List<Category> getCategories(boolean fullscreen){
        if (fullscreen)
            return categoriesRepository.find(where("showOnFullscreen").eq(true)).toList();
        else
            return categoriesRepository.find().toList();
    }
    public Season getSeasonInSeries(Series series, int seasonNumber){
        for (String seasonID : series.getSeasons()){
            Season season = getSeason(seasonID);

            if (season == null)
                continue;

            if (season.getSeasonNumber() == seasonNumber)
                return season;
        }

        return null;
    }
    public Episode getEpisodeInSeason(Season season, int episodeNumber){
        for (String episodeID : season.getEpisodes()){
            Episode episode = getEpisode(episodeID);

            if (episode == null)
                continue;

            if (episode.getEpisodeNumber() == episodeNumber)
                return episode;
        }

        return null;
    }
    //endregion

    //region UPDATE
    public void updateCategory(Category category){
        categoriesRepository.update(where("id").eq(category.getId()), category);
    }
    public void updateSeries(Series series){
        seriesRepository.update(where("id").eq(series.getId()), series);
    }
    public void updateSeason(Season season){
        seasonRepository.update(where("id").eq(season.getId()), season);
    }
    public void updateEpisode(Episode episode){
        episodesRepository.update(where("id").eq(episode.getId()), episode);
    }
    //endregion

    //region DELETE
    public void deleteCategory(Category category){
        List<String> seriesIDs = List.copyOf(category.getSeries());

        for (String seriesID : seriesIDs){
            Series series = getSeries(seriesID);

            if (series != null) {
                deleteSeries(series);
            }
        }

        categoriesRepository.remove(category);
    }
    public void deleteSeries(Series series){
        App.currentCategory.removeSeries(series.getId());

        try{
            FileUtils.deleteDirectory(new File("src/main/resources/img/seriesCovers/" + series.getId()));
            FileUtils.deleteDirectory(new File("src/main/resources/img/logos/" + series.getId()));
        } catch (IOException e) {
            System.err.println("App.removeCollection: Error deleting cover images directory");
        }

        List<String> seasonsIDs = List.copyOf(series.getSeasons());

        for (String seasonID : seasonsIDs){
            Season season = getSeason(seasonID);

            if (season != null) {
                deleteSeason(season);
            }
        }

        App.currentCategory.analyzedFolders.remove(series.folder);
        seriesRepository.remove(series);
    }
    public void deleteSeason(Season season){
        Series series = getSeries(season.getSeriesID());

        if (series != null)
            series.removeSeason(season.getId());

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

        List<String> episodeIDs = List.copyOf(season.getEpisodes());

        for (String episodeID : episodeIDs){
            Episode episode = getEpisode(episodeID);

            if (episode != null) {
                deleteEpisode(episode);
            }
        }

        App.currentCategory.seasonFolders.remove(season.folder);
        App.currentCategory.analyzedFolders.remove(season.folder);
        seasonRepository.remove(season);
    }
    public void deleteEpisode(Episode episode){
        Season season = getSeason(episode.getSeasonID());

        if (season != null)
            season.removeEpisode(episode.getId());

        try{
            FileUtils.deleteDirectory(new File("src/main/resources/img/discCovers/" + episode.getId()));
        } catch (IOException e) {
            System.err.println("App.removeDisc: Error deleting directory: src/main/resources/img/discCovers/" + episode.getId());
        }

        App.currentCategory.analyzedFiles.remove(episode.executableSrc);
        episodesRepository.remove(episode);
    }
    //endregion
}
