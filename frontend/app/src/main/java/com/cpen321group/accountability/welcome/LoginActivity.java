package com.cpen321group.accountability.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cpen321group.accountability.HomeScreenActivity;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.RetrofitAPI;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.color.DynamicColors;
import com.google.gson.JsonObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private String TAG="LoginActivity";
    private int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set Navigation Bar transparent
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (FrontendConstants.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        Intent settingsIntent = new Intent(LoginActivity.this, HomeScreenActivity.class);

        if(FrontendConstants.is_test == 1){
            startActivity(settingsIntent);
        }

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Handler h1 = new Handler();
                        h1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(Profile.getCurrentProfile()!=null) {
                                    authenticationFB(Profile.getCurrentProfile().getId()+"fb");
                                }
                            }
                        },3000);
                        Log.d(TAG,"SUCCESS!");
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        LoginButton login_fb = findViewById(R.id.login_button_fb);
        login_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Profile.getCurrentProfile()==null) {
                    signInFB();
                }else{
                    authenticationFB(Profile.getCurrentProfile().getId()+"fb");
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInFB(){
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            authentication(account.getIdToken(),account.getId()+"go");
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

    }

    private void updateUI(GoogleSignInAccount account) {
        if(account == null){
            Log.d(TAG,"No one signed in!");
        }else{
            Log.d(TAG,"Pref Name: "+account.getDisplayName());
            Log.d(TAG,"Email: "+account.getEmail());
            Log.d(TAG,"Given Name: "+account.getGivenName());
            Log.d(TAG,"Family Name: "+account.getFamilyName());
            Log.d(TAG,"URL: "+account.getPhotoUrl());
        }
    }

    private void authentication(String token,String id){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/accounts/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.checkAuth(id,token);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Log in",response.toString());
                if(response.code()==200) {
                    FrontendConstants.is_subscribed = false;
                    FrontendConstants.userID = id;
                    FrontendConstants.avatar = null;
                    FrontendConstants.isAccountant = response.body().get("isAccountant").getAsBoolean();
                    Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                }else if(response.code()==403) {
                    Toast.makeText(getApplicationContext(),"Have problem with Google account!",Toast.LENGTH_LONG).show();
                    signOut(mGoogleSignInClient);
                }else if(response.code()==404){
                    Toast.makeText(getApplicationContext(),"Have to register first",Toast.LENGTH_LONG).show();
                    signOut(mGoogleSignInClient);
                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Message","error");
                Toast.makeText(getApplicationContext(),"Try again",Toast.LENGTH_LONG).show();
                signOut(mGoogleSignInClient);
            }
        });
    }

    private void authenticationFB(String id){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/accounts/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.findAccount(id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Log in",response.toString());
                if(response.code()==200) {
                    FrontendConstants.is_subscribed = false;
                    FrontendConstants.userID = id;
                    FrontendConstants.avatar = null;
                    FrontendConstants.isAccountant = response.body().get("isAccountant").getAsBoolean();
                    Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                }else if(response.code()==403){
                    Toast.makeText(getApplicationContext(),"Have problem with account!",Toast.LENGTH_LONG).show();
                    LoginManager.getInstance().logOut();
                }else if(response.code()==404){
                    Toast.makeText(getApplicationContext(),"Have to register first",Toast.LENGTH_LONG).show();
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Message","error");
                Toast.makeText(getApplicationContext(),"Try again",Toast.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
            }
        });
    }

    //Google sign out
    private void signOut(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
}