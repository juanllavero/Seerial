package com.example.executablelauncher;

import java.util.Comparator;

public class Utils {
    private static int CompareSeriesOrSeasons(int order, int order2, String name, String name2) {
        if (order != 0 && order2 == 0)
            return -1;

        if (order == 0 && order2 != 0)
            return 1;

        if (order != 0){
            return Integer.compare(order, order2);
        }

        return name.compareTo(name2);
    }
    static class SeriesComparator implements Comparator<Series> {
        @Override
        public int compare(Series a, Series b) {
            return CompareSeriesOrSeasons(a.getOrder(), b.getOrder(), a.getName(), b.getName());
        }
    }

    static class SeasonComparator implements Comparator<Season> {
        @Override
        public int compare(Season a, Season b) {
            return CompareSeriesOrSeasons(a.getOrder(), b.getOrder(), a.getName(), b.getName());
        }
    }

    static class DiscComparator implements Comparator<Disc> {
        @Override
        public int compare(Disc a, Disc b) {
            return Float.compare(a.getEpisodeNumber(), b.getEpisodeNumber());
        }
    }

}
