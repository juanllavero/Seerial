package com.example.executablelauncher.tmdbMetadata.series;

import com.example.executablelauncher.tmdbMetadata.common.Crew;
import com.example.executablelauncher.tmdbMetadata.common.GuestStar;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

public class EpisodeMetadata {
    public String air_date;
    public int episode_number;
    public int id;
    public String name;
    public String overview;
    public String production_code;
    public String episode_type;
    public int runtime;
    public int season_number;
    public int show_id;
    public String still_path;
    public double vote_average;
    public int vote_count;
    @JsonIgnore
    public ArrayList<Crew> crew;
    @JsonIgnore
    public ArrayList<GuestStar> guest_stars;
}