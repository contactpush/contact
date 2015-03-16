package com.codepath.contact.helpers;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by mekilah on 3/15/15.
 */
public class LocationHelper implements android.location.LocationListener{

    private LocationManager locationManager;
    private LocationHelperListener listener;
    public int millisecondsLocationAllowedStale =  2 * 60 * 1000; // 2 minutes

    public interface LocationHelperListener{
        public void onLocationUpdated(Location location);
    }

    public void getRecentLocation(Context context, LocationHelperListener listener){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - millisecondsLocationAllowedStale) {
            //location is not too stale. use it
            if(listener != null){
                listener.onLocationUpdated(location);
            }
        }
        else {
            this.listener = listener;
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location){
        if(this.listener != null){
            this.listener.onLocationUpdated(location);
            this.listener = null;
        }

        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){

    }

    @Override
    public void onProviderEnabled(String provider){

    }

    @Override
    public void onProviderDisabled(String provider){

    }
}
