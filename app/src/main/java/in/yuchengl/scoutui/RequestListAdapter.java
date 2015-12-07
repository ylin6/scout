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

/**
 * Created by Yucheng on 12/5/15.
 */
public class RequestListAdapter extends ArrayAdapter<FriendsListItem> {

    private Context c;
    private int logo1;
    private int logo2;

    RequestListAdapter(Context context, FriendsListItem[] users) {
        super(context, R.layout.addfriends_list_item_layout, users);
        this.c = context;
        this.logo1 = this.c.getResources().getIdentifier("scoutlogo", "drawable", "in.yuchengl.scoutui");
        this.logo2 = this.c.getResources().getIdentifier("scoutlogo2", "drawable", "in.yuchengl.scoutui");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent ){
        LayoutInflater listInflater = LayoutInflater.from(getContext());
        View customView = listInflater.inflate(R.layout.addfriends_list_item_layout, parent, false);

        final String userArrayItemName = getItem(position).getName();
        /*
        String userArrayItemImage = getItem(position).mImage;
        Boolean userArrayItemStatus = getItem(position).mLive;*/

        final TextView userName = (TextView) customView.findViewById(R.id.displayName);
        ImageView avatar = (ImageView) customView.findViewById(R.id.avi);
        Button acceptFriendBtn = (Button) customView.findViewById(R.id.acceptFriendButton);
        Button denyFriendBtn = (Button) customView.findViewById(R.id.denyFriendButton);
        acceptFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Approve", userArrayItemName);
            }
        });

        denyFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Deny", userArrayItemName);
            }
        });




        userName.setText(userArrayItemName);
        avatar.setImageResource(this.logo2);

        return customView;

    }


}
