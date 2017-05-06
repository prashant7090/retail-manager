package com.retailmanager.util;

import org.springframework.stereotype.Component;

/**
 * Created by prashant on 5/6/17.
 */

@Component
public class Haversine {

    private static final int EARTH_RADIUS = 6371; // Approx Earth radius in KM

    /**
     * Returns distance between two lat long points.
     * @param startLat customer's latitude.
     * @param startLong customer's longitude.
     * @param endLat shop's latitude.
     * @param endLong shops's latitude.
     * @return <tt>double</tt> value of distance between two latitude and longitude
     */
    public double distance(double startLat, double startLong,
                                  double endLat, double endLong) {

        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * Returns value which is calculated by below formula. More formally, we have split Haversine
     * and wrote function for common code.
     * @param val val whose value to calculate.
     * @return <tt>double</tt> value which is calculated by below formula
     */
    private double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }


}
