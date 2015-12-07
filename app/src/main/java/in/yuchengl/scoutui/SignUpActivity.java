package in.yuchengl.scoutui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void SignUp(View view) {
        EditText usernameText = (EditText) findViewById(R.id.username);
        EditText passwordText = (EditText) findViewById(R.id.password);
        EditText confirmText = (EditText) findViewById(R.id.confirmpassword);

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String confirm = confirmText.getText().toString();

        if ((password.equals(confirm) && password.length() > 0) && (username.length() > 0)) {
            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Signed up",
                                Toast.LENGTH_SHORT).show();
                        Intent nextIntent = new Intent(getApplicationContext(), FriendListActivity.class);
                        startActivity(nextIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage().toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Passwords must match, and all fields must not be empty",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
