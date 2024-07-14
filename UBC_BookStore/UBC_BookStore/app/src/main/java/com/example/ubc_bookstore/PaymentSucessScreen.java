package com.example.ubc_bookstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;

public class PaymentSucessScreen extends AppCompatActivity {
    TextView Number,totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_sucess_screen);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String CVC = bundle.getString("CVC_Number");
        String Card_Number = bundle.getString("Card_Number");
        String totalMoneyCharged= bundle.getString("money");
        String[] MoneyTobeCharged=totalMoneyCharged.split("\\$");
        String MoneyTobeChargedToCard=MoneyTobeCharged[1];
        Number=findViewById(R.id.CardNumber);
        totalAmount=findViewById(R.id.ChargedAmount);

        Number.setText(Card_Number);
        totalAmount.setText("$"+MoneyTobeChargedToCard);




    }



    public void goBack(View view){
        //Somebody can put the goBack intent here
        Intent MainScreen = new Intent(this,MainActivity.class);
        startActivity(MainScreen);




    }
    }
