package in.yuchengl.scoutui;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class Scout extends Application {
    private Location mLocation;
    public boolean mBroadcasting = false;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    public Location getLocation() {
        return mLocation;
    }

    public void startListening() {
        mLocationManager.requestLocationUpdates(mLocationManager.NETWORK_PROVIDER, 400, 1,
                mLocationListener);
    }

    public void stopListening() {
        mLocationManager.removeUpdates(mLocationListener);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ZFqxViHGMZC0FRXXu1QlQJF44lewIZ4VBLDAPlZ5",
                "3cmt5L56qE1NpMcqzZInRGNhIhNcy1feJb9cKIdx");

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
                Log.d("Application", "Lat: " + location.getLatitude() + " Long: " +
                        location.getLongitude());

                if (mBroadcasting) {
                    ParseUser user = ParseUser.getCurrentUser();

                    if (user != null) {
                        user.put("latitude", mLocation.getLatitude());
                        user.put("longitude", mLocation.getLongitude());
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("Application", "location updated");
                                } else {
                                    Log.e("Application", "Failed to update location");
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };
    }
}
