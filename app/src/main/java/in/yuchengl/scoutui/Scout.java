package in.yuchengl.scoutui;

import android.app.Application;

import com.parse.Parse;

public class Scout extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "ZFqxViHGMZC0FRXXu1QlQJF44lewIZ4VBLDAPlZ5",
                "3cmt5L56qE1NpMcqzZInRGNhIhNcy1feJb9cKIdx");
    }
}
