package com.example.ubc_bookstore;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "my_notification_channel";

    // Method to schedule a stock notification
    public void scheduleStockNotification(String bookTitle) {
        Log.d(TAG, "Scheduling stock notification for: " + bookTitle);

        // Create a message to send via FCM
        String message = "Stock Update: " + bookTitle + " is now available!";
        sendNotificationToFCMServer(bookTitle, message);
    }

    private void sendNotificationToFCMServer(String title, String message) {
        // Your server's endpoint URL
        String url = "https://yourserver.com/api/send-fcm-message";  // Change server

        // Prepare the JSON payload
        JSONObject payload = new JSONObject();
        try {
            payload.put("title", title);
            payload.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Create the request body
        RequestBody requestBody = RequestBody.create(payload.toString(), MediaType.parse("application/json; charset=utf-8"));

        // Build the request
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // Send the request using OkHttpClient or any other HTTP client
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to send notification request", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Notification request sent successfully");
                } else {
                    Log.e(TAG, "Notification request failed: " + response);
                }
            }
        });
    }

    //  method for updating book stock
    public void updateBookStock(String bookTitle, int newStock) {
        if (newStock > 0) {
            saveNotificationFlag(bookTitle, true);
        }
    }

    // Method to save notification flags
    private void saveNotificationFlag(String title, boolean flag){
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(title, flag);
        editor.apply();

        if (flag) {
            scheduleStockNotification(title);
        }
    }

    // Overridden method for handling received messages
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    // Method to show a notification
    @SuppressLint("MissingPermission")
    private void showNotification(String title, String body) {
        Intent intent = new Intent(this, ProductView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));

        createNotificationChannel();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notificationBuilder.build());
    }

    // Method to create a notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_notification_channel_name);
            String description = getString(R.string.default_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Overridden method for handling new token generation
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    // Method to send registration token to server
    private void sendRegistrationToServer(String token) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://myserver.com/api/register-token";  // Change server
        RequestBody body = RequestBody.create("{\"token\":\"" + token + "\"}", MediaType.parse("application/json; charset=utf-8"));
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Token registration failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Token registered successfully");
                } else {
                    Log.e(TAG, "Token registration failed: " + response);
                }
            }
        });
    }
}


