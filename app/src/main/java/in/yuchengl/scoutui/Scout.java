package in.yuchengl.scoutui;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.parse.Parse;

public class Scout extends Application {
    public LocationManager mLocationManager;
    public LocationListener mLocationListener;
    public Location mLocation;

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "ZFqxViHGMZC0FRXXu1QlQJF44lewIZ4VBLDAPlZ5",
                "3cmt5L56qE1NpMcqzZInRGNhIhNcy1feJb9cKIdx");

        // location services
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
                Log.d("Application", "Lat: " + location.getLatitude() + " Long: " +
                        location.getLongitude());
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
}
