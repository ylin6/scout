package in.yuchengl.scoutui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        redirectIfNotLoggedIn(this);

        // Dummy DB
        ListItem user1 = new ListItem("msprigg", "image.png", true);
        ListItem user2 = new ListItem("gmartinez", "image2.png", true);
        ListItem user3 = new ListItem("ylin6", "image3.png", true);
        ListItem user4 = new ListItem("Mosise","image4.png", true);
        ListItem user5 = new ListItem("Mosise","image4.png", true);
        ListItem[] friends = {user1, user2, user3, user4, user5, user5, user1, user4, user3, user2, user3, user4, user1 };

        ListAdapter friendListAdapter = new CustomListAdapter(this, friends);

        ListView friendsList = (ListView) findViewById(R.id.friendList);
        friendsList.setAdapter(friendListAdapter);

        Button startScoutingButton = (Button) findViewById(R.id.startScoutButton);

        /* initialize switch */
        final Switch liveSwitch = (Switch) findViewById(R.id.liveswitch);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(),
                new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    liveSwitch.setChecked(parseObject.getBoolean("live"));
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
                } else {
                    user.put("live", false);
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
    }

    public void goToCamera(View view){
        Intent startScouting = new Intent(this, CameraActivity.class);
        startActivity(startScouting);
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

}
