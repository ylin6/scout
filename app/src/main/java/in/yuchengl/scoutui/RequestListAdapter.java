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

import java.util.ArrayList;

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
    public View getView(int position, View convertView, ViewGroup parent ){
        LayoutInflater listInflater = LayoutInflater.from(getContext());
        View customView = listInflater.inflate(R.layout.addfriends_list_item_layout, parent, false);
        final TextView userName = (TextView) customView.findViewById(R.id.displayName);
        Button acceptFriendBtn = (Button) customView.findViewById(R.id.acceptFriendButton);
        Button denyFriendBtn = (Button) customView.findViewById(R.id.denyFriendButton);

        acceptFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        denyFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        userName.setText(getItem(position).getInviterName());
        return customView;

    }


}
