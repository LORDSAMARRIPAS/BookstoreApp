package com.example.ubc_bookstore;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductView extends AppCompatActivity {
    // creating a variable for our Database
    // Reference for Firebase.
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    final String BOOK_NODE = "BOOK";
    private TextView textView_Title2, textView_Author, textView_Stock,
            textView_Genre, textView_QtyNew, textView_QtyUsed,
            textView_PriceNew, textView_PriceUsed, textView_ISBN;
    private ImageButton imageButtonCancel;
    private Button button_AddToCart;

    private ImageView imagedView_BookCover2;
    private String isbn;
    private Book myBook;

    final Integer TEXT_GREEN = 0xFF7CFC00;
    final Integer TEXT_RED = 0xFFFF0000;
    String productid;
    String productname;
    String username;
    Button addreviewbttn;
   // TextView checkFBvals;
    RatingBar productratingbar;
    RecyclerView recyclerView;
    TextView productratingtxt;
    TextView noreviewstxt;
    ReviewCardListAdapter adapter;
   // FirebaseDatabase database;
    DatabaseReference reviewRef;
    private ValueEventListener postListener;
    boolean productReviewsExist;
    boolean thisUserReviewExists;

    ArrayList<ReviewCardData> reviewCardData;
    double product_calculated_rating;
    //StringBuilder res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_information_view);

        textView_Title2 = findViewById(R.id.textViewTitle2);
        textView_Author = findViewById(R.id.textViewAuthor);
        textView_ISBN = findViewById(R.id.textViewISBN);
        textView_Genre = findViewById(R.id.textViewGenre);
        textView_QtyNew = findViewById(R.id.textViewQtyNew);
        textView_QtyUsed = findViewById(R.id.textViewQtyUsed);
        textView_PriceNew = findViewById(R.id.textViewPriceNew);
        textView_PriceUsed = findViewById(R.id.textViewPriceUsed);
        textView_Stock = findViewById(R.id.textViewStock);
        button_AddToCart = findViewById(R.id.buttonAddToCart);
        imageButtonCancel = findViewById(R.id.imageButtonCancel);
        imagedView_BookCover2 = findViewById(R.id.ivBookCover2);


        // Get the intent from the calling view
        Intent intent = getIntent();
        isbn = intent.getStringExtra("isbn");

        // Setup the Firebase database instance and connection
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("").child(BOOK_NODE).child(isbn);
        readBookListFromFirebase();
        imageButtonCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button_AddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductView.this, ItemOptions_Quantity_addCartScreen.class);
                intent.putExtra("title", textView_Title2.getText().toString());
                intent.putExtra("priceNew", Double.parseDouble(textView_PriceNew.getText().toString()));
                intent.putExtra("priceUsed", Double.parseDouble(textView_PriceUsed.getText().toString()));
                intent.putExtra("imageURL", myBook.getImageURL()); // Assuming you have an imageURL in the myBook object
                intent.putExtra("qtyNew", Integer.parseInt(textView_QtyNew.getText().toString()));
                intent.putExtra("qtyUsed", Integer.parseInt(textView_QtyUsed.getText().toString()));
                startActivity(intent);
            }
        });


        initializeReviewFeatures();
    }

    // This method connects to the Firebase Realtime Database and retrieves
    // a single record from the table with matching ISBN.
    public void readBookListFromFirebase() {
        Log.i("MyLog", "Reading the Book table from Firebase");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if we have any records from the database
                if (dataSnapshot.exists()){
                    Log.i("MyLog", "Book found with isbn "+isbn);
                    myBook = dataSnapshot.getValue(Book.class);
                }
                else {
                    // No Student Record in Firebase
                    Log.i("MyLog", "Firebase database is empty");
                    DisplayMessageToScreen("Firebase database is empty.");
                }

                UpdateView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                DisplayMessageToScreen("Fail to add data " + error);
            }

        });

    }

    //
    // Method to Update the view with the book information
    //
    private void UpdateView() {
        textView_Title2.setText(myBook.getTitle());
        productname = myBook.getTitle();
        textView_Author.setText(myBook.getAuthor());
        productid = myBook.getISBN();
        textView_ISBN.setText(myBook.getISBN());
        textView_Genre.setText(myBook.getGenre());
        textView_PriceNew.setText(String.valueOf(myBook.getPriceNew()));
        textView_PriceUsed.setText(String.valueOf(myBook.getPriceUsed()));
        textView_QtyNew.setText(String.valueOf(myBook.getQtyNew()));
        textView_QtyUsed.setText(String.valueOf(myBook.getQtyUsed()));
        Glide.with(this).load(myBook.getImageURL()).into(imagedView_BookCover2 );
        // check if in stock
        int int_QtyNew = Integer.valueOf(myBook.getQtyNew());
        int int_QtyUsed = Integer.valueOf(myBook.getQtyUsed());

        if (int_QtyNew <= 0 && int_QtyUsed <= 0) {
            // Out of stock
            button_AddToCart.setText(getString(R.string.notify_me_when_available));
            textView_Stock.setText(getString(R.string.out_of_stock));
            textView_Stock.setTextColor(TEXT_RED);

            button_AddToCart.setOnClickListener(v -> {
                Toast.makeText(ProductView.this, "Notification set for " + myBook.getTitle(), Toast.LENGTH_SHORT).show();
                saveNotificationFlag(myBook.getTitle(), true);
            });
        } else {
            // In stock
            button_AddToCart.setText(getString(R.string.add_to_cart));
            textView_Stock.setText(getString(R.string.in_stock));
            textView_Stock.setTextColor(TEXT_GREEN);

            reviewRef.addValueEventListener(postListener);

            button_AddToCart.setOnClickListener(v -> {
                // Navigate to ItemOptions_Quantity_addCartScreen
                Intent intent = new Intent(ProductView.this, ItemOptions_Quantity_addCartScreen.class);
                intent.putExtra("title", myBook.getTitle());
                intent.putExtra("priceNew", myBook.getPriceNew());
                intent.putExtra("priceUsed", myBook.getPriceUsed());
                intent.putExtra("qtyNew", myBook.getQtyNew());
                intent.putExtra("qtyUsed", myBook.getQtyUsed());
                intent.putExtra("imageURL", myBook.getImageURL());
                startActivity(intent);
            });
        }
    }

    private void saveNotificationFlag(String title, boolean flag) {
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(title, flag);
        editor.apply();
    }


    // Method to display a simple Toast Message
    public void DisplayMessageToScreen(String msg) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), msg, duration);
        toast.show();
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
            addreviewbttn.setText("Click to Add Review");
        }
        //checkFBvals.setText("Current User: " + username);

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
        // checkFBvals = findViewById(R.id.checkFB);
        thisUserReviewExists = false;
        productReviewsExist = false;
        product_calculated_rating = 0;
        reviewCardData = new ArrayList<ReviewCardData>();
        int num = (int)(Math.random() * 10)%10;
        username = "TestUser" + num;
        // productid = myBook.getISBN();
        // productname = myBook.getTitle();
        //  recordcount = 0;
        //database = FirebaseDatabase.getInstance();
        reviewRef = firebaseDatabase.getReference("reviews");
        recyclerView = (RecyclerView) findViewById(R.id.recyclervw);
        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(productid != null) {
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

                        }
                        product_calculated_rating = sum / cnt;
                    }
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

        //reviewRef.addValueEventListener(postListener);


    }

}
