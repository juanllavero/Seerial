package com.example.executablelauncher.utils;

import com.example.executablelauncher.entities.Category;
import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static int CompareMedia(int order, int order2, String name, String name2) {
        if (order != 0 && order2 == 0)
            return 1;

        if (order == 0 && order2 != 0)
            return -1;

        if (order != 0){
            return Integer.compare(order, order2);
        }

        return name.compareTo(name2);
    }
    public static class SeriesComparator implements Comparator<Series> {
        @Override
        public int compare(Series a, Series b) {
            if (a != null && b != null)
                return CompareMedia(a.getOrder(), b.getOrder(), a.getName(), b.getName());
            return 0;
        }
    }

    public static class SeasonComparator implements Comparator<Season> {
        @Override
        public int compare(Season a, Season b) {
            return CompareMedia(a.getOrder(), b.getOrder(), a.getName(), b.getName());
        }
    }

    public static class EpisodeComparator implements Comparator<Episode> {
        @Override
        public int compare(Episode a, Episode b) {
            return Integer.compare(b.getEpisodeNumber(), a.getEpisodeNumber());
        }
    }

    public static class CategoryComparator implements Comparator<Category> {
        @Override
        public int compare(Category a, Category b) {
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

}
