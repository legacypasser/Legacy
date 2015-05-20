package com.androider.legacy.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Think on 2015/5/19.
 */
public class Locator {
    public static Location getLocation(Context context){
        LocationManager manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, kidding);
        Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location == null){
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, kidding);
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return location;
    }

    static LocationListener kidding = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
