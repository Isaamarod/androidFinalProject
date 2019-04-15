package com.isa_t.proyectofinalmasterd;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * Utilidad de geolocalizacion
 */
@SuppressWarnings({"MissingPermission"})
public class Geolocation {

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private LocationManager locationManager;

    /**
     * Indica si el gps esta habilitado en el dispositivo.
     */

    private boolean gps_enabled;

    /**
     * Indica si la geolocalizacion por red esta habilitada en el dispositivo.
     */

    private boolean network_enabled;

    private Location lastGpsLocation;

    private Location lastNetWorkLocation;

    private Activity activity;

    private LocationListener listener;

    /**
     * Construye un objeto de tipo Geolocation.
     * @param usingNetworkProvider Indica si se debe usar la geolocalizacion por red o no.
     */


    public Geolocation(boolean usingNetworkProvider, Activity activity){
        this.activity = activity;
        this.locationManager = (LocationManager) this.activity.getSystemService(Context.LOCATION_SERVICE);
        this.gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                locationListenerGps);
        if(usingNetworkProvider){
            this.network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,locationListenerNetwork);
        }
    }

    /**
     * Obtiene las coordenadas actuales del dispositivo.
     * @return Coordenadas actuales del dispositivo.
     */

    public Double[] getDevicePosition(){
        Double[] deviceLocation = null;
        Location location = getDeviceLocation();

        if(location!=null)
            deviceLocation = new Double[]{/*X*/location.getLatitude(), /*Y*/location.getLongitude()};
        else {
            deviceLocation = new Double[]{0d, 0d};
        }
        return deviceLocation;
    }

    public LatLng getDevicePositionLatLng(){
        LatLng deviceLocation = null;
        Location location = getDeviceLocation();

        if(location!=null)
            deviceLocation = new LatLng(/*X*/location.getLatitude(), /*Y*/location.getLongitude());
        else {
            deviceLocation = new LatLng(0d, 0d);
        }
        return deviceLocation;
    }

    public Location getDeviceLocation(){
        Location location = null;
        if (!gps_enabled && !network_enabled) {
            Toast.makeText(activity, "No coordinate location provider found. Please, activate GPS or location from network.", Toast.LENGTH_LONG).show();
        }else{
            if(network_enabled){
                Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(isBetterLocation(networkLocation, lastNetWorkLocation))
                    lastNetWorkLocation = networkLocation;
                location = lastNetWorkLocation;
            }
            if(gps_enabled){
                Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(isBetterLocation(gpsLocation, lastGpsLocation))
                    lastGpsLocation = gpsLocation;
                if(isBetterLocation(lastGpsLocation, location))
                    location = lastGpsLocation;
            }
        }
        return location;
    }

    public void setLocationListener(LocationListener listener){
        this.listener = listener;
    }

    /** Determines whether one Location reading is better than the current Location fix.
     * @param newLocation  The new Location that you want to evaluate
     * @param lastLocation  The current Location fix, to which you want to compare the new one
     * @return true si la nueva localizacion es mejor que la anterior, false de lo contrario.
     */
    private static boolean isBetterLocation(Location newLocation, Location lastLocation) {
        if(newLocation == null)
            return false;
        if (lastLocation == null)
            return true;

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - lastLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - lastLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
                lastLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     *  Checks whether two providers are the same.
     *  @param provider1 Proveedor a comparar.
     *  @param provider2 Proveedor a comparar.
     *	@return true si es el mismo proveedor de localizacion, false de lo contrario.
     * */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            Geolocation.this.lastGpsLocation = location;
            if(listener != null)
                listener.onLocationChanged(getDeviceLocation());
        }

        public void onProviderDisabled(String provider) {
            gps_enabled = false;
        }

        public void onProviderEnabled(String provider) {
            gps_enabled = true;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            Geolocation.this.lastNetWorkLocation = location;
            if(listener != null)
                listener.onLocationChanged(getDeviceLocation());
        }

        public void onProviderDisabled(String provider) {
            network_enabled = false;
        }

        public void onProviderEnabled(String provider) {
            network_enabled = true;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public void clearRegisteredListeners(){
        locationManager.removeUpdates(locationListenerNetwork);
        locationManager.removeUpdates(locationListenerGps);
    }
}