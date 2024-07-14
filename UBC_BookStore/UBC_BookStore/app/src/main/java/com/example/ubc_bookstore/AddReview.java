package com.example.ubc_bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AddReview extends AppCompatActivity {
    RatingBar ratingbar;
    TextView ratingtv;
    EditText reviewtv;
    TextView checktv;
    TextView productnametv;
   // ImageView prodsmallimg;
    String userreview;
    float userrating;
    String datestamp;
    String productid;
    String productname;
    FirebaseDatabase database;
    DatabaseReference reviewRef;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        Intent intent = getIntent();
        productid = intent.getStringExtra("productid");
        productname = intent.getStringExtra("productname");
        username = intent.getStringExtra("username");
        initializeAll();
    }


    public void submitReview(View v) {
        //StringBuilder reviewtxt = new StringBuilder();
        datestamp = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
        // reviewtxt.append(timeStamp + " ");
        userrating = ratingbar.getRating();
        userreview = reviewtv.getText().toString();
        //validity checks, check if they have a rating or review first
        if(userrating == 0) {
            showToast("Select a rating to submit");
        } else {
            //  reviewtxt.append("Rating : " + userrating);
            //  reviewtxt.append(" " + userreview);
            //  checktv.setText(reviewtxt);
            //ratingbar2.setRating(userrating);
            savetoDatabase();
        }

    }

    public void cancelReview(View v) {
        returntoProductPage();
    }

    public void savetoDatabase() {
        reviewRef.child(productid).child(username).child("date").setValue(datestamp);
        reviewRef.child(productid).child(username).child("reviewtxt").setValue(userreview);
        reviewRef.child(productid).child(username).child("rating").setValue(userrating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("Your review was successfully saved!");
                        returntoProductPage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                        showToast("Sorry something went wrong. Try submitting again.");
                    }
                });


    }

    public void showToast(String msg) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }

    public void returntoProductPage () {
        finish();
    }



    public void initializeAll() {
        ratingtv = findViewById(R.id.ratingText);
        reviewtv = findViewById(R.id.reviewbox2);
        productnametv = findViewById(R.id.productnamevw);
        checktv = findViewById(R.id.checktv);
       // prodsmallimg = findViewById(R.id.prodsmallimg);

        userreview = "";
        userrating = 0;
        productnametv.setText(productname);
        //prodsmallimg.setImageResource(R.drawable.);

        database = FirebaseDatabase.getInstance();
        reviewRef =database.getReference("reviews");

        ratingbar = findViewById(R.id.ratingBar);
        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                userrating = rating;
                String ratingtxt = "";
                if (fromUser) {
                    if (userrating == 1) {
                        ratingtxt = "Terrible.";
                    } else if (userrating == 2) {
                        ratingtxt = "Didn't like it.";
                    } else if (userrating == 3) {
                        ratingtxt = "It was ok.";
                    } else if (userrating == 4) {
                        ratingtxt = "Liked it.";
                    } else {
                        ratingtxt = "Excellent.";
                    }
                    ratingtv.setText(ratingtxt);
                }

            }
        });

    }

}