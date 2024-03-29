package com.example.executablelauncher.utils;

import com.example.executablelauncher.DataManager;
import com.example.executablelauncher.entities.Library;
import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static class SeriesComparator implements Comparator<Series> {
        @Override
        public int compare(Series s1, Series s2) {
            if (s1.getOrder() != 0 && s2.getOrder() != 0) {
                return Integer.compare(s1.getOrder(), s2.getOrder());
            }

            return s1.getName().compareTo(s2.getName());
        }
    }

    public static class SeasonComparator implements Comparator<Season> {
        @Override
        public int compare(Season s1, Season s2) {
            if (s1.getOrder() != 0 && s2.getOrder() != 0) {
                return Integer.compare(s1.getOrder(), s2.getOrder());
            }

            if (DataManager.INSTANCE.currentLibrary.type.equals("Shows")){
                if (s1.getSeasonNumber() == 0 || s2.getSeasonNumber() == 0) {
                    //Check if there is a season with seasonNumber == 0, for it has to be at the end of the seasons list
                    if (s1.getSeasonNumber() == 0 && s2.getSeasonNumber() != 0) {
                        return 1;
                    } else if (s1.getSeasonNumber() != 0 && s2.getSeasonNumber() == 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                }else{
                    return Integer.compare(s1.getSeasonNumber(), s2.getSeasonNumber());
                }
            }

            //If the current library is for movies, then we compare the year of each season (movie)
            int year1 = Integer.parseInt(s1.getYear());
            int year2 = Integer.parseInt(s2.getYear());
            return Integer.compare(year1, year2);
        }
    }

    public static class EpisodeComparator implements Comparator<Episode> {
        @Override
        public int compare(Episode a, Episode b) {
            if (a.getOrder() != 0 && b.getOrder() != 0) {
                return Integer.compare(a.getOrder(), b.getOrder());
            }

            return Integer.compare(b.getEpisodeNumber(), a.getEpisodeNumber());
        }
    }

    public static class LibraryComparator implements Comparator<Library> {
        @Override
        public int compare(Library a, Library b) {
            return a.name.compareTo(b.name);
        }
    }

    public static String episodeDateFormat(String originalDate, String language) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag(language));

        try {
            Date date = originalFormat.parse(originalDate);
            return newFormat.format(date);
        } catch (ParseException e) {
            System.err.println("episodeDateFormat: Error applying date format");
            return originalDate;
        }
    }

    public static class LocaleStringConverter extends javafx.util.StringConverter<Locale> {
        @Override
        public String toString(Locale object) {
            if (object != null)
                return object.getDisplayName();

            return null;
        }

        @Override
        public Locale fromString(String string) {
            return Locale.forLanguageTag(string);
        }
    }

}
