package com.example.turbo.dcidr.utils.common_utils;

import java.io.Serializable;

/**
 * Created by Borat on 4/1/2016.
 */
public class Points implements Serializable {
    public double mLatitude;
    public double mLongitude;

    public Points(double mLatitude, double mLongitude) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    public String toString() {
        return ("(" + mLatitude + ", " + mLongitude + ")");
    }

    public static double[] stringToDoubleArray(String pointsAsStr){
        double[] points = new double[2];
        points[0] = Double.valueOf(pointsAsStr.split(",")[0].split("\\(")[1]);
        points[1] = Double.valueOf(pointsAsStr.split(",")[1].split("\\)")[0]);
        return points;
    }
    
    public double getLatitude(){
        return this.mLatitude;
    }
    public double getLongitude(){
        return this.mLongitude;
    }
}
