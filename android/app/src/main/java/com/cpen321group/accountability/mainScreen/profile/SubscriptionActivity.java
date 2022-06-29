package com.cpen321group.accountability.mainScreen.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cpen321group.accountability.MainActivity;
import com.cpen321group.accountability.R;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.google.android.material.color.DynamicColors;

import com.stripe.android.paymentsheet.*;
import com.stripe.android.PaymentConfiguration;
// Add the following lines to build.gradle to use this example's networking library:
//   implementation 'com.github.kittinunf.fuel:fuel:2.3.1'
//   implementation 'com.github.kittinunf.fuel:fuel-json:2.3.1'
import com.github.kittinunf.fuel.*;

import org.json.JSONException;
import org.json.JSONObject;

public class SubscriptionActivity extends AppCompatActivity {
    PaymentSheet paymentSheet;
    private String paymentClientSecret;
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

        Fuel.INSTANCE.post("Your backend endpoint/payment-sheet", null).responseString(new Handler<String>() {
            @Override
            public void success(String s) {
                try {
                    final JSONObject result = new JSONObject(s);
                    customerConfig = new PaymentSheet.CustomerConfiguration(
                            result.getString("customer"),
                            result.getString("ephemeralKey")
                    );
                    paymentClientSecret = result.getString("paymentClientSecret");
                    PaymentConfiguration.init(getApplicationContext(), result.getString("publishableKey"));
                } catch (JSONException e) { /* handle error */ }
            }

            @Override
            public void failure(@NonNull FuelError fuelError) { /* handle error */ }
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
                .allowsDelayedPaymentMethods(true)
        .build();
        paymentSheet.presentWithPaymentIntent(
                paymentClientSecret,
                configuration
        );
    }

    void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.d("","Canceled");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Log.e("App", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Display for example, an order confirmation screen
            Log.d("","Completed");
        }
    }


}