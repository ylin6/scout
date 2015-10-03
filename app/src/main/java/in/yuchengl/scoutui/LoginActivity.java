package in.yuchengl.scoutui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.Parse;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Button signup = (Button) findViewById(R.id.signUp);
        final Button login = (Button) findViewById(R.id.login);
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "texSbr87THubJtJbjminaY9SPtAbX4wB0RNac5xJ",
                "RBDRmL8yMw4cBBG1Vm6WUOCjIhhsQTgIh7YS7o1o");
    }

    public void newUser(View view){
        Intent createNewUser = new Intent(this, SignUpActivity.class);
        startActivity(createNewUser);
    }

    public void signIn(View view){
        //TODO CHECK FOR AUTHORIZATION
        Intent signInIntent = new Intent(this, ListActivity.class);
        startActivity(signInIntent);
    }

}
