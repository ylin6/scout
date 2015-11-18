package in.yuchengl.scoutui;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class Scout extends Application {
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;
    public boolean mBroadcasting = false;

    public Location getLocation() {
        return mLocation;
    }

    public void startListening() {
        mLocationManager.requestLocationUpdates(mLocationManager.NETWORK_PROVIDER, 500, 0,
                mLocationListener);
    }

    public void stopListening() {
        mLocationManager.removeUpdates(mLocationListener);
    }

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

                if (mBroadcasting) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                    query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                parseObject.put("latitude", mLocation.getLatitude());
                                parseObject.put("longitude", mLocation.getLongitude());
                            }
                        }
                    });
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
