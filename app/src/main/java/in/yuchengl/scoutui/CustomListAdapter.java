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
import android.widget.Button;

class CustomListAdapter extends ArrayAdapter<ListItem> {

    private Context c;
    private int logo1;
    private int logo2;

  CustomListAdapter(Context context, ListItem[] users) {
      super(context, R.layout.list_item_layout, users);
      this.c = context;
      this.logo1 = this.c.getResources().getIdentifier("scoutlogo", "drawable", "in.yuchengl.scoutui");
      this.logo2 = this.c.getResources().getIdentifier("scoutlogo2", "drawable", "in.yuchengl.scoutui");

  }

    @Override
    public View getView(int position, View convertView, ViewGroup parent ){
        LayoutInflater listInflater = LayoutInflater.from(getContext());
        View customView = listInflater.inflate(R.layout.list_item_layout, parent, false);

        String userArrayItemName = getItem(position).mName;
        String userArrayItemImage = getItem(position).mImage;
        Boolean userArrayItemStatus = getItem(position).mLive;

        TextView userName = (TextView) customView.findViewById(R.id.displayName);
        ImageView avatar = (ImageView) customView.findViewById(R.id.avi);


        userName.setText(userArrayItemName);
        avatar.setImageResource(this.logo2);

        return customView;
    }
}
