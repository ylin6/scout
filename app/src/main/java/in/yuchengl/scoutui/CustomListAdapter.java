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

class CustomListAdapter extends ArrayAdapter<FriendsListItem> {

    private Context mContext;
    private int mLogo1;
    private int mLogo2;

  CustomListAdapter(Context context, ArrayList<FriendsListItem> friendsList) {
      super(context, R.layout.list_item_layout, friendsList);
      mContext = context;
      mLogo1 = mContext.getResources().getIdentifier("scoutlogo",
              "drawable", "in.yuchengl.scoutui");
      mLogo2 = mContext.getResources().getIdentifier("scoutlogo2",
              "drawable", "in.yuchengl.scoutui");

  }

    @Override
    public View getView(int position, View convertView, ViewGroup parent ){
        LayoutInflater listInflater = LayoutInflater.from(getContext());
        View customView = listInflater.inflate(R.layout.list_item_layout, parent, false);

        String userArrayItemName = getItem(position).getName();

        TextView userName = (TextView) customView.findViewById(R.id.displayName);
        ImageView avatar = (ImageView) customView.findViewById(R.id.avi);

        userName.setText(userArrayItemName);
        avatar.setImageResource(mLogo2);

        return customView;

    }
}
