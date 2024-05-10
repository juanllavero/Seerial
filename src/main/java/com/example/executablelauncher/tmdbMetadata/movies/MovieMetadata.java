package com.example.executablelauncher.tmdbMetadata.movies;

import com.example.executablelauncher.tmdbMetadata.common.Genre;
import com.example.executablelauncher.tmdbMetadata.common.ProductionCompany;
import com.example.executablelauncher.tmdbMetadata.common.ProductionCountry;
import com.example.executablelauncher.tmdbMetadata.common.SpokenLanguage;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

public class MovieMetadata {
    public boolean adult;
    public String backdrop_path;
    @JsonIgnore
    public BelongsToCollection belongs_to_collection;
    @JsonIgnore
    public int budget;
    public ArrayList<Genre> genres;
    @JsonIgnore
    public String homepage;
    public int id;
    public String imdb_id;
    public ArrayList<String> origin_country;
    public String original_language;
    public String original_title;
    public String overview;
    public double popularity;
    public String poster_path;
    public ArrayList<ProductionCompany> production_companies;
    public ArrayList<ProductionCountry> production_countries;
    public String release_date;
    public int revenue;
    public int runtime;
    @JsonIgnore
    public ArrayList<SpokenLanguage> spoken_languages;
    @JsonIgnore
    public String status;
    public String tagline;
    public String title;
    public boolean video;
    public double vote_average;
    public int vote_count;
}

