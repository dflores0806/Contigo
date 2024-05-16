package com.spilab.monact.utils;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;

public class LocationUtils {












    public static boolean insideArea(final Double latitude,final Double longitude,final Double radius, LatLng location){

        PointF origin = new PointF(latitude.floatValue(), longitude.floatValue());
        PointF north = calculateDerivedPosition(origin, radius, 0);
        PointF east = calculateDerivedPosition(origin, radius, 90);
        PointF south = calculateDerivedPosition(origin, radius, 180);
        PointF west = calculateDerivedPosition(origin, radius, 270);


        if(location.latitude>south.x && location.latitude<north.x
                && location.longitude>west.y && location.longitude<east.y){
            return true;
        }


        return false;
    }

    private static PointF calculateDerivedPosition(PointF point, double range, double bearing){
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;
    }
}

