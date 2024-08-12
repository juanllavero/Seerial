package com.example.executablelauncher.tmdbMetadata.movieCredits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cast {
    public boolean adult;
    public int gender;
    public int id;
    public String known_for_department;
    public String name;
    public String original_name;
    public double popularity;
    public String profile_path;
    public int cast_id;
    public String character;
    public String credit_id;
    public int order;
}
