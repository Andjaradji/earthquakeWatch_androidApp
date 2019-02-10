package com.example.andjaradji.earthquakewatcher.Utils;

import java.util.Random;

/**
 * Created by Anjar on 17/12/2017.
 */

public class Constants {
    public static final String URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojson";
    public static final int LIMIT=30;
    public static final double MAX_MAG = 2.0;

    public static int randomInt (int max, int min) {
        return new Random().nextInt(max - min) + min;
    }
}
