package com.example.ubc_bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.view.CardMultilineWidget;

public class CartPageAndPaymentScreen extends AppCompatActivity {
    Button payment;

    String CustomerId;
    String EphericalKey;
    String ClientSecret;
    PaymentSheet paymentSheet;
    ImageView imageViewBook;

    TextView totalCost, textViewBookTitle;
    CardMultilineWidget cardMultilineWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_page_and_payment_screen);
        payment = findViewById(R.id.payButton);
        TextView textViewCondition = findViewById(R.id.textViewCondition); // Assuming you have a TextView for condition
        TextView textViewQuantity = findViewById(R.id.textViewQuantity);
        ImageView bookImageView = findViewById(R.id.imageViewBook);
        cardMultilineWidget = findViewById(R.id.cardInputWidget);
        Locale bcLocale= new Locale("en","CA");
        Currency cad=Currency.getInstance("CAD");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(bcLocale);



        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        double price = intent.getDoubleExtra("price", 0);
        int quantity = intent.getIntExtra("quantity", 1);
        String condition = intent.getStringExtra("condition");
        boolean isUsed = intent.getBooleanExtra("isUsed", false);
        String imageURL = intent.getStringExtra("imageURL");

        TextView textViewBookTitle = findViewById(R.id.textViewBookTitle); // replace with your actual TextView ID
        textViewBookTitle.setText(title);


        // Set the text for the condition and quantity
        textViewCondition.setText(condition);
        textViewQuantity.setText(String.valueOf(quantity));
        // loading an image using Glide
        Glide.with(this).load(imageURL).into(bookImageView);


        totalCost = findViewById(R.id.TotalPrice);
        double total = quantity * price; // calculation

        totalCost.setText("Total Price "+currencyFormatter.format(total)); // Set the calculation  as text
        Toast.makeText(this, "Book added to cart: " + title, Toast.LENGTH_SHORT).show();

    }

    public void payForItem(View view) {
        totalCost=findViewById(R.id.TotalPrice);
        String CVC= String.valueOf(cardMultilineWidget.getCvcEditText().getText());
        String Number= String.valueOf(cardMultilineWidget.getCardNumberEditText().getText());
        String ExpirationDate=String.valueOf(cardMultilineWidget.getExpiryDateEditText().getText());
       // String CardType=String.valueOf(cardMultilineWidget.getBrand());
        String moneyToBeCharged=totalCost.getText().toString();

        if (CVC.length()==0 & Number.length()==0 & ExpirationDate.length()==0){
            Toast.makeText(this,"Error, please Enter the Correct Info", Toast.LENGTH_SHORT).show();


        }

        if(Number.length()>=16 &ExpirationDate.length()>4 & CVC.length()<3){
            Toast.makeText(this,"Error, please enter your cards CVC Code", Toast.LENGTH_SHORT).show();

        }


        if(Number.length()<16 &ExpirationDate.length()==4 & CVC.length()==4){
            Toast.makeText(this,"Error, please enter a vaild card Number", Toast.LENGTH_SHORT).show();

        }

        if(ExpirationDate.length()<4 & Number.length()==16 & CVC.length()==3){
            Toast.makeText(this,"Error,No Expiration Date found please enter the cards Expiration date", Toast.LENGTH_SHORT).show();

        }

        if(Number.length()==0 & ExpirationDate.length()>=4 & CVC.length()>=3 ){
            System.out.println("HIT_3");
            Toast.makeText(this,"Error, No Card Number is found, please enter your card number to continue", Toast.LENGTH_SHORT).show();

        }
        if(Number.length()==0 & CVC.length()==0){
            Toast.makeText(this,"Error, No Card Number or CVC Number  found, please enter the required info", Toast.LENGTH_SHORT).show();

        }
        if(Number.length()==0 & CVC.length()>=3 & ExpirationDate.length()==0){
            Toast.makeText(this,"Error, No Card Number or Expiry date found, please enter the required info", Toast.LENGTH_SHORT).show();

        }
        if(Number.length()>=16 & CVC.length()==0 & ExpirationDate.length()==0){
            Toast.makeText(this,"Error, CVC number or Expiry date found, please enter the required info", Toast.LENGTH_SHORT).show();

        }







        if(CVC.length()>=3 & Number.length()>=16 & ExpirationDate.length()>=4){
            Intent SuccessScreen = new Intent(this, PaymentSucessScreen.class);
            Bundle bundle = new Bundle();
            bundle.putString("CVC_Number",CVC);
            bundle.putString("Card_Number",Number);
            // bundle.putString("Card_type",CardType);
            bundle.putString("money",moneyToBeCharged);
            SuccessScreen.putExtras(bundle);
            startActivity(SuccessScreen);

        }






    }
    public void goBackToMainPage(View view) {
        Intent productPageIntent = new Intent(this, MainActivity.class);
        productPageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(productPageIntent);
        finish(); // Ensure this activity is closed and removed from the back stack
    }
}