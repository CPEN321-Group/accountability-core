package com.cpen321group.accountability.mainscreen.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cpen321group.accountability.HomeScreenActivity;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.RetrofitAPI;
import com.google.android.material.color.DynamicColors;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.stripe.android.paymentsheet.*;
import com.stripe.android.PaymentConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubscriptionActivity extends AppCompatActivity {
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration customerConfig;
    // temporary server for testing only
    private String stripe_url = FrontendConstants.baseURL + "/stripe/checkout/"+ FrontendConstants.userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Enable dark mode
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        if (FrontendConstants.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // Following Stripe API Doc, but using StringRequest to request POST request instead of using Fuel
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, stripe_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("paymentIntent:", "success");
                    final JSONObject jsonResponse = new JSONObject(response);
                    customerConfig = new PaymentSheet.CustomerConfiguration(
                            jsonResponse.getString("customer"),
                            jsonResponse.getString("ephemeralKey")
                    );
                    paymentIntentClientSecret = jsonResponse.getString("paymentIntent");
                    Log.d("paymentIntent:", paymentIntentClientSecret);
                    PaymentConfiguration.init(getApplicationContext(), jsonResponse.getString("publishableKey"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Check your internet connection",Toast.LENGTH_LONG).show();
            }
        });
        // Stack StringRequest into Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Trigger start request button
        Button start_subscription = findViewById(R.id.start_subscription_button);
        start_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    requestQueue.add(stringRequest);
                    presentPaymentSheet();
                } catch (Exception e) {
                    // Server connection may timeout due to delay, which will cause null pointer error in presentPaymentSheet().
                    // Should be caught here
                    Toast.makeText(getApplicationContext(),"Server connection timeout, try again",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Provided by Stripe API Doc
    private void presentPaymentSheet() {
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Accountability-CPEN321")
                .customer(customerConfig)
                // Set `allowsDelayedPaymentMethods` to true if your business can handle payment methods
                // that complete payment after a delay, like SEPA Debit and Sofort.
                .allowsDelayedPaymentMethods(true).build();

        paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                configuration
        );
    }

    // Handle payment result
    void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        // User cancel from their end
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.d("Stripe","Canceled");
        }
        // Users' transaction is in error
        else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Log.d("App", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            new MaterialAlertDialogBuilder(this)
                    .setIcon(R.drawable.ic_subscription_24)
                    .setTitle("Sorry, we encountered in errors")
                    .setMessage((CharSequence) ((PaymentSheetResult.Failed) paymentSheetResult).getError())
                    .setNeutralButton("Try again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent subscriptionIntent = new Intent(SubscriptionActivity.this, SubscriptionActivity.class);
                            startActivity(subscriptionIntent);
                        }
                    })
                    .show();
        }
        // Payment success
        else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            startSubscription();
            Log.d("Stripe","Completed");
            new MaterialAlertDialogBuilder(this)
                    .setIcon(R.drawable.ic_subscription_24)
                    .setTitle("Congratulations!")
                    .setMessage("You have successfully subscribed our advanced service. Enjoy!")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent homeScreenIntent = new Intent(SubscriptionActivity.this, HomeScreenActivity.class);
                            startActivity(homeScreenIntent);
                        }
                    })
                    .show();
            FrontendConstants.is_subscribed = true;
        }
    }

    private void startSubscription() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/subscription/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        String subscriptionDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Calendar c= Calendar.getInstance();
        c.add(Calendar.DATE, 30);
        Date d=c.getTime();
        String expiryDate = new SimpleDateFormat("yyyy-MM-dd").format(d);
        Call<JsonObject> call = retrofitAPI.createSubscription(FrontendConstants.userID, subscriptionDate, expiryDate);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                Toast.makeText(getApplicationContext(),"You have successfully subscribed our advanced service",Toast.LENGTH_LONG).show();
                Log.d("Message",response.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Message","error");
            }
        });
    }
}