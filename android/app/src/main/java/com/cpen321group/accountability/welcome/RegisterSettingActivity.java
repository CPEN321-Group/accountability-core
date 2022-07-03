package com.cpen321group.accountability.welcome;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.google.android.gms.common.util.CollectionUtils.listOf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.core.motion.utils.Utils;
import androidx.core.view.WindowCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cpen321group.accountability.MainActivity;
import com.cpen321group.accountability.R;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisterSettingActivity extends AppCompatActivity {
    private TextInputLayout inputText;
    private AutoCompleteTextView autoText;
    private ImageView avatar;
    private String TAG = "register";
    private String server_url = "http://localhost:8000/user/";
    private MyProfile myProfile_1;
    private String userId;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_setting);
        if (MainActivity.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        

        //change avatar
        Button changeButton = findViewById(R.id.btn_change);
        avatar = findViewById(R.id.iv_personal_icon);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
            }
        });


        //drop down menu
        inputText = findViewById(R.id.menu);
        autoText = findViewById(R.id.select_text);

        String[] items = {"User", "Accountant"};
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(RegisterSettingActivity.this, R.layout.list_item, items);
        autoText.setAdapter(itemAdapter);
        autoText.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                text = (String)parent.getItemAtPosition(position);
            }
        });



        //register button
        Button sign_out = findViewById(R.id.button_register);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUIFB();
                try {
                    queue.add(createAccount());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN) != null) {
                    GoogleSignInClient account = GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
                    signOut(account);
                    Log.d("Profile", "Google sign out successfully!");
                }
                if (Profile.getCurrentProfile() != null) {
                    LoginManager.getInstance().logOut();
                    Log.d("Profile", "Facebook sign out successfully!");
                }
                Intent settingsIntent = new Intent(RegisterSettingActivity.this, WelcomeActivity.class);
                startActivity(settingsIntent);
            }
        });
    }

    //Google sign out
    private void signOut(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(RegisterSettingActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    //choose picture
    private void choosePic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Avatar");
        String[] select_item = {"Choose from local"};
        builder.setNegativeButton("cancel", null);
        builder.setItems(select_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setType("image/*");
                startActivityForResult(openAlbumIntent, 0);
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                setImageToView(data);
            }
        }
    }

    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            avatar.setImageBitmap(photo);
        }
    }

    private void updateUIFB() {
        Profile profile = Profile.getCurrentProfile();
        if(profile == null){
            Log.d(TAG,"No one signed in!");
        }else{
            Log.d(TAG,"Pref Name: "+profile.getFirstName());
            Log.d(TAG,"Email: "+profile.getId());
            Log.d(TAG,"Given Name: "+profile.getFirstName());
            Log.d(TAG,"Family Name: "+profile.getLastName());
            Log.d(TAG,"URL: "+profile.getLinkUri());
        }
    }

    private JsonObjectRequest createAccount() throws JSONException {
        createProfile();
        final JSONObject jsonObject = new JSONObject();
        final JSONObject jsonProfile = new JSONObject();
        try {
            jsonProfile.put("firstName",myProfile_1.getFirstName());
            jsonProfile.put("lastName",myProfile_1.getLastName());
            jsonProfile.put("e_mail",myProfile_1.getE_mail());
            jsonProfile.put("age",myProfile_1.getAge());
            jsonProfile.put("profession",myProfile_1.getProfession());
            jsonObject.put("profile", jsonProfile);
            jsonObject.put("isAccountant", (text=="Accountant"));
            jsonObject.put("isAuthenticated", 0);
            jsonObject.put("authenticateExpiryDate",null);
            jsonObject.put("hasAccountant",0);
        } catch (JSONException e) {
            // handle exception
        }
        //Log.d(TAG,jsonObject.getJSONObject("profile").getString("firstName"));
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, server_url+userId, jsonObject,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {

                try {
                    Log.d("json", jsonObject.toString());
                    return jsonObject.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        return putRequest;

    }

    private void createProfile(){
        myProfile_1 = new MyProfile("","","",20,"student");
        if(GoogleSignIn.getLastSignedInAccount(RegisterSettingActivity.this)!=null){
            GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(RegisterSettingActivity.this);
            myProfile_1.setFirstName(account.getGivenName());
            myProfile_1.setLastName(account.getFamilyName());
            myProfile_1.setE_mail(account.getEmail());
            userId = account.getId()+"go";
        }else{
            Profile profile = Profile.getCurrentProfile();
            myProfile_1.setFirstName(profile.getFirstName());
            myProfile_1.setLastName(profile.getLastName());
            myProfile_1.setE_mail("");
            userId = profile.getId()+"fb";
        }
    }
}


