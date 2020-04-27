package be.uclouvain.lsinf1225.groupel31.wishlist.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import be.uclouvain.lsinf1225.groupel31.wishlist.Classes.WishList;
import be.uclouvain.lsinf1225.groupel31.wishlist.R;
import be.uclouvain.lsinf1225.groupel31.wishlist.singleton.CurrentWishList;


public class NewWish extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wish);
        final WishList current_list = CurrentWishList.getInstance();
        final EditText name_in = findViewById(R.id.name_in);
        final EditText price_in = findViewById(R.id.price_in);
        final EditText market_in = findViewById(R.id.market_in);
        final EditText description_in = findViewById(R.id.description_in);

        //Button add wish action
        Button button = findViewById(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get editText inputs
                String name = name_in.getText().toString();
                double price = Double.parseDouble(price_in.getText().toString());
                String market = market_in.getText().toString();
                String description = description_in.getText().toString();

                //insert in db
                current_list.createWish(name, null, description, price, market);

                //go to next layout -> go to base activity
                Intent next_layout = new Intent(getApplicationContext(), Base.class);
                startActivity(next_layout);
                finish();
            }
        });

        //Circle profile picture action -> go to profile activity
        de.hdodenhof.circleimageview.CircleImageView profile_picture = findViewById(R.id.picture_profile);
        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nex_layout = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(nex_layout);
                finish();
            }
        });

        //Button back action -> go to base activity
        Button back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next_layout = new Intent(getApplicationContext(), Base.class);
                startActivity(next_layout);
                finish();
            }
        });

    }
}
