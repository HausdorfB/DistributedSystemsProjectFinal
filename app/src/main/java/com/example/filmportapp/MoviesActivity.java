package com.example.filmportapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.filmportapp.utils.Constants;
//import com.example.filmportapp.utils.PreferenceUtils;



//Class used by trivia winner to chose which movie to watch
public class MoviesActivity extends AppCompatActivity {
    public static DatabaseHelper sqLiteHelper;
    ImageButton card_nature;
    ImageButton card_urban;
    ImageButton card_artsy;
    ImageButton card_spooky;
    String category;
    Button logout, submitButton, boards;
    public String hp = "jfdZd0yx05o";
    public String terminator = "1JPxRU4y19w";
    public String javamovie = "RnqAXuLZlaE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        final TextView seekBarValue = findViewById(R.id.distanceText);
        TextView welcomeMsg = findViewById(R.id.welcomeText);
        welcomeMsg.setText("CONGRATULATIONS " + Constants.KEY_EMAIL +"! You Won the Quiz!\nPick a movie to watch:");
        int userScore = Constants.KEY_MYSCORE;
        seekBarValue.setText("Your Score: " + userScore);
       // String username = PreferenceUtils.getUsername(this);
       // welcomeMsg.setText("Welcome to Film Port, " + username + ".\nYOU ARE THE QUIZ WINNER!\n Pick a Movie to watch");


       // submitButton = findViewById(R.id.button_submit);
      //  logout = findViewById(R.id.Logout_btn);
      //  boards = findViewById(R.id.button_leaderboard);
        card_nature = findViewById(R.id.ib_terminator);
        card_urban = findViewById(R.id.ib_java);
        card_artsy = findViewById(R.id.ib_artsy);
        card_spooky = findViewById(R.id.ib_hp);

        card_nature.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MoviesActivity.this, HostActivity.class);
                intent.putExtra("MOVIE_ID", terminator);
                startActivity(intent);
                //category = "nature";
            }
        });

        card_urban.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //category = "urban";
                Intent intent = new Intent(MoviesActivity.this, HostActivity.class);
                intent.putExtra("MOVIE_ID", javamovie);
                startActivity(intent);

            }
        });

        card_artsy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                category = "artsy";
            }
        });

        card_spooky.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //category = "spooky";
                Intent intent = new Intent(MoviesActivity.this, HostActivity.class);
                intent.putExtra("MOVIE_ID", hp);
                startActivity(intent);
            }
        });

    }

}

        //button will available for trivia winner be used to pass movie id and obtain host status

//        submitButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(), AdventureMarkerMap.class);
//                intent.putExtra("ID", distance);
//                if (!(category.equals("null")))
//                startActivity(intent); // dont change pages unless a category is selected
//            }
//        });

        /*
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.savePassword(null, cardViewMenu.this);
                PreferenceUtils.saveUsername(null, cardViewMenu.this);
                Intent intent = new Intent(cardViewMenu.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        boards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cardViewMenu.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });


    }
*/