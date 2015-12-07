package in.yuchengl.scoutui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.util.Log;
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
    private ArrayList<FriendsListItem> mRequestsList;
    private RequestListAdapter mRequestsListAdaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
                    if (e == null){
                        Context context = getApplicationContext();
                        String message = "Request has already been made. Status is : " + list.get(0).getString("status");
                        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    else{
                        Log.d("Application", e.getMessage());
                        ParseObject friendRequest = new ParseObject("FriendRequest");
                        friendRequest.put("requestFrom", currentUser.getUsername());
                        friendRequest.put("requestTo", add_friend_username);
                        friendRequest.put("status", "pending");
                        friendRequest.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    Context context = getApplicationContext();
                                    Toast toast = Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                                else{
                                    Context context = getApplicationContext();
                                    Toast toast = Toast.makeText(context, "Request Failed", Toast.LENGTH_SHORT);
                                    Log.d("Application", e.getMessage());
                                    toast.show();
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


}
