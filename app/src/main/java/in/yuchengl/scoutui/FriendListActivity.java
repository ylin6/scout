package in.yuchengl.scoutui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {
    private ArrayList<FriendListItem> mFriendList;
    private FriendListAdapter mFriendListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        redirectIfNotLoggedIn(this);

        /* FRIENDS LIST INITIALIZATION */
        mFriendList = new ArrayList<>();
        mFriendListAdapter = new FriendListAdapter(this, mFriendList);

        ListView friendListView = (ListView) findViewById(R.id.friendList);
        friendListView.setAdapter(mFriendListAdapter);
        updateFriendList();

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = mFriendListAdapter.getItem(position).getName();
                String uid = mFriendListAdapter.getItem(position).getId();
                Boolean live = mFriendListAdapter.getItem(position).getLive();

                if (!live) {
                    Toast.makeText(getApplicationContext(), name + " is not live",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent scoutIntent = new Intent(FriendListActivity.this, ScoutingActivity.class);
                scoutIntent.putExtra("name", name);
                scoutIntent.putExtra("uid", uid);
                startActivity(scoutIntent);
            }
        });

        /* BROADCAST SWITCH INITIALIZATION */
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
    }

    private void updateFriendList() {
        List<String> friendList = ParseUser.getCurrentUser().getList("Friends");
        mFriendList.clear();
        mFriendListAdapter.notifyDataSetChanged();

        if (friendList == null) return;

        int size = friendList.size();
        for (int i = 0; i < size; i++) {
            ParseQuery<ParseObject> friendQuery = ParseQuery.getQuery("_User");
            friendQuery.getInBackground(friendList.get(i), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        String name = parseObject.getString("username");
                        String id = parseObject.getObjectId();
                        Boolean live = parseObject.getBoolean("live");
                        FriendListItem friend = new FriendListItem(name, id, live);
                        mFriendList.add(friend);
                        mFriendListAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Application", "friend not found");
                    }
                }
            });
        }
    }

    private void goToAddFriend(){
        Intent addNewFriend = new Intent(this, AddFriendActivity.class);
        startActivity(addNewFriend);
    }

    private void logout(){
        ParseUser.logOut();
        Intent goToLogin = new Intent(this, LoginActivity.class);
        startActivity(goToLogin);
    }

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

        else if (id== R.id.action_refreshlist){
            updateFriendList();
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
