package in.yuchengl.scoutui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yucheng on 12/5/15.
 */
public class RequestListAdapter extends ArrayAdapter<RequestListItem> {

    private Context mContext;

    RequestListAdapter(Context context, ArrayList<RequestListItem> users) {
        super(context, R.layout.addfriends_list_item_layout, users);
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent ){
        LayoutInflater listInflater = LayoutInflater.from(getContext());
        View customView = listInflater.inflate(R.layout.addfriends_list_item_layout, parent, false);
        final TextView userName = (TextView) customView.findViewById(R.id.displayName);
        Button acceptFriendBtn = (Button) customView.findViewById(R.id.acceptFriendButton);
        Button denyFriendBtn = (Button) customView.findViewById(R.id.denyFriendButton);

        acceptFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rId = getItem(position).getId();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequest");
                query.whereEqualTo("objectId", rId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null){
                            final ParseObject request = list.get(0);
                            request.put("status", "accepted");
                            final String newFriendName = request.getString("requestFrom");
                            request.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                                        query.whereEqualTo("username", newFriendName);
                                        query.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> list, ParseException e) {
                                                if (e == null){
                                                    String newFriendId = list.get(0).getObjectId();
                                                    ParseUser user = ParseUser.getCurrentUser();
                                                    user.addUnique("Friends", newFriendId);
                                                    user.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null){
                                                                // Update Request List
                                                                remove(getItem(position));
                                                                notifyDataSetChanged();
                                                                Toast.makeText(getContext(), "Request Approved", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else{
                                                                Log.d("Application", e.getMessage());
                                                            }
                                                        }
                                                    });
                                                }

                                                else{
                                                    Log.d("Application", e.getMessage());
                                                }
                                            }
                                        });
                                    } else {
                                        Log.d("Application", e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                });
            }

        });

        denyFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rId = getItem(position).getId();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequest");
                query.whereEqualTo("objectId", rId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if(e == null){
                            ParseObject request = list.get(0);
                            request.put("status", "rejected");
                            request.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        // Update Request List
                                        remove(getItem(position));
                                        notifyDataSetChanged();
                                        Toast.makeText(getContext(), "Request Denied", Toast.LENGTH_SHORT).show();
                                    }

                                    else{
                                        Log.d("Application", e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        userName.setText(getItem(position).getInviterName());
        return customView;

    }


}
