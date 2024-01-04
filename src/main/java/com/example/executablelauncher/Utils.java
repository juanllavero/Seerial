package com.example.executablelauncher;

import com.example.executablelauncher.entities.Disc;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;

import java.util.Comparator;

public class Utils {
    private static int CompareMedia(int order, int order2, String name, String name2) {
        if (order != 0 && order2 == 0)
            return -1;

        if (order == 0 && order2 != 0)
            return 1;

        if (order != 0){
            return Integer.compare(order, order2);
        }

        return name.compareTo(name2);
    }
    public static class SeriesComparator implements Comparator<Series> {
        @Override
        public int compare(Series a, Series b) {
            return CompareMedia(a.getOrder(), b.getOrder(), a.getName(), b.getName());
        }
    }

    public static class SeasonComparator implements Comparator<Season> {
        @Override
        public int compare(Season a, Season b) {
            return CompareMedia(a.getOrder(), b.getOrder(), a.getName(), b.getName());
        }
    }

    public static class DiscComparator implements Comparator<Disc> {
        @Override
        public int compare(Disc a, Disc b) {
            return CompareMedia(a.getOrder(), b.getOrder(), a.getName(), b.getName());
        }
    }

}
