package be.uclouvain.lsinf1225.groupel31.wishlist.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import be.uclouvain.lsinf1225.groupel31.wishlist.R;
import be.uclouvain.lsinf1225.groupel31.wishlist.Classes.User;

public class SignIn extends AppCompatActivity {
    private EditText mail_in;
    private EditText pass_in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        this.mail_in = findViewById(R.id.mail_in);
        this.pass_in = findViewById(R.id.pass_in);

        //Text with action "sign up instead" -> go to sign up activity
        TextView sign_up = findViewById(R.id.go_sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next_layout = new Intent(getApplicationContext(), SignUp.class);
                startActivity(next_layout);
                finish();
            }
        });


        Button sign_in = findViewById(R.id.sign_in_button);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get EditText inputs
                String mail = mail_in.getText().toString();
                String password = pass_in.getText().toString();

                //set error text at null and size at 0
                TextView error = findViewById(R.id.not_register_error);
                error.setText(null);
                error.setHeight(0);

                //Create empty user
                User user = new User();

                //give application context and create a link between user and db
                user.setDb(getApplicationContext());
                if (user.existingUser(mail)){
                    //set user's attributes with db ref
                    user.signIn(mail);
                    if(user.matchingPassAndMail(password, mail)){
                        //go to next layout -> base activity
                        Toast.makeText(getApplicationContext(), "Log In as "
                                + user.getPseudo(), Toast.LENGTH_SHORT).show();

                        Intent next_layout = new Intent(getApplicationContext(), Base.class);
                        startActivity(next_layout);
                        finish();
                    }else{
                        //error password check
                        user.logOut();
                        error.setText(R.string.pass_error);
                        error.setTextColor(getResources().getColor(R.color.Red, getTheme()));
                        error.setHeight(50);
                    }
                }else{
                    //error : mail not register
                    error.setTextColor(getResources().getColor(R.color.Red, getTheme()));
                    error.setText(R.string.not_register_error);
                    error.setHeight(50);
                }
            }
        });

    }
}
