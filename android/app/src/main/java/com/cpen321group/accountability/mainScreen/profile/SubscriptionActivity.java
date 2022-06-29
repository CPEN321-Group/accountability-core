package com.cpen321group.accountability.mainScreen.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.cpen321group.accountability.MainActivity;
import com.cpen321group.accountability.R;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.google.android.material.color.DynamicColors;

import com.stripe.android.paymentsheet.*;
import com.stripe.android.PaymentConfiguration;
import com.github.kittinunf.fuel.*;

import org.json.JSONException;
import org.json.JSONObject;

public class SubscriptionActivity extends AppCompatActivity {
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration customerConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        if (MainActivity.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        Fuel.INSTANCE.post("http://165.232.129.241:8080/checkout", null).responseString(new Handler<String>() {
            @Override
            public void success(String s) {
                try {
                    final JSONObject result = new JSONObject(s);
                    customerConfig = new PaymentSheet.CustomerConfiguration(
                            result.getString("customer"),
                            result.getString("ephemeralKey")
                    );
                    paymentIntentClientSecret = result.getString("paymentIntent");
                    Log.d("paymentIntent:", paymentIntentClientSecret);
                    PaymentConfiguration.init(getApplicationContext(), result.getString("publishableKey"));
                } catch (JSONException e) { /* handle error */

                }
            }
            @Override
            public void failure(@NonNull FuelError fuelError) { /* handle error */
                Log.d("stripe err:", "error post");
            }
        });

        Button start_subscription = findViewById(R.id.start_subscription_button);
        start_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentPaymentSheet();
            }
        });
    }

    private void presentPaymentSheet() {
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Example, Inc.")
                .customer(customerConfig)
                // Set `allowsDelayedPaymentMethods` to true if your business can handle payment methods
                // that complete payment after a delay, like SEPA Debit and Sofort.
                .allowsDelayedPaymentMethods(true).build();

        paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                configuration
        );
    }

    void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.d("Stripe","Canceled");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Log.e("App", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Display for example, an order confirmation screen
            Log.d("Stripe","Completed");
        }
    }


}