package com.example.ubc_bookstore;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Locale;

public class ItemOptions_Quantity_addCartScreen extends AppCompatActivity {
    private ImageButton addmorequantity, imageButtonLessQuantity, buttonaddcart ,imageButtonCancel2;
    private ImageView ivBookCover3;
    private EditText textViewQtyNew2; // Renamed from textiewQtyNew2 to textViewQtyNew2 for consistency
    private CheckBox checkBox;
    private TextView textViewPriceNew2, textViewTitle3;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemoptions_quantity_addcartscreen);

        addmorequantity = findViewById(R.id.addmorequantity);
        imageButtonLessQuantity = findViewById(R.id.imageButtonLessQuantity);
        textViewQtyNew2 = findViewById(R.id.textViewQtyNew2); // Correct ID for quantity EditText
        checkBox = findViewById(R.id.checkBox);
        textViewPriceNew2 = findViewById(R.id.textViewPriceNew2);
        textViewTitle3 = findViewById(R.id.textViewTitle3);
        imageButtonCancel2 = findViewById(R.id.imageButtonCancel2);
        button2 = findViewById(R.id.button2); // Make sure this ID matches your layout XML
        ivBookCover3 = findViewById(R.id.ivBookCover3); // replace with your actual ImageView ID
        // Retrieve data from Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String imageURL = intent.getStringExtra("imageURL");
        Glide.with(this)
                .load(imageURL)
                .into(ivBookCover3);
        double priceNew = intent.getDoubleExtra("priceNew", 0);
        double priceUsed = intent.getDoubleExtra("priceUsed", 0);
        int qtyNew = intent.getIntExtra("qtyNew", 0);
        int qtyUsed = intent.getIntExtra("qtyUsed", 0);
        boolean isUsed = intent.getBooleanExtra("isUsed", false);
        Toast.makeText(this, "Your order has been sent to the cart", Toast.LENGTH_SHORT).show();

        textViewTitle3.setText(title);
        if (isUsed) {
            textViewPriceNew2.setText(String.format(Locale.US, "%.2f", priceUsed));
            textViewQtyNew2.setText(String.valueOf(qtyUsed));
        } else {
            textViewPriceNew2.setText(String.format(Locale.US, "%.2f", priceNew));
            textViewQtyNew2.setText(String.valueOf(qtyNew));
        }

        imageButtonCancel2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to send data to CartPageAndPaymentScreen
                sendOrderDetails();
                // Show toast message
                Toast.makeText(ItemOptions_Quantity_addCartScreen.this, "Your order has been sent to the cart", Toast.LENGTH_SHORT).show();
            }
        });



        // Set initial quantity to 1
        textViewQtyNew2.setText("1");

        // Handle checkbox changes
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                textViewPriceNew2.setText(String.format(Locale.US, "%.2f", priceUsed));
                textViewQtyNew2.setText("1"); // Start with a default quantity of 1
                textViewQtyNew2.setTag(qtyUsed); // Store the maximum used quantity
            } else {
                textViewPriceNew2.setText(String.format(Locale.US, "%.2f", priceNew));
                textViewQtyNew2.setText("1"); // Start with a default quantity of 1
                textViewQtyNew2.setTag(qtyNew); // Store the maximum new quantity
            }
        });

        // Ensure the checkbox reflects the 'isUsed' value from the intent
        checkBox.setChecked(isUsed);

        addmorequantity.setOnClickListener(v -> {
            try {
                int currentQty = Integer.parseInt(textViewQtyNew2.getText().toString());
                Object tag = textViewQtyNew2.getTag();
                int maxQty = (tag != null) ? (int) tag : Integer.MAX_VALUE; // Set a default max if tag is null

                if (currentQty < maxQty) {
                    textViewQtyNew2.setText(String.valueOf(currentQty + 1));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // Handle error - possibly show a message to the user
            }
        });


        imageButtonLessQuantity.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(textViewQtyNew2.getText().toString());
            if (currentQty > 1) {
                textViewQtyNew2.setText(String.valueOf(currentQty - 1));
            }
        });

        // This button should navigate to the cart page
        ImageButton buttonGoToCart = findViewById(R.id.buttonaddcart); // Assuming you have this ImageButton in your XML
        buttonGoToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCartPageAndPaymentScreen();
            }
        });


    }

    private void navigateToCartPageAndPaymentScreen(){
        // Retrieve the details from your current screen
        String title = textViewTitle3.getText().toString();
        double price = Double.parseDouble(textViewPriceNew2.getText().toString());
        int quantity = Integer.parseInt(textViewQtyNew2.getText().toString());
        boolean isUsed = checkBox.isChecked();
        String imageURL = getIntent().getStringExtra("imageURL");

        // Create an Intent to start the CartPageAndPaymentScreen
        Intent intent = new Intent(ItemOptions_Quantity_addCartScreen.this, CartPageAndPaymentScreen.class);
        // Add all the necessary information to the Intent
        intent.putExtra("title", title);
        intent.putExtra("price", price);
        intent.putExtra("quantity", quantity);
        intent.putExtra("isUsed", isUsed);
        intent.putExtra("imageURL", imageURL);
        intent.putExtra("condition", isUsed ? "Used" : "New");
        // Start the CartPageAndPaymentScreen activity
        startActivity(intent);
    }
    private void sendOrderDetails() {
        // Retrieve the details from your current screen
        String title = textViewTitle3.getText().toString();
        double price = Double.parseDouble(textViewPriceNew2.getText().toString());
        int quantity = Integer.parseInt(textViewQtyNew2.getText().toString());
        boolean isUsed = checkBox.isChecked();

        // Instead of creating an Intent to navigate,
        // save the details so they can be accessed later by CartPageAndPaymentScreen
        SharedPreferences sharedPreferences = getSharedPreferences("CartPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("title", title);
        editor.putFloat("price", (float) price); // SharedPreferences does not support double, hence the cast to float
        editor.putInt("quantity", quantity);
        editor.putBoolean("isUsed", isUsed);
        editor.apply(); // Apply the changes asynchronously
    }
}
