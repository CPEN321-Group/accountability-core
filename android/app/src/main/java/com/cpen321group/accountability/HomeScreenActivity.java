package com.cpen321group.accountability;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.cpen321group.accountability.databinding.ActivityHomeScreenBinding;
import com.google.android.material.color.DynamicColors;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
        Toast.makeText(this, "If you want to sign out, use sign out button instead", Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set Navigation Bar transparent
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        ActivityHomeScreenBinding binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_chat, R.id.navigation_dashboard, R.id.navigation_profile)
//                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home_screen);
        // Uncomment it if action bar is required.
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (FrontendConstants.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(GoogleSignIn.getLastSignedInAccount(HomeScreenActivity.this)!=null){
                    GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(HomeScreenActivity.this);
                    FrontendConstants.userID = account.getId()+"go";
                }else if(Profile.getCurrentProfile()!=null){
                    Profile profile = Profile.getCurrentProfile();
                    FrontendConstants.userID = profile.getId()+"fb";
                }
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(FrontendConstants.baseURL + "/accounts/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();


                RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
                Call<JsonObject> call = retrofitAPI.getAccount(FrontendConstants.userID);

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.body() != null) {
                                Log.d("Account",response.body().toString());
                                FrontendConstants.isAccountant = (response.body().get("isAccountant").toString().equals("true"));
                                Log.d("Message", response.body().get("isAccountant").toString());
                                JsonElement jsonname = response.body().get("profile").getAsJsonObject().get("firstname");
                                if (jsonname != null) {
                                    String name = jsonname.toString();
                                    if (!name.equals("")) {
                                        FrontendConstants.userName = name.substring(1, name.length() - 1);
                                    }
                                }
//                                String avatar = response.body().get("profile").getAsJsonObject().get("avatar").getAsString();
//                                if(!avatar.equals(" ")) {
//                                    FrontendConstants.avatar = avatar;
//                                }
                                try {
                                    String date = response.body().get("subscription").getAsJsonObject().get("expiryDate").getAsString();
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    Date subDate = formatter.parse(date);
                                    Date a = new Date();
                                    int num = (int) ((subDate.getTime() - a.getTime()) / (1000 * 3600 * 24));
                                    if (num >= 0) {
                                        FrontendConstants.is_subscribed = true;
                                    }
                                }catch(Exception e){
                                    Log.d("Home",e.toString());
                                    FrontendConstants.is_subscribed = false;
                                }
                            }
                        }catch(Exception e){
                            Log.d("Home",e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d("Message","error");
                    }
                });
            }
        }, 1000);
    }
}