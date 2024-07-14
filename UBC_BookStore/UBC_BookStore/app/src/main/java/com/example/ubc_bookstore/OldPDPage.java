package com.example.ubc_bookstore;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OldPDPage extends AppCompatActivity {

    String productid;
    String productname;
    String username;
    Button addreviewbttn;
    TextView checkFBvals;
    RatingBar productratingbar;
    RecyclerView recyclerView;
    TextView productratingtxt;
    TextView noreviewstxt;
    ReviewCardListAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference reviewRef;
    private ValueEventListener postListener;
    boolean productReviewsExist;
    boolean thisUserReviewExists;

    //long recordcount;
    ArrayList<ReviewCardData> reviewCardData;
    double product_calculated_rating;
    //StringBuilder res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_page);
        initializeReviewFeatures();
    }

    public void showReviews() {
        if (productReviewsExist) {
            adapter = new ReviewCardListAdapter(reviewCardData);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            noreviewstxt.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);

        } else {
            noreviewstxt.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

    }

    public void showProductRating() {
        if(productReviewsExist) {
            productratingbar.setRating((float) product_calculated_rating);
            productratingtxt.setText(String.format("%.1f", product_calculated_rating));
        } else {
            productratingtxt.setText("No ratings yet.");
        }

    }

    public void handleAddReviewFeature() {

        if (thisUserReviewExists) {
            addreviewbttn.setText("Edit your review");
        } else {
            addreviewbttn.setText("Add a review");
        }
        checkFBvals.setText("Current User: " + username);

    }

    public void handleReview(View v) {
        Class cls = AddReview.class;
        if (thisUserReviewExists) {
            cls = EditReview.class;
        }
        Intent intent = new Intent(this, cls);
        intent.putExtra("productid", productid);
        intent.putExtra("productname", productname);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void initializeReviewFeatures() {
        addreviewbttn = findViewById(R.id.addreviewbttn2);
        productratingbar = findViewById(R.id.productratingbar);
        noreviewstxt = findViewById(R.id.noreviewstxt);
        productratingtxt = findViewById(R.id.productratingtxt);
        checkFBvals = findViewById(R.id.checkFB);

        thisUserReviewExists = false;
        productReviewsExist = false;
        product_calculated_rating = 0;
        reviewCardData = new ArrayList<ReviewCardData>();

        int num = (int)(Math.random() * 10)%7;
        username = "TestUser" + num;
        productid = "3";
        productname = "Cozy UBC Sweater";
        //  recordcount = 0;

        database = FirebaseDatabase.getInstance();
        reviewRef = database.getReference("reviews");
        // res = new StringBuilder();
        recyclerView = (RecyclerView) findViewById(R.id.recyclervw);

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productReviewsExist = dataSnapshot.hasChild(productid);

                if (productReviewsExist) {
                    // recordcount = dataSnapshot.child(productid).getChildrenCount();
                    thisUserReviewExists = dataSnapshot.child(productid).hasChild(username);
                    int cnt = 0;
                    double sum = 0;
                    reviewCardData.clear();
                    for (DataSnapshot child : dataSnapshot.child(productid).getChildren()) {
                        //get the data values
                        String reviewername = (String) child.getKey();
                        String reviewdate = (String) child.child("date").getValue();
                        long reviewrating = 0;
                        if (child.child("rating").getValue() != null) {
                            reviewrating = (long) child.child("rating").getValue();
                        }
                        String reviewtext = (String) child.child("reviewtxt").getValue();
                        //put the data vals in the arraylist for the recyclerview
                        String header = reviewername + "\nReviewed on " + reviewdate;
                        reviewCardData.add(new ReviewCardData(header, reviewtext, reviewrating));
                        //do the logic to calculate the product rating
                        sum = sum + reviewrating;
                        cnt++;
                        // res.append(someval);
                        //res.append(uname + " " + rvtxt + " " + urating);
                    }
                    product_calculated_rating = sum / cnt;
                }
                showProductRating();
                showReviews();
                handleAddReviewFeature();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        reviewRef.addValueEventListener(postListener);


    }

}
