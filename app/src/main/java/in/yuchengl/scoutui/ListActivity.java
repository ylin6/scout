package in.yuchengl.scoutui;

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
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        //
        Button startScoutingButton = (Button) findViewById(R.id.startScoutButton);


    }

    public void goToCamera(View view){
        Intent startScouting = new Intent(this, CameraActivity.class);
        startActivity(startScouting);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
