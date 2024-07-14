package com.example.ubc_bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookSearchView extends AppCompatActivity  {
    // creating a variable for our Database
    // Reference for Firebase.
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    final String BOOK_NODE = "BOOK";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Book> bookList = new ArrayList<>();
    private String searchString = "";
    private AutoCompleteTextView actvSearchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search_view);

        // setup the resource ids
        recyclerView = findViewById(R.id.rvBookList);

        // Get the intent from the calling view
        Intent intent = getIntent();
        searchString = intent.getStringExtra("searchText");

        // Update the search text field on the view with the passed
        // in searchString.
        actvSearchString = findViewById(R.id.actvSearchText);
        if (!searchString.isEmpty())
            actvSearchString.setText(searchString);

        // Setup the Firebase database instance and connection
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("").child(BOOK_NODE);

        // read the Book table from Firebase database
        readBookListFromFirebase(this);
    }
    // Method calls readBookListFromFirebase
    public void QueryDatabase(View view){
        readBookListFromFirebase(this);
    }

    //
    // This method connects to the Firebase Realtime Database and retrieves
    // all rows from the Book table into a list bookList, and then filters out the books
    // according to the seachString.  If searchString is blank it the bookList
    // will contain all rows from the Book table.
    public void readBookListFromFirebase(Context context) {
        Log.i("MyLog", "Reading the Book table from Firebase");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookList.clear();  //empty out the list
                // Get the count of rows from the Book Table in Firebase
                int count = (int) dataSnapshot.getChildrenCount();
                if (count > 0) {
                    Log.i("MyLog", "Number of books = " + count);
                    for (DataSnapshot bookDataSnapshot : dataSnapshot.getChildren()) {
                        // Add the Book Record to the ArrayList tListbook
                        bookList.add(bookDataSnapshot.getValue(Book.class));
                    }
                } else {
                    // No Book Record in Firebase
                    Log.i("MyLog", "Firebase database is empty");
                    DisplayMessageToScreen("Firebase database is empty.");
                }

                // Get the searchString from the view
                searchString = actvSearchString.getText().toString().trim();
                // If the searchString is not blank then filter out the list
                // using regular expression to see if the search pattern exists
                // in the Book searchString.
                if(!searchString.isEmpty()) {

                    if (searchString.length() > 0) {
                        Log.i("MyLog", "Search string = " + searchString);
                        List<Book> filtList = bookList.stream().filter(x -> x.getSearchString()
                                        .matches("(?i).*" + searchString + ".*"))
                                .collect(Collectors.toList());
                        bookList = filtList;
                    }
                }
                // Setup the recycler view to list the books found
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);
                mAdapter = new RecycleViewAdapter(bookList, context);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                DisplayMessageToScreen("Fail to add data " + error);
            }

        });

    }

    // Method to return back to the main view
    public void returnToMainScreen(View view) {
        Log.i("MyLog", "Returning to Main View.");
        finish();
    }
    // Method to display a simple Toast Message
    public void DisplayMessageToScreen(String msg) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), msg, duration);
        toast.show();
    }



}