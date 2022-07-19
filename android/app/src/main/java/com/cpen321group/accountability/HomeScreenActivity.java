package com.cpen321group.accountability;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cpen321group.accountability.databinding.ActivityHomeScreenBinding;
import com.google.android.material.color.DynamicColors;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_chat, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home_screen);
        // Uncomment it if action bar is required.
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (VariableStore.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(GoogleSignIn.getLastSignedInAccount(HomeScreenActivity.this)!=null){
                    GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(HomeScreenActivity.this);
                    VariableStore.userID = account.getId()+"go";
                }else{
                    Profile profile = Profile.getCurrentProfile();
                    VariableStore.userID = profile.getId()+"fb";
                }
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(VariableStore.baseURL + "/accounts/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();


                RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
                Call<JsonObject> call = retrofitAPI.getAccount(VariableStore.userID);

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.body() != null) {
                                VariableStore.isAccountant = (response.body().get("isAccountant").toString().equals("true"));
                                Log.d("Message", response.body().get("isAccountant").toString());
                                JsonElement jsonname = response.body().get("profile").getAsJsonObject().get("firstname");
                                if (jsonname != null) {
                                    String name = jsonname.toString();
                                    if (!name.equals("")) {
                                        VariableStore.userName = name.substring(1, name.length() - 1);
                                    }
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