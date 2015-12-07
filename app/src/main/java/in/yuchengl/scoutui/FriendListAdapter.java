package in.yuchengl.scoutui;

/**
 * Created by Yucheng on 10/3/15.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class FriendListAdapter extends ArrayAdapter<FriendListItem> {

    private Context mContext;
    private int mLogoOnline;
    private int mLogoOffline;

  FriendListAdapter(Context context, ArrayList<FriendListItem> friendsList) {
      super(context, R.layout.list_item_layout, friendsList);
      mContext = context;
      mLogoOnline = mContext.getResources().getIdentifier("scoutlogo",
              "drawable", "in.yuchengl.scoutui");
      mLogoOffline = mContext.getResources().getIdentifier("scoutlogo2",
              "drawable", "in.yuchengl.scoutui");

  }

    @Override
    public View getView(int position, View convertView, ViewGroup parent ){
        LayoutInflater listInflater = LayoutInflater.from(getContext());
        View customView = listInflater.inflate(R.layout.list_item_layout, parent, false);

        String name = getItem(position).getName();
        Boolean live = getItem(position).getLive();

        TextView userName = (TextView) customView.findViewById(R.id.displayName);
        ImageView avatar = (ImageView) customView.findViewById(R.id.avi);

        userName.setText(name);
        if (live) {
            avatar.setImageResource(mLogoOnline);
        } else {
            avatar.setImageResource(mLogoOffline);
        }

        return customView;

    }
}
