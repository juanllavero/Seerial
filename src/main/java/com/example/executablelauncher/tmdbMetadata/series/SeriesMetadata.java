package com.example.executablelauncher.tmdbMetadata.series;

import com.example.executablelauncher.tmdbMetadata.common.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

public class SeriesMetadata {
    public boolean adult;
    public String backdrop_path;
    @JsonIgnore
    public ArrayList<Object> created_by;
    public ArrayList<Integer> episode_run_time;
    public String first_air_date;
    public ArrayList<Genre> genres;
    public String homepage;
    public int id;
    public boolean in_production;
    public ArrayList<String> languages;
    public String last_air_date;
    @JsonIgnore
    public LastEpisodeToAir last_episode_to_air;
    public String name;
    @JsonIgnore
    public Object next_episode_to_air;
    public ArrayList<Network> networks;
    public int number_of_episodes;
    public int number_of_seasons;
    public ArrayList<String> origin_country;
    public String original_language;
    public String original_name;
    public String overview;
    public double popularity;
    public String poster_path;
    public ArrayList<ProductionCompany> production_companies;
    public ArrayList<ProductionCountry> production_countries;
    public ArrayList<Season> seasons;
    public ArrayList<SpokenLanguage> spoken_languages;
    public String status;
    public String tagline;
    public String type;
    public double vote_average;
    public int vote_count;
}

