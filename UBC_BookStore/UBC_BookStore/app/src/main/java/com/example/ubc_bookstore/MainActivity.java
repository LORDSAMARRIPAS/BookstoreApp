package com.example.ubc_bookstore;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS = 100;

    EditText searchText;
    ImageButton btnSearchBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchText = findViewById(R.id.actvSearchText);
        btnSearchBook = findViewById(R.id.ibtnSearch);
        checkForProductAvailability();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // You can use notifications
            } else {
                // You cannot use notifications, request the permission
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // The permission is granted, you can use notifications
            } else {
                // The permission is denied, you should disable the functionality that depends on this permission
            }
        }
    }

    public void SearchBook(View view){
        Log.i("MyLog", "onClick method SearchBook");         // Create an Intent with one parameter and open "BookSearchView"

        Intent intent = new Intent(this, BookSearchView.class);
        intent.putExtra("searchText",searchText.getText().toString().trim());
        startActivity(intent);
    }

    // Method to display a Not implemented yet message
    public void NotImplementedYet(View view){
        DisplayMessageToScreen("This has not been implemented yet.");
    }

    // Simple method to display a Toast Message
    public void DisplayMessageToScreen(String msg) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), msg, duration);
        toast.show();
    }
    private void checkForProductAvailability() {
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String title = entry.getKey();
            if ((Boolean) entry.getValue()) {
                // Set up Firebase listener for the product
                listenForStockChanges(title);
            }
        }
    }



     private void listenForStockChanges(String title) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BOOK").child(title);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Book book = dataSnapshot.getValue(Book.class);
                    if (book != null && (book.getQtyNew() > 0 || book.getQtyUsed() > 0)) {
                        // Check if previously out of stock
                        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
                        if (sharedPreferences.getBoolean(book.getTitle(), false)) {
                            Toast.makeText(MainActivity.this, book.getTitle() + " available to buy", Toast.LENGTH_SHORT).show();
                            // Reset the notification flag
                            saveNotificationFlag(book.getTitle(), false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Firebase error: " + databaseError.getMessage());
            }
        });
    }


    private void saveNotificationFlag(String title, boolean flag) {
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(title, flag);
        editor.apply();
    }


}