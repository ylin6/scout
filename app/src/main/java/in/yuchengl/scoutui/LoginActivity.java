package in.yuchengl.scoutui;

import android.content.Context;
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
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        redirectIfLoggedIn(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Button signup = (Button) findViewById(R.id.signUp);
        final Button login = (Button) findViewById(R.id.login);
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
    }

    public void newUser(View view){
        Intent createNewUser = new Intent(this, SignUpActivity.class);
        startActivity(createNewUser);
    }

    public void signIn(View view){
        EditText usernameText = (EditText) findViewById(R.id.username);
        EditText passwordText = (EditText) findViewById(R.id.password);

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast.makeText(getApplicationContext(), "Logged in",
                            Toast.LENGTH_SHORT).show();
                    Intent nextIntent = new Intent(getApplicationContext(), ListActivity.class);
                    startActivity(nextIntent);
                } else {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void redirectIfLoggedIn(Context context) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
        } else {
            Intent redirectIntent = new Intent(context, ListActivity.class);
            context.startActivity(redirectIntent);
        }
    }

}
