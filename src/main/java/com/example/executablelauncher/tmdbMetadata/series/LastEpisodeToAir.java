package com.example.executablelauncher.tmdbMetadata.series;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LastEpisodeToAir {
    public int id;
    public String name;
    public String overview;
    public int vote_average;
    public int vote_count;
    public String air_date;
    public int episode_number;
    @JsonIgnore
    public String episode_type;
    public String production_code;
    public int runtime;
    public int season_number;
    public int show_id;
    public String still_path;
}
