package com.example.executablelauncher.tmdbMetadata.movieCredits;

import com.example.executablelauncher.tmdbMetadata.common.Crew;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Credits {
    public int id;
    public ArrayList<Cast> cast;
    public ArrayList<Crew> crew;
    public ArrayList<Cast> guest_stars;
}

