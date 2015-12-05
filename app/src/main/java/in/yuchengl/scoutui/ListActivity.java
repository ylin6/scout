package in.yuchengl.scoutui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ListActivity extends AppCompatActivity implements LocationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        redirectIfNotLoggedIn(this);

        // Dummy DB
        ListItem user1 = new ListItem("Dome", "image.png", true);
        ListItem user2 = new ListItem("Marshall Sprigg", "image2.png", true);
        ListItem user3 = new ListItem("Yucheng Lin", "image3.png", true);
        ListItem user4 = new ListItem("Joe Montana","image4.png", true);
        ListItem user5 = new ListItem("Lou Holtz","image4.png", true);
        ListItem[] friends = {user1, user2, user3, user4, user5};

        ListAdapter friendListAdapter = new CustomListAdapter(this, friends);

        ListView friendsList = (ListView) findViewById(R.id.friendList);
        friendsList.setAdapter(friendListAdapter);

        /* initialize switch */
        final Switch liveSwitch = (Switch) findViewById(R.id.liveswitch);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(),
                new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    liveSwitch.setChecked(parseObject.getBoolean("live"));
                    if (parseObject.getBoolean("live")) {
                        Toast.makeText(getApplicationContext(), "Broadcast on",
                                Toast.LENGTH_SHORT).show();
                        ((Scout) getApplication()).mBroadcasting = true;
                        ((Scout) getApplication()).startListening();
                    } else {
                        Toast.makeText(getApplicationContext(), "Broadcast off",
                                Toast.LENGTH_SHORT).show();
                        ((Scout) getApplication()).mBroadcasting = false;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        liveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ParseUser user = ParseUser.getCurrentUser();
                if (isChecked) {
                    user.put("live", true);
                    ((Scout) getApplication()).mBroadcasting = true;
                    ((Scout) getApplication()).startListening();
                } else {
                    user.put("live", false);
                    ((Scout) getApplication()).mBroadcasting = false;
                    ((Scout) getApplication()).stopListening();
                }
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Failed to update status",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Updated broadcast status",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        registerListClickCallback();
    }

    public void registerListClickCallback(){
        ListView list = (ListView) findViewById(R.id.friendList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                int LocationData = 7;
                Intent startScouting = new Intent(ListActivity.this, CameraActivity.class);
                startScouting.putExtra("LocationData", LocationData);
                startActivity(startScouting);
            }
        });
    }

    public void goToAddFriend(){
        Intent addNewFriend = new Intent(this, AddFriendActivity.class);
        startActivity(addNewFriend);
    }

    public void logout(){
      //TODO Logout
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Intent goToLogin = new Intent(this, LoginActivity.class);
        startActivity(goToLogin);
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        else if(id == R.id.action_logout){
            logout();
            return true;
        }

        else if(id == R.id.action_addfriend){
            Intent addNewFriend = new Intent(this, AddFriendActivity.class);
            startActivity(addNewFriend);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ParseUser user = ParseUser.getCurrentUser();
        user.put("live", false);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {}
        });
    }

    private ParseUser redirectIfNotLoggedIn(Context context) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            return currentUser;
        } else {
            Toast.makeText(context, "You must login first",
                    Toast.LENGTH_SHORT).show();
            Intent redirectIntent = new Intent(context, LoginActivity.class);
            context.startActivity(redirectIntent);
            return null;
        }
    }

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
}
