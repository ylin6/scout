package in.yuchengl.scoutui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class AddFriendActivity extends AppCompatActivity {
    private ArrayList<RequestListItem> mRequestList;
    private RequestListAdapter mRequestListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* FRIEND REQUEST LIST INITIALIZATION */
        mRequestList = new ArrayList<>();
        mRequestListAdapter = new RequestListAdapter(this, mRequestList);

        ListView RequestListView = (ListView) findViewById(R.id.pendingFriendList);
        RequestListView.setAdapter(mRequestListAdapter);
        updateRequestList();
    }

    void updateRequestList() {
        Log.d("Application", "update called");
        mRequestList.clear();
        mRequestListAdapter.notifyDataSetChanged();

        ParseQuery<ParseObject> requestQuery = ParseQuery.getQuery("FriendRequest");
        requestQuery.whereEqualTo("requestTo", ParseUser.getCurrentUser().getUsername());
        requestQuery.whereEqualTo("status", "pending");
        requestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < list.size(); i++) {
                        String status = list.get(i).getString("status");
                        if (!status.equals("pending")) {
                            continue;
                        }
                        String name = list.get(i).getString("requestFrom");
                        String rId = list.get(i).getObjectId();
                        RequestListItem request = new RequestListItem(name, rId);
                        mRequestList.add(request);
                        mRequestListAdapter.notifyDataSetChanged();
                        Log.d("Application", "added request to list");
                    }
                } else {
                    Log.d("Application", "error" + e.getMessage());
                }
            }
        });
    }

    public void AddNewFriend(View view) {
        EditText username_textfield = (EditText) findViewById(R.id.add_friend_username);
        final String add_friend_username = username_textfield.getText().toString();

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser.getUsername() != add_friend_username) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequest");
            query.whereEqualTo("requestFrom", currentUser.getUsername());
            query.whereEqualTo("requestTo", add_friend_username);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e != null) {
                        Log.d("Application", e.getMessage());
                    }

                    if (list.size() > 0){
                        String message = "Request already made: " + list.get(0).getString("status");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        ParseObject friendRequest = new ParseObject("FriendRequest");
                        friendRequest.put("requestFrom", currentUser.getUsername());
                        friendRequest.put("requestTo", add_friend_username);
                        friendRequest.put("status", "pending");
                        friendRequest.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    Toast.makeText(getApplicationContext(), "Request Sent",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Request Failed",
                                            Toast.LENGTH_SHORT).show();
                                    Log.d("Application", e.getMessage());
                                }
                            }
                        });
                    }
                }
            });

        }
        else{
            Context context = getApplicationContext();
            CharSequence text = "You Can't Add Yourself!";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.request_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id== R.id.action_refreshlist){
            updateRequestList();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}
